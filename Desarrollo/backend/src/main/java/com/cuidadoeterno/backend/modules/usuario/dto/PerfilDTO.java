package com.cuidadoeterno.backend.modules.usuario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida para GET /auth/perfil y GET /auth/perfil/{id}
 *
 * Cubre los 3 roles del sistema en un solo objeto.
 * Los campos específicos de cada rol son null cuando no aplican,
 * y @JsonInclude(NON_NULL) evita que lleguen al cliente Android.
 *
 * Ejemplo respuesta CLIENTE:
 * {
 *   "idPersona": 1, "nombre": "Juan", "apPaterno": "Pérez",
 *   "email": "juan@email.com", "rol": "CLIENTE",
 *   "estadoCliente": "activo", "prefNotificacion": "push"
 *   // campos de CUIDADOR y ADMINISTRADOR ausentes
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilDTO {

    // ── Datos comunes (PERSONA) ─────────────────────────────────────────────────
    private final Integer idPersona;
    private final String nombre;
    private final String apPaterno;
    private final String apMaterno;
    private final String email;
    private final String telefono;
    private final LocalDate fechaNacimiento;
    private final String genero;

    // ── Datos de CREDENCIAL ─────────────────────────────────────────────────────
    private final String rol;
    private final String nombreUsuario;
    private final String estadoCuenta;

    // ── Exclusivo CLIENTE ───────────────────────────────────────────────────────
    private final LocalDate fechaRegistro;
    private final String prefNotificacion;
    private final String estadoCliente;

    // ── Exclusivo CUIDADOR ──────────────────────────────────────────────────────
    private final BigDecimal calificacionPromedio;
    private final String estadoVerificacion;
    private final String estadoDisponibilidad;
    private final LocalDate fechaIngresoCuidador;

    // ── Exclusivo ADMINISTRADOR ─────────────────────────────────────────────────
    private final String nivelAcceso;
    private final String cargo;
    private final LocalDate fechaIngresoAdmin;

    private PerfilDTO(Builder b) {
        this.idPersona             = b.idPersona;
        this.nombre                = b.nombre;
        this.apPaterno             = b.apPaterno;
        this.apMaterno             = b.apMaterno;
        this.email                 = b.email;
        this.telefono              = b.telefono;
        this.fechaNacimiento       = b.fechaNacimiento;
        this.genero                = b.genero;
        this.rol                   = b.rol;
        this.nombreUsuario         = b.nombreUsuario;
        this.estadoCuenta          = b.estadoCuenta;
        this.fechaRegistro         = b.fechaRegistro;
        this.prefNotificacion      = b.prefNotificacion;
        this.estadoCliente         = b.estadoCliente;
        this.calificacionPromedio  = b.calificacionPromedio;
        this.estadoVerificacion    = b.estadoVerificacion;
        this.estadoDisponibilidad  = b.estadoDisponibilidad;
        this.fechaIngresoCuidador  = b.fechaIngresoCuidador;
        this.nivelAcceso           = b.nivelAcceso;
        this.cargo                 = b.cargo;
        this.fechaIngresoAdmin     = b.fechaIngresoAdmin;
    }

    public static Builder builder() { return new Builder(); }

    // ── Getters ─────────────────────────────────────────────────────────────────
    public Integer getIdPersona()              { return idPersona; }
    public String getNombre()                  { return nombre; }
    public String getApPaterno()               { return apPaterno; }
    public String getApMaterno()               { return apMaterno; }
    public String getEmail()                   { return email; }
    public String getTelefono()                { return telefono; }
    public LocalDate getFechaNacimiento()      { return fechaNacimiento; }
    public String getGenero()                  { return genero; }
    public String getRol()                     { return rol; }
    public String getNombreUsuario()           { return nombreUsuario; }
    public String getEstadoCuenta()            { return estadoCuenta; }
    public LocalDate getFechaRegistro()        { return fechaRegistro; }
    public String getPrefNotificacion()        { return prefNotificacion; }
    public String getEstadoCliente()           { return estadoCliente; }
    public BigDecimal getCalificacionPromedio(){ return calificacionPromedio; }
    public String getEstadoVerificacion()      { return estadoVerificacion; }
    public String getEstadoDisponibilidad()    { return estadoDisponibilidad; }
    public LocalDate getFechaIngresoCuidador() { return fechaIngresoCuidador; }
    public String getNivelAcceso()             { return nivelAcceso; }
    public String getCargo()                   { return cargo; }
    public LocalDate getFechaIngresoAdmin()    { return fechaIngresoAdmin; }

    // ── Builder ─────────────────────────────────────────────────────────────────
    public static class Builder {
        private Integer idPersona;
        private String nombre, apPaterno, apMaterno, email, telefono, genero;
        private LocalDate fechaNacimiento;
        private String rol, nombreUsuario, estadoCuenta;
        private LocalDate fechaRegistro;
        private String prefNotificacion, estadoCliente;
        private BigDecimal calificacionPromedio;
        private String estadoVerificacion, estadoDisponibilidad;
        private LocalDate fechaIngresoCuidador;
        private String nivelAcceso, cargo;
        private LocalDate fechaIngresoAdmin;

        public Builder idPersona(Integer v)           { this.idPersona = v; return this; }
        public Builder nombre(String v)               { this.nombre = v; return this; }
        public Builder apPaterno(String v)            { this.apPaterno = v; return this; }
        public Builder apMaterno(String v)            { this.apMaterno = v; return this; }
        public Builder email(String v)                { this.email = v; return this; }
        public Builder telefono(String v)             { this.telefono = v; return this; }
        public Builder fechaNacimiento(LocalDate v)   { this.fechaNacimiento = v; return this; }
        public Builder genero(String v)               { this.genero = v; return this; }
        public Builder rol(String v)                  { this.rol = v; return this; }
        public Builder nombreUsuario(String v)        { this.nombreUsuario = v; return this; }
        public Builder estadoCuenta(String v)         { this.estadoCuenta = v; return this; }
        public Builder fechaRegistro(LocalDate v)     { this.fechaRegistro = v; return this; }
        public Builder prefNotificacion(String v)     { this.prefNotificacion = v; return this; }
        public Builder estadoCliente(String v)        { this.estadoCliente = v; return this; }
        public Builder calificacionPromedio(BigDecimal v){ this.calificacionPromedio = v; return this; }
        public Builder estadoVerificacion(String v)   { this.estadoVerificacion = v; return this; }
        public Builder estadoDisponibilidad(String v) { this.estadoDisponibilidad = v; return this; }
        public Builder fechaIngresoCuidador(LocalDate v){ this.fechaIngresoCuidador = v; return this; }
        public Builder nivelAcceso(String v)          { this.nivelAcceso = v; return this; }
        public Builder cargo(String v)                { this.cargo = v; return this; }
        public Builder fechaIngresoAdmin(LocalDate v) { this.fechaIngresoAdmin = v; return this; }

        public PerfilDTO build()                      { return new PerfilDTO(this); }
    }
}