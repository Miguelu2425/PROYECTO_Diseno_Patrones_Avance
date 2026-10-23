package patrones.creacionales.builder;

/**
 * Builder: representa un expediente construido paso a paso.
 */
public class Expediente {

    private String codigo;
    private String estado;
    private String tipoDocumento;

    /**
     * Builder: construye el producto final usando los valores acumulados.
     */
    private Expediente(ExpedienteBuilder builder) {
        this.codigo = builder.codigo;
        this.estado = builder.estado;
        this.tipoDocumento = builder.tipoDocumento;
    }

    /**
     * Builder: concentra la configuración opcional del expediente.
     */
    public static class ExpedienteBuilder {

        private String codigo;
        private String estado;
        private String tipoDocumento;

        /**
         * Builder: configura el código y devuelve el mismo constructor.
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
         * Builder: ensambla y devuelve un expediente completo.
         */
        public Expediente build() {
            return new Expediente(this);
        }
    }

    /**
     * Producto Builder: muestra la información del expediente construido.
     */
    public void mostrarDetalle() {
        System.out.println("Expediente: " + codigo + "| Estado: " + estado);
    }
}
