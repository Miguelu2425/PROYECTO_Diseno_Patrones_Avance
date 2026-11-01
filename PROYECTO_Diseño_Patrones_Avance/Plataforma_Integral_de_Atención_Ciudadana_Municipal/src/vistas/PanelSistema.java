package vistas;

import controladores.AtencionController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Vista Swing: panel de diagnóstico y demostración del patrón Singleton.
 * Muestra el municipio configurado en ConfiguracionPlataforma y el estado de ConexionSingleton.
 * MVC: se comunica exclusivamente con AtencionController.
 */
public class PanelSistema extends JPanel {

    private final AtencionController controlador;
    private final Runnable alComprobar;

    private final JLabel lblMunicipio = new JLabel();
    private final JLabel lblEstadoBD = new JLabel();
    private final JLabel lblUltimaComprobacion = new JLabel("Última comprobación: --:--:--");

    private static final Color COLOR_PRIMARIO = new Color(0, 74, 173); // #004AAD
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 17);
    private static final Font FUENTE_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FUENTE_VALOR = new Font("SansSerif", Font.PLAIN, 13);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public PanelSistema(AtencionController controlador, Runnable alComprobar) {
        this.controlador = controlador;
        this.alComprobar = alComprobar;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 248, 253));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        inicializarComponentes();
        actualizarDatos();
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
        gbc.weightx = 1.0;

        // Título
        JLabel titulo = new JLabel("Configuración y Conexión (Patrón Singleton)");
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(new Color(3, 45, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        tarjeta.add(titulo, gbc);

        gbc.gridwidth = 1;

        // Fila 1: Municipio (ConfiguracionPlataforma Singleton)
        agregarFila(tarjeta, gbc, 1, "Municipio (ConfiguracionPlataforma):", lblMunicipio);

        // Fila 2: Estado de persistencia actual
        agregarFila(tarjeta, gbc, 2, "Estado de BD (ConexionSingleton):", lblEstadoBD);

        // Fila 3: Última comprobación
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        lblUltimaComprobacion.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblUltimaComprobacion.setForeground(new Color(100, 115, 130));
        tarjeta.add(lblUltimaComprobacion, gbc);

        // Botón Probar Conexión
        JButton btnProbar = new JButton("Probar conexión (Singleton)");
        btnProbar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnProbar.setBackground(COLOR_PRIMARIO);
        btnProbar.setForeground(Color.WHITE);
        btnProbar.setFocusPainted(false);
        btnProbar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProbar.addActionListener(e -> probarConexion());

        gbc.gridy = 4;
        gbc.insets = new Insets(18, 8, 15, 8);
        tarjeta.add(btnProbar, gbc);

        // Tarjeta explicativa sobre el Singleton
        JPanel panelExplicacion = new JPanel(new GridBagLayout());
        panelExplicacion.setBackground(new Color(240, 246, 255));
        panelExplicacion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 245), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        GridBagConstraints gbcExp = new GridBagConstraints();
        gbcExp.fill = GridBagConstraints.HORIZONTAL;
        gbcExp.weightx = 1.0;
        gbcExp.insets = new Insets(3, 4, 3, 4);

        JLabel lblExpTitulo = new JLabel("Puntos de uso del Patrón Singleton en esta pantalla:");
        lblExpTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblExpTitulo.setForeground(new Color(3, 45, 102));
        gbcExp.gridy = 0;
        panelExplicacion.add(lblExpTitulo, gbcExp);

        JLabel lblExp1 = new JLabel("• ConfiguracionPlataforma: instancia global única con los datos del municipio.");
        lblExp1.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbcExp.gridy = 1;
        panelExplicacion.add(lblExp1, gbcExp);

        JLabel lblExp2 = new JLabel("• ConexionSingleton: única conexión activa a SQL Server; si no hay BD, opera en memoria.");
        lblExp2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbcExp.gridy = 2;
        panelExplicacion.add(lblExp2, gbcExp);

        JLabel lblExp3 = new JLabel("• Al pulsar 'Probar conexión', se llama dos veces para verificar 'creando' y 'reutilizando'.");
        lblExp3.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbcExp.gridy = 3;
        panelExplicacion.add(lblExp3, gbcExp);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 8, 8, 8);
        tarjeta.add(panelExplicacion, gbc);

        GridBagConstraints gbcPrincipal = new GridBagConstraints();
        gbcPrincipal.gridx = 0;
        gbcPrincipal.gridy = 0;
        gbcPrincipal.weightx = 1.0;
        gbcPrincipal.fill = GridBagConstraints.HORIZONTAL;
        add(tarjeta, gbcPrincipal);
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
                            String etiqueta, JLabel valorLabel) {
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(new Color(40, 50, 70));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        valorLabel.setFont(FUENTE_VALOR);
        valorLabel.setForeground(new Color(20, 30, 45));
        panel.add(valorLabel, gbc);
    }

    private void actualizarDatos() {
        lblMunicipio.setText(controlador.nombreMunicipio());
        lblEstadoBD.setText(controlador.estadoConexion());
    }

    private void probarConexion() {
        System.out.println("\n=== Probando ConexionSingleton desde Panel de Sistema ===");
        String resultado = controlador.probarConexion();
        System.out.println("Resultado de prueba: " + resultado);

        actualizarDatos();
        lblUltimaComprobacion.setText("Última comprobación: " + LocalTime.now().format(FMT_HORA));

        if (alComprobar != null) {
            alComprobar.run();
        }
    }
}
