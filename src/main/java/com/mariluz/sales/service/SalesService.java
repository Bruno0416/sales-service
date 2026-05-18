package com.mariluz.sales.service;

import com.mariluz.sales.dto.*;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface SalesService {
    // 1. generar venta
    public SaleResponse createSale(SaleRequest request);
    // 2. buscar venta por saleId (usuario)
    public SaleResponse getSaleById(Integer id);
    // 3. ver estado venta
    public SaleStatusResponse getStatusBySaleId(Integer saleId);
    // 4. listar ventas (admin)
    public List<SaleResponse> getAllSales();
    // 5. buscar mis compras (por usuario)
    // 6. cancelar venta(idVenta)
}
