package com.mariluz.sales.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "sale")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp // para dejar registro fijo de la fecha de compra
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ---> Relacion direccion de envio (conectado a ms usuario)

    // relacion con SaleItem
    @OneToMany(
        mappedBy = "sale",
        cascade = CascadeType.ALL, // cascade para manejar la persistencia de SaleItem
        orphanRemoval = true // orphanRemoval para eliminar SaleItem cuando se elimine la venta automaticamente
    )
    @Builder.Default // para evitar que el deje sin inicializar la lista
    private List<SaleItem> items = new ArrayList<>(); // inicializar la lista para evitar NullPointerException

    public void addItem(final SaleItem item) {
        this.items.add(item);
        item.setSale(this);
    }

    public void addItems(List<SaleItem> newItems) {
        this.items = newItems;
        if (newItems != null) {
            newItems.forEach(item -> item.setSale(this));
        }
    }
}
