package com.cuidadoeterno.backend.modules.finanzas.model;

import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla: cuenta_banco
 * Almacena los datos bancarios de los cuidadores para gestionar 
 * la transferencia de los fondos recaudados por sus servicios prestados.
 */
@Entity
@Table(name = "cuenta_banco")
@Getter
@Setter
@NoArgsConstructor
public class CuentaBanco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuenta;

    /**
     * FK → CUIDADOR.id_persona
     * Identifica al cuidador dueño de la cuenta de destino.
     * LAZY porque no siempre se requieren los detalles del cuidador al consultar la cuenta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Cuidador cuidador;

    /**
     * FK → TIPO_CUENTA.id_tipo_cuenta
     * Clasifica el tipo de cuenta (Ej: Corriente, Vista/RUT, Ahorro).
     * LAZY para optimizar las consultas financieras.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;

    /**
     * Nombre de la institución bancaria (Ej: Banco Estado, BCI, Santander).
     */
    @Column(name = "banco", nullable = false, length = 100)
    private String banco;

    @Column(name = "numero_cuenta", nullable = false, length = 30)
    private String numeroCuenta;

    /**
     * RUT del titular de la cuenta con formato (Ej: 12.345.678-9 o 12345678-9).
     */
    @Column(name = "rut_titular", nullable = false, length = 12)
    private String rutTitular;

    @Column(name = "nombre_titular", nullable = false, length = 150)
    private String nombreTitular;

    /**
     * Control de vigencia de la cuenta: 1 = Activa (disponible para abonos), 0 = Inactiva
     */
    @Column(name = "estado_activo", nullable = false)
    private Boolean estadoActivo = true;
}