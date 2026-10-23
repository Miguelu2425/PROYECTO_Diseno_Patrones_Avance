package vistas;

import controladores.AtencionController;
import patrones.creacionales.abstractfactory.FabricaAtencion;
import patrones.creacionales.abstractfactory.FabricaReclamo;
import patrones.creacionales.abstractfactory.FabricaSolicitud;
import patrones.creacionales.builder.Expediente;
import patrones.creacionales.factory.TramiteFactory;
import patrones.creacionales.prototype.EncuestaSatisfaccion;
import patrones.creacionales.singleton.ConfiguracionPlataforma;
import patrones.creacionales.productos.Tramite;
import patrones.estructurales.adapter.AdaptadorSistemaAntiguo;
import patrones.estructurales.bridge.AlertaCiudadano;
import patrones.estructurales.bridge.CanalCorreo;
import patrones.estructurales.bridge.CanalSMS;
import patrones.estructurales.composite.GrupoTramites;
import patrones.estructurales.decorator.Notificador;
import patrones.estructurales.decorator.NotificadorBase;
import patrones.estructurales.decorator.NotificadorSMS;
import patrones.estructurales.proxy.TramiteProxy;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import patrones.creacionales.singleton.ConexionSingleton;

/**
 * Vista Swing: permite interactuar con la plataforma y demuestra los patrones.
 */
public class Main extends JFrame {

    private final JTextField dniField = new JTextField();
    private final JTextField nombreField = new JTextField();
    private final JTextField correoField = new JTextField();
    private final JComboBox<String> tramiteCombo =
            new JComboBox<>(new String[]{"reclamo", "solicitud"});
    private final JTextArea resultadoArea = new JTextArea();
    private final AtencionController controlador = new AtencionController();

    /**
     * Vista: construye la ventana interactiva y sus controles Swing.
     */
    public Main() {
        ConfiguracionPlataforma config = ConfiguracionPlataforma.getInstancia();

        setTitle("Mesa de Partes Virtual - " + config.getNombreMunicipio());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(new Color(25, 72, 122));
        encabezado.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titulo = new JLabel("Plataforma Integral de Atención Ciudadana");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        encabezado.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel(config.getNombreMunicipio());
        subtitulo.setForeground(new Color(220, 235, 250));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        encabezado.add(subtitulo, BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new GridLayout(1, 2, 16, 0));
        contenido.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contenido.add(crearFormulario());
        contenido.add(crearPanelResultados());
        add(contenido, BorderLayout.CENTER);
    }

    /**
     * Vista: crea el formulario de ingreso de datos ciudadanos.
     */
    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.weightx = 1;

        JLabel titulo = new JLabel("Registrar nuevo trámite");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(titulo, gbc);

        agregarCampo(formulario, gbc, 1, "DNI:", dniField);
        agregarCampo(formulario, gbc, 2, "Nombre completo:", nombreField);
        agregarCampo(formulario, gbc, 3, "Correo electrónico:", correoField);
        agregarCampo(formulario, gbc, 4, "Tipo de trámite:", tramiteCombo);

        JButton registrar = new JButton("Registrar trámite");
        registrar.setBackground(new Color(25, 118, 210));
        registrar.setForeground(Color.WHITE);
        registrar.setFocusPainted(false);
        registrar.addActionListener(event -> registrarTramite());
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 7, 5);
        formulario.add(registrar, gbc);

        JButton limpiar = new JButton("Limpiar formulario");
        limpiar.addActionListener(event -> limpiarFormulario());
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 5, 7, 5);
        formulario.add(limpiar, gbc);

        return formulario;
    }

    /**
     * Vista: crea el área donde se muestran los resultados de la operación.
     */
    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titulo = new JLabel("Actividad del sistema");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        panel.add(titulo, BorderLayout.NORTH);

        resultadoArea.setEditable(false);
        resultadoArea.setLineWrap(true);
        resultadoArea.setWrapStyleWord(true);
        resultadoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultadoArea.setText("Complete el formulario para registrar un trámite.");
        panel.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Vista: agrega una etiqueta y su control al formulario.
     */
    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
                              String etiqueta, java.awt.Component campo) {
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    /**
     * Vista: valida la entrada y solicita al controlador registrar el trámite.
     */
    private void registrarTramite() {
        String dni = dniField.getText().trim();
        String nombre = nombreField.getText().trim();
        String correo = correoField.getText().trim();
        String tramite = ((String) tramiteCombo.getSelectedItem()).trim();

        if (dni.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Complete todos los campos antes de registrar.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this,
                    "El DNI debe contener exactamente 8 dígitos.",
                    "DNI inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!correo.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un correo electrónico válido.",
                    "Correo inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controlador.atenderCiudadano(dni, nombre, correo, tramite);
        resultadoArea.setText("TRÁMITE REGISTRADO CORRECTAMENTE\n\n"
                + "Ciudadano: " + nombre + "\n"
                + "DNI: " + dni + "\n"
                + "Correo: " + correo + "\n"
                + "Tipo: " + tramite.toUpperCase() + "\n\n"
                + "El expediente fue creado y enviado a revisión.");
        JOptionPane.showMessageDialog(this,
                "El trámite fue registrado correctamente.",
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Vista: restablece los controles para permitir un nuevo registro.
     */
    private void limpiarFormulario() {
        dniField.setText("");
        nombreField.setText("");
        correoField.setText("");
        tramiteCombo.setSelectedIndex(0);
        resultadoArea.setText("Complete el formulario para registrar un trámite.");
    }

    /**
     * Demostración: ejecuta una vez cada patrón solicitado por el curso.
     */
    private static void demostrarPatrones() {
        ConfiguracionPlataforma.getInstancia();
        EncuestaSatisfaccion prototipo = new EncuestaSatisfaccion();
        prototipo.clone().enviarEncuesta();
        TramiteFactory.crearTramite("reclamo").procesar();

        FabricaAtencion fabricaSolicitud = new FabricaSolicitud();
        fabricaSolicitud.crearTramite().procesar();
        fabricaSolicitud.crearEncuesta().enviarEncuesta();
        FabricaAtencion fabricaReclamo = new FabricaReclamo();
        fabricaReclamo.crearTramite().procesar();

        new Expediente.ExpedienteBuilder()
                .setCodigo("EXP-DEMO")
                .setEstado("En Revision")
                .setTipoDocumento("solicitud")
                .build()
                .mostrarDetalle();

        new AdaptadorSistemaAntiguo().procesar();
        GrupoTramites grupo = new GrupoTramites();
        grupo.agregarTramite(TramiteFactory.crearTramite("solicitud"));
        grupo.procesar();
        Notificador notificador = new NotificadorSMS(new NotificadorBase());
        notificador.enviar("Demostración Decorator");
        new TramiteProxy("reclamo", true).procesar();
        new AlertaCiudadano(new CanalCorreo()).enviar("Demostración Bridge");
        new AlertaCiudadano(new CanalSMS()).enviar("Demostración Bridge");
    }

    /**
     * Persistencia: prueba la conexión Singleton y muestra los tipos de trámite
     * disponibles en SQL Server, sin modificar la lógica de negocio.
     */
    private static void probarConexionBaseDatos() {
        String sql = "SELECT id_tipo_tramite, nombre, descripcion "
                + "FROM TipoTramite ORDER BY id_tipo_tramite";

        try (Connection conexion = ConexionSingleton.getInstancia().getConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultados = sentencia.executeQuery(sql)) {

            System.out.println("Conectado");
            System.out.println("Contenido de TipoTramite:");
            while (resultados.next()) {
                System.out.println(
                        resultados.getInt("id_tipo_tramite") + " | "
                        + resultados.getString("nombre") + " | "
                        + resultados.getString("descripcion"));
            }
        } catch (SQLException ex) {
            System.err.println("Error exacto de conexión o consulta: "
                    + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Punto de entrada: ejecuta la demostración y abre la vista Swing.
     */
    public static void main(String[] args) {
        demostrarPatrones();
        probarConexionBaseDatos();
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Interfaz gráfica no disponible; "
                    + "la prueba de base de datos finalizó correctamente.");
            return;
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
