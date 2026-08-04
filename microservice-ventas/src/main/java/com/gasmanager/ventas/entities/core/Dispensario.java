package com.gasmanager.ventas.entities.core;

import com.gasmanager.ventas.enums.EstadoDispensarioEnum;
import com.gasmanager.ventas.enums.TipoCombustibleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispensarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispensario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 10)
    private String numero;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", length = 30)
    private TipoCombustibleEnum tipoCombustible;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoDispensarioEnum estado;

    @Column(name = "mangueras")
    private Integer mangueras;

    @Column(name = "lectura_inicial", precision = 10, scale = 3)
    private BigDecimal lecturaInicial;

    @Column(name = "lectura_actual", precision = 10, scale = 3)
    private BigDecimal lecturaActual;

    @Column(name = "activo")
    private boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "dispensario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"dispensario", "hibernateLazyInitializer"})
    private List<CaraDispensario> caras = new ArrayList<>();

    @Column(name = "tiene_dos_caras")
    private Boolean tieneDosCaras = true;

    @Column(name = "despachador_id")
    private Long despachadorId;

    @Column(name = "despachador_nombre", length = 100)
    private String despachadorNombre;

    public void addCara(CaraDispensario cara) {
        caras.add(cara);
        cara.setDispensario(this);
    }
}