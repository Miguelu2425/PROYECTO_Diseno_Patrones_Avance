package patrones.creacionales.builder;

/**
 * Builder: representa un expediente construido paso a paso.
 */
public class Expediente {

    private String codigo;
    private String estado;
    private String tipoDocumento;
    private String dni;
    private String nombreCiudadano;
    private String fechaRegistro;

    /**
     * Builder: construye el producto final usando los valores acumulados.
     */
    private Expediente(ExpedienteBuilder builder) {
        this.codigo = builder.codigo;
        this.estado = builder.estado;
        this.tipoDocumento = builder.tipoDocumento;
        this.dni = builder.dni;
        this.nombreCiudadano = builder.nombreCiudadano;
        this.fechaRegistro = builder.fechaRegistro;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEstado() {
        return estado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getDni() {
        return dni;
    }

    public String getNombreCiudadano() {
        return nombreCiudadano;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Builder: concentra la configuracion opcional del expediente.
     */
    public static class ExpedienteBuilder {

        private String codigo;
        private String estado;
        private String tipoDocumento;
        private String dni;
        private String nombreCiudadano;
        private String fechaRegistro;

        /**
         * Builder: configura el codigo y devuelve el mismo constructor.
         */
        public ExpedienteBuilder setCodigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        /**
         * Builder: configura el estado y devuelve el mismo constructor.
         */
        public ExpedienteBuilder setEstado(String estado) {
            this.estado = estado;
            return this;
        }

        /**
         * Builder: configura el tipo documental y devuelve el mismo constructor.
         */
        public ExpedienteBuilder setTipoDocumento(String tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
            return this;
        }

        /**
         * Builder: configura el DNI del ciudadano.
         */
        public ExpedienteBuilder setDni(String dni) {
            this.dni = dni;
            return this;
        }

        /**
         * Builder: configura el nombre del ciudadano.
         */
        public ExpedienteBuilder setNombreCiudadano(String nombreCiudadano) {
            this.nombreCiudadano = nombreCiudadano;
            return this;
        }

        /**
         * Builder: configura la fecha de registro.
         */
        public ExpedienteBuilder setFechaRegistro(String fechaRegistro) {
            this.fechaRegistro = fechaRegistro;
            return this;
        }

        /**
         * Builder: ensambla y devuelve un expediente completo.
         */
        public Expediente build() {
            return new Expediente(this);
        }
    }

    /**
     * Producto Builder: muestra la informacion del expediente construido.
     */
    public void mostrarDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expediente: ").append(codigo).append(" | Estado: ").append(estado);
        if (tipoDocumento != null) {
            sb.append(" | Tipo: ").append(tipoDocumento);
        }
        if (dni != null) {
            sb.append(" | DNI: ").append(dni);
        }
        if (nombreCiudadano != null) {
            sb.append(" | Ciudadano: ").append(nombreCiudadano);
        }
        System.out.println(sb.toString());
    }
}