package com.mariluz.sales.repository;

import com.mariluz.sales.model.Sale;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sale, Integer> {
    public Optional<Sale> findByIdAndUserId(int id, String userId); // Funcion para buscar una venta por su ID y el ID del usuario que la creó
}
