package com.mariluz.sales.service;

import com.mariluz.sales.client.CatalogClient;
import com.mariluz.sales.client.NotificationClient;
import com.mariluz.sales.client.ShippingClient;
import com.mariluz.sales.dto.*;
import com.mariluz.sales.dto.catalog.*;
import com.mariluz.sales.exceptions.CouldNotCancelSaleException;
import com.mariluz.sales.exceptions.DuplicateProductException;
import com.mariluz.sales.exceptions.InsufficientStockException;
import com.mariluz.sales.exceptions.ProductsNotFoundException;
import com.mariluz.sales.exceptions.SaleNotFoundException;
import com.mariluz.sales.exceptions.UnauthenticatedException;
import com.mariluz.sales.exceptions.UnauthorizedOperationException;
import com.mariluz.sales.mapper.SaleMapper;
import com.mariluz.sales.model.Sale;
import com.mariluz.sales.model.SaleItem;
import com.mariluz.sales.model.Status;
import com.mariluz.sales.model.User;
import com.mariluz.sales.repository.SalesRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final CatalogClient catalogClient;
    private final ShippingClient shippingClient;
    private final NotificationClient notificationClient;
    private final SalesRepository repo;
    private final SaleMapper saleMapper;

    private User getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new UnauthenticatedException("No hay un usuario autenticado");
        }
        return user;
    }

    private void validateAdminAccess(String message) {
        User user = getCurrentUser();
        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            throw new UnauthorizedOperationException(message);
        }
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        String authHeader = (
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        )
            .getRequest()
            .getHeader("Authorization");

        User user = getCurrentUser();

        // 1. mapear lista de ids
        List<Integer> ids = request
            .getProducts()
            .stream()
            .map(p -> p.getId())
            .toList();

        // validar productos duplicados en el request
        if (ids.size() != ids.stream().distinct().count()) {
            throw new DuplicateProductException();
        }

        // 2. validar productos (restClient)
        List<ProductResponse> products = catalogClient
            .findProducts(ids, authHeader)
            .getProducts();

        Map<SaleItemRequest, ProductResponse> validatedItemsMap =
            new LinkedHashMap<>();

        for (SaleItemRequest itemRequest : request.getProducts()) {
            ProductResponse productInCatalog = products
                .stream()
                .filter(p -> p.getId().equals(itemRequest.getId()))
                .findFirst()
                .orElseThrow(() ->
                    new ProductsNotFoundException(
                        "Producto no encontrado: " + itemRequest.getId()
                    )
                );

            if (productInCatalog.getQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                    "Stock insuficiente para el producto: id=" +
                        itemRequest.getId()
                );
            }

            validatedItemsMap.put(itemRequest, productInCatalog);
        }

        // 3. calcular total
        Integer total = validatedItemsMap
            .entrySet()
            .stream()
            .mapToInt(p -> p.getKey().getQuantity() * p.getValue().getPrice())
            .sum();

        List<SaleItem> items = new ArrayList<>();
        List<String> productsNoti = new ArrayList<>();

        // 4. construir items y lista de notificacion
        validatedItemsMap.forEach((item, p) -> {
            items.add(
                SaleItem.builder()
                    .productId(p.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(p.getPrice())
                    .subTotal(item.getQuantity() * p.getPrice())
                    .build()
            );
            productsNoti.add(p.getName() + " - " + item.getQuantity());
        });

        // 5. guardar venta
        Sale sale = Sale.builder()
            .userId(user.getId())
            .total(total)
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .build();

        sale.addItems(items);
        repo.save(sale);

        // 6. crear orden de envio
        shippingClient.createShippingOrder(sale.getId(), authHeader);

        // 7. enviar notificacion
        try {
            notificationClient.sendPurchaseEmail(
                user.getEmail(),
                "Confirmacion de tu compra",
                productsNoti
            );
        } catch (Exception e) {
            // no se interrumpe el flujo si la notificacion falla pero se imprime el error
            System.out.println(
                "Error al enviar notificacion: " + e.getMessage()
            );
        }

        /*
            NOTA:
            8. Actualizamos el stock despues de crear la orden de envio de modo que si hay un error en cualquiera
            de los pasos anteriores el @Transactional cancele la operacion y no se llegue a actualizar el stock.
        */
        validatedItemsMap.forEach((item, p) ->
            catalogClient.updateStock(p.getId(), item.getQuantity(), authHeader)
        );
        // 9. retornar respuesta
        return saleMapper.toResponse(sale);
    }

    @Override
    public SaleResponse getSaleById(Integer id) {
        // 1. obtener usuario
        User user = getCurrentUser();
        // 2. obtener venta por id y usuario
        Sale sale = repo
            .findByIdAndUserId(id, user.getId())
            .orElseThrow(SaleNotFoundException::new);

        // 3. construir respuesta
        return saleMapper.toResponse(sale);
    }

    @Override
    public SaleStatusResponse getStatusBySaleId(Integer saleId) {
        // 1. obtener usuario
        User user = getCurrentUser();
        // 2. obtener estado de la venta y validar que pertenece al usuario al retornar
        return saleMapper.toStatusResponse(
            repo
                .findStatusByIdAndUserId(saleId, user.getId())
                .orElseThrow(SaleNotFoundException::new)
        );
    }

    @Override
    public List<SaleResponse> getAllSales() {
        // 1. validar usuario administrador
        validateAdminAccess(
            "Solo un administrador puede acceder a todas las ventas"
        );

        // 2. buscar y retornar ventas
        return saleMapper.toResponseList(repo.findAll());
    }

    @Override
    public List<SaleResponse> getSalesByUserId() {
        // 1. obtener usuario
        User user = getCurrentUser();

        // 2. buscar y retornar ventas
        return saleMapper.toResponseList(repo.findByUserId(user.getId()));
    }

    @Override
    @Transactional
    public void cancelSale(Integer saleId) {
        // 1. obtener authHeader
        String authHeader = (
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        )
            .getRequest()
            .getHeader("Authorization");
        // 2. obtener usuario
        User user = getCurrentUser();

        // 3. buscar venta del usuario
        Sale sale = repo
            .findByIdAndUserId(saleId, user.getId())
            .orElseThrow(SaleNotFoundException::new);

        // 4. validar que la venta no este ya cancelada
        if (sale.getStatus() == Status.CANCELLED) {
            throw new CouldNotCancelSaleException(
                "La venta ya se encuentra cancelada."
            );
        }

        // 5. cancelar la orden de envío
        shippingClient.cancelShippingOrder(saleId, authHeader);

        // 6. actualizar estado de la venta a CANCELLED
        sale.setStatus(Status.CANCELLED);
        repo.save(sale);
    }
}
