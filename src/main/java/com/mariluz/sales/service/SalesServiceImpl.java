package com.mariluz.sales.service;

import com.mariluz.sales.client.CatalogClient;
import com.mariluz.sales.dto.SaleItemRequest;
import com.mariluz.sales.dto.SaleItemResponse;
import com.mariluz.sales.dto.SaleRequest;
import com.mariluz.sales.dto.SaleResponse;
import com.mariluz.sales.dto.catalog.ProductResponse;
import com.mariluz.sales.exceptions.UnauthorizedOperationException;
import com.mariluz.sales.model.Sale;
import com.mariluz.sales.model.SaleItem;
import com.mariluz.sales.model.Status;
import com.mariluz.sales.model.User;
import com.mariluz.sales.repository.SalesRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final CatalogClient catalogClient;

    private final SalesRepository repo;

    private User getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new UnauthorizedOperationException(
                "No hay un usuario autenticado"
            );
        }

        return user;
    }

    private void validateAdminAccess(String message) {
        User user = getCurrentUser();

        if (!user.getRole().equalsIgnoreCase("ADMIN")) {
            // si el usuario no es admin arrojamos un error
            throw new UnauthorizedOperationException(message);
        }
    }

    @Override
    public SaleResponse createSale(SaleRequest request) {
        // extraer token para comunicarse con el catalogo y obtener usuario
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

        // 2. ---> validar productos (restClient) <---

        // 2.1 obtener productos del catalogo
        List<ProductResponse> products = catalogClient
            .findProducts(ids, authHeader)
            .getProducts();

        // 2.2 crear map de productos validados
        Map<SaleItemRequest, ProductResponse> validatedItemsMap =
            new HashMap<>();

        for (SaleItemRequest itemRequest : request.getProducts()) {
            // 2.3 Buscamos el producto en la lista en lugar de en un mapa
            ProductResponse productInCatalog =
                products
                    .stream()
                    .filter(p -> p.getId().equals(itemRequest.getId()))
                    //aplicamos filtro al stream para encontrar el producto por id
                    .findFirst()
                    // si el primer elemento de la lista filtrada no existe, lanzamos una excepcion (no existe el producto)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "Producto no encontrado: " + itemRequest.getId()
                        )
                    );

            // 2.4 Validamos el stock
            if (productInCatalog.getQuantity() < itemRequest.getQuantity()) {
                // si el stock del producto es menor que la cantidad solicitada lanzamos una excepcion
                throw new IllegalStateException(
                    "Stock insuficiente para el producto: " +
                        itemRequest.getId()
                );
            }

            // Guardamos en el mapa final
            validatedItemsMap.put(itemRequest, productInCatalog);
        }

        // 3. calcular total
        Integer total = validatedItemsMap
            .entrySet()
            .stream()
            .mapToInt(p -> p.getKey().getQuantity() * p.getValue().getPrice())
            .sum();

        System.out.println(total);
        // iniciar lista de items de venta
        List<SaleItem> items = new ArrayList<>();

        // 4. actualizar stock
        validatedItemsMap.forEach((item, p) -> {
            // por cada item actualizamos stock
            catalogClient.updateStock(
                p.getId(),
                item.getQuantity(),
                authHeader
            );
            // y vamos agregando los items a una lista (construyendo el objeto)
            items.add(
                SaleItem.builder()
                    .productId(p.getId())
                    .quantity(item.getQuantity())
                    .unitPrice(p.getPrice())
                    .subTotal(item.getQuantity() * p.getPrice())
                    .build()
            );
        });
        // 5. guardar venta
        Sale sale = Sale.builder()
            .userId(user.getId())
            .items(items)
            .total(
                items
                    .stream()
                    .mapToInt(i -> i.getSubTotal())
                    .sum()
            )
            .status(Status.COMPLETED)
            .createdAt(LocalDateTime.now())
            .build();

        sale.addItems(items);
        repo.save(sale);

        // 6. mandar notificacion con los productos comprados --> opcional por ahora
        // 7. retornar respuesta
        return SaleResponse.builder()
            .id(sale.getId())
            .total(sale.getTotal())
            .status(sale.getStatus())
            .createdAt(sale.getCreatedAt())
            .products(
                sale
                    .getItems()
                    .stream()
                    .map(i ->
                        SaleItemResponse.builder()
                            .id(i.getId())
                            .quantity(i.getQuantity())
                            .subTotal(i.getSubTotal())
                            .build()
                    )
                    .toList()
            )
            .build();
    }
}
