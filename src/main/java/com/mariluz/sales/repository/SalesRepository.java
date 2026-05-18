package com.mariluz.sales.repository;

import com.mariluz.sales.model.Sale;
import com.mariluz.sales.model.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<Sale, Integer> {
    public Optional<Sale> findByIdAndUserId(Integer id, String userId); // metodo para buscar una venta por su ID y el ID del usuario que la creó

    @Query(
        "SELECT s.status FROM sale s WHERE s.id = :id AND s.userId = :userId" // query para asegurar que solo retorna el status de la venta
    )
    public Optional<Status> findStatusByIdAndUserId(
        @Param("id") Integer id,
        @Param("userId") String userId
    ); // metodo para buscar el estado de una venta por su ID y el ID del usuario que la creó
}
