package vistas;

import controladores.AtencionController;
import patrones.creacionales.builder.Expediente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Vista Swing: panel para registrar un nuevo trámite ciudadano.
 * GRASP Alta Cohesión: contiene únicamente los controles y validaciones del formulario de registro.
 * MVC: se comunica exclusivamente con AtencionController.
 */
public class PanelRegistro extends JPanel {

    private final AtencionController controlador;
    private final Runnable alRegistrar;

    private final JTextField dniField = new JTextField(15);
    private final JTextField nombreField = new JTextField(20);
    private final JTextField correoField = new JTextField(20);
    private final JComboBox<String> tramiteCombo = new JComboBox<>(new String[]{"reclamo", "solicitud"});

    private static final Color COLOR_PRIMARIO = new Color(0, 74, 173); // #004AAD
    private static final Font FUENTE_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FUENTE_INPUT = new Font("SansSerif", Font.PLAIN, 14);

    /**
     * Construye el panel de registro.
     *
     * @param controlador controlador MVC
     * @param alRegistrar callback para notificar a la ventana principal cuando se registre un trámite
     */
    public PanelRegistro(AtencionController controlador, Runnable alRegistrar) {
        this.controlador = controlador;
        this.alRegistrar = alRegistrar;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 253));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 225, 245), 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1;

        // Título del formulario
        JLabel lblTitulo = new JLabel("Registrar Nuevo Trámite");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(3, 45, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 8, 15, 8);
        tarjeta.add(lblTitulo, gbc);

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        // Campos
        agregarFila(tarjeta, gbc, 1, "DNI:", dniField);
        agregarFila(tarjeta, gbc, 2, "Nombre completo:", nombreField);
        agregarFila(tarjeta, gbc, 3, "Correo electrónico:", correoField);
        agregarFila(tarjeta, gbc, 4, "Tipo de trámite:", tramiteCombo);

        // Estilos de inputs
        dniField.setFont(FUENTE_INPUT);
        nombreField.setFont(FUENTE_INPUT);
        correoField.setFont(FUENTE_INPUT);
        tramiteCombo.setFont(FUENTE_INPUT);

        // Botón Registrar
        JButton btnRegistrar = new JButton("Registrar trámite");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.setBackground(COLOR_PRIMARIO);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> registrarTramite());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 6, 8);
        tarjeta.add(btnRegistrar, gbc);

        // Botón Limpiar
        JButton btnLimpiar = new JButton("Limpiar formulario");
        btnLimpiar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnLimpiar.setBackground(new Color(235, 240, 248));
        btnLimpiar.setForeground(new Color(50, 70, 90));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        gbc.gridy = 6;
        gbc.insets = new Insets(4, 8, 8, 8);
        tarjeta.add(btnLimpiar, gbc);

        GridBagConstraints gbcPrincipal = new GridBagConstraints();
        gbcPrincipal.gridx = 0;
        gbcPrincipal.gridy = 0;
        gbcPrincipal.weightx = 1.0;
        gbcPrincipal.fill = GridBagConstraints.HORIZONTAL;
        add(tarjeta, gbcPrincipal);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
                            String etiqueta, java.awt.Component componente) {
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(new Color(40, 50, 70));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(componente, gbc);
    }

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

        System.out.println("--- Registrando tramite en Mesa de Partes ---");
        Expediente exp = controlador.atenderCiudadano(dni, nombre, correo, tramite);
        System.out.println("\n========================================");
        System.out.println("TRAMITE REGISTRADO CORRECTAMENTE");
        System.out.println("Expediente: " + exp.getCodigo());
        System.out.println("Ciudadano:  " + nombre + " (DNI: " + dni + ")");
        System.out.println("Correo:     " + correo);
        System.out.println("Tipo:       " + tramite.toUpperCase());
        System.out.println("Estado:     " + exp.getEstado());
        System.out.println("========================================\n");

        if (alRegistrar != null) {
            alRegistrar.run();
        }

        JOptionPane.showMessageDialog(this,
                "El trámite fue registrado correctamente.\nCódigo: " + exp.getCodigo(),
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);

        limpiarFormulario();
    }

    public void limpiarFormulario() {
        dniField.setText("");
        nombreField.setText("");
        correoField.setText("");
        tramiteCombo.setSelectedIndex(0);
    }
}

