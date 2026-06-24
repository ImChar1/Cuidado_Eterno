package com.cuidadoeterno.backend.modules.servicio.model.enums;

public enum SubEstadoOrden {
    SIN_ASIGNAR,          // Estado por defecto inicial (Orden Pendiente)
    ASIGNADO,             // Cuidador aceptó, preparándose para salir
    COMPRANDO_INSUMOS,    // Retirando/Comprando productos en el puesto de venta
    EN_CAMINO,            // Desplazándose hacia el cementerio/sepultura
    EN_SITIO,             // Llegó al espacio físico del difunto
    TRABAJO_EN_PROCESO    // Realizando la limpieza/jardinería (subiendo evidencias)
}