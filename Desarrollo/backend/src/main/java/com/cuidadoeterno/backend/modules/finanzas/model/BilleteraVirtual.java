package com.cuidadoeterno.backend.modules.finanzas.model;

import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billetera_virtual")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilleteraVirtual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_billetera")
    private Integer idBilletera;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuidador", nullable = false, unique = true)
    private Cuidador cuidador;

    @Column(name = "saldo_disponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
}
