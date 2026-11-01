package vistas;

import controladores.AtencionController;
import patrones.creacionales.builder.Expediente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Vista Swing: panel para listar, filtrar y gestionar el estado de las solicitudes ciudadanas.
 * Patrón MVC: interactúa exclusivamente con AtencionController.
 * Patrón State & Observer: permite avanzar de estado coordinando eventos y notificaciones.
 */
public class PanelMisSolicitudes extends JPanel {

    private final AtencionController controlador;

    private final JTextField txtDniFiltro = new JTextField(10);
    private final JButton btnBuscar = new JButton("Buscar mis solicitudes");
    private final JButton btnVerTodas = new JButton("Ver todas");
    private final JButton btnActualizar = new JButton("Actualizar");

    private final DefaultTableModel modeloTabla;
    private final JTable tablaSolicitudes;
    private final JLabel lblSinResultados = new JLabel("No hay solicitudes para mostrar", SwingConstants.CENTER);

    private final JLabel lblDetalleEstado = new JLabel("Seleccione un expediente de la tabla para ver su seguimiento.");
    private final JLabel lblSiguienteEstado = new JLabel("");
    private final JButton btnAvanzar = new JButton("Avanzar estado");

    private final JButton btnDeshacer = new JButton("Deshacer última acción");
    private final JLabel lblUltimaAccion = new JLabel("Sin acciones para deshacer");

    private String filtroDniActual = null;

    private static final Color COLOR_PRIMARIO = new Color(0, 74, 173); // #004AAD
    private static final Color COLOR_DESHACER = new Color(180, 40, 40); // Rojo suave para reversion
    private static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FUENTE_NEGRITA = new Font("SansSerif", Font.BOLD, 13);

    public PanelMisSolicitudes(AtencionController controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 248, 253));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Inicializar tabla
        String[] columnas = {"Código", "Tipo", "Ciudadano", "Estado", "Situación", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaSolicitudes = new JTable(modeloTabla);
        configurarTabla();

        // 1. Barra superior de filtros
        add(crearBarraFiltros(), BorderLayout.NORTH);

        // 2. Centro: JTable con mensaje en caso de estar vacía
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(Color.WHITE);
        panelCentro.setBorder(BorderFactory.createLineBorder(new Color(210, 225, 245), 1, true));

        JScrollPane scrollTabla = new JScrollPane(tablaSolicitudes);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        lblSinResultados.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblSinResultados.setForeground(new Color(120, 130, 145));
        lblSinResultados.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        lblSinResultados.setVisible(false);
        panelCentro.add(lblSinResultados, BorderLayout.SOUTH);

        add(panelCentro, BorderLayout.CENTER);

        // 3. Panel inferior de seguimiento y avance
        add(crearPanelSeguimiento(), BorderLayout.SOUTH);

        // Refresco automatico al hacerse visible
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                recargar();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        // Cargar solicitudes iniciales
        cargarSolicitudes(null, null);
    }

    private JPanel crearBarraFiltros() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 225, 245), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblDni = new JLabel("DNI:");
        lblDni.setFont(FUENTE_NEGRITA);
        barra.add(lblDni);

        txtDniFiltro.setFont(FUENTE_NORMAL);
        barra.add(txtDniFiltro);

        btnBuscar.setFont(FUENTE_NEGRITA);
        btnBuscar.setBackground(COLOR_PRIMARIO);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarPorDni());
        barra.add(btnBuscar);

        btnVerTodas.setFont(FUENTE_NORMAL);
        btnVerTodas.setBackground(new Color(235, 242, 250));
        btnVerTodas.setFocusPainted(false);
        btnVerTodas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerTodas.addActionListener(e -> {
            txtDniFiltro.setText("");
            filtroDniActual = null;
            cargarSolicitudes(null, null);
        });
        barra.add(btnVerTodas);

        btnActualizar.setFont(FUENTE_NORMAL);
        btnActualizar.setBackground(new Color(235, 242, 250));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarSolicitudes(filtroDniActual, obtenerCodigoSeleccionado()));
        barra.add(btnActualizar);

        return barra;
    }

    private void configurarTabla() {
        tablaSolicitudes.setFont(FUENTE_NORMAL);
        tablaSolicitudes.setRowHeight(28);
        tablaSolicitudes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSolicitudes.getTableHeader().setFont(FUENTE_NEGRITA);
        tablaSolicitudes.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tablaSolicitudes.getTableHeader().setBackground(new Color(235, 242, 252));
        tablaSolicitudes.getTableHeader().setForeground(new Color(3, 45, 102));

        // Renderer de color para la columna Estado (índice 3)
        tablaSolicitudes.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(FUENTE_NEGRITA);

                String estado = (value != null) ? value.toString() : "";
                if (!isSelected) {
                    switch (estado) {
                        case "En Revision":
                            c.setBackground(new Color(255, 243, 205)); // Ámbar claro
                            c.setForeground(new Color(133, 100, 4));
                            break;
                        case "Atendido":
                            c.setBackground(new Color(212, 237, 218)); // Verde claro
                            c.setForeground(new Color(21, 87, 36));
                            break;
                        case "Cerrado":
                            c.setBackground(new Color(226, 227, 229)); // Gris claro
                            c.setForeground(new Color(56, 61, 65));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                            break;
                    }
                }
                return c;
            }
        });

        // Centrar Código y Situación
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tablaSolicitudes.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tablaSolicitudes.getColumnModel().getColumn(4).setCellRenderer(centrado);

        // Evento de selección de fila
        tablaSolicitudes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarDetalleSeleccion();
            }
        });
    }

    private JPanel crearPanelSeguimiento() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 225, 245), 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 6, 4, 6);

        // Título de detalle
        JLabel lblTitulo = new JLabel("Control y Seguimiento del Trámite Seleccionado");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(3, 45, 102));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(lblTitulo, gbc);

        // Estado actual y descripción
        lblDetalleEstado.setFont(FUENTE_NORMAL);
        gbc.gridy = 1;
        panel.add(lblDetalleEstado, gbc);

        // Siguiente estado
        lblSiguienteEstado.setFont(FUENTE_NEGRITA);
        lblSiguienteEstado.setForeground(COLOR_PRIMARIO);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(lblSiguienteEstado, gbc);

        // Botón Avanzar estado
        btnAvanzar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAvanzar.setBackground(COLOR_PRIMARIO);
        btnAvanzar.setForeground(Color.WHITE);
        btnAvanzar.setFocusPainted(false);
        btnAvanzar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAvanzar.setEnabled(false);
        btnAvanzar.addActionListener(e -> confirmarYAvanzarEstado());

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnAvanzar, gbc);

        // Separador sutil
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 235, 248));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 4, 10, 4);
        panel.add(sep, gbc);

        // Fila 4: Control de Deshacer (Command & Memento)
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 6, 2, 6);
        lblUltimaAccion.setFont(FUENTE_NORMAL);
        panel.add(lblUltimaAccion, gbc);

        btnDeshacer.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDeshacer.setBackground(COLOR_DESHACER);
        btnDeshacer.setForeground(Color.WHITE);
        btnDeshacer.setFocusPainted(false);
        btnDeshacer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeshacer.setEnabled(false);
        btnDeshacer.addActionListener(e -> AccionDeshacer.ejecutarDeshacer(this, controlador, btnDeshacer, this::recargar));

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnDeshacer, gbc);

        return panel;
    }

    private void buscarPorDni() {
        String dni = txtDniFiltro.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número de DNI para realizar la búsqueda.",
                    "DNI requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this,
                    "El DNI debe contener exactamente 8 dígitos.",
                    "DNI inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        filtroDniActual = dni;
        cargarSolicitudes(filtroDniActual, null);
    }

    private void actualizarDetalleSeleccion() {
        AccionDeshacer.actualizarEstadoControles(controlador, btnDeshacer, lblUltimaAccion);

        int fila = tablaSolicitudes.getSelectedRow();
        if (fila == -1) {
            lblDetalleEstado.setText("Seleccione un expediente de la tabla para ver su seguimiento.");
            lblSiguienteEstado.setText("");
            btnAvanzar.setEnabled(false);
            return;
        }

        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 3);
        String descripcion = controlador.descripcionEstado(estado);
        String siguiente = controlador.siguienteEstado(estado);

        lblDetalleEstado.setText("Expediente " + codigo + " (" + estado + "): " + descripcion);

        if ("-".equals(siguiente)) {
            lblSiguienteEstado.setText("Trámite finalizado (no admite más avances)");
            btnAvanzar.setEnabled(false);
        } else {
            lblSiguienteEstado.setText("Siguiente estado: " + siguiente);
            btnAvanzar.setEnabled(true);
        }
    }

    private void confirmarYAvanzarEstado() {
        int fila = tablaSolicitudes.getSelectedRow();
        if (fila == -1) return;

        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        String estadoActual = (String) modeloTabla.getValueAt(fila, 3);
        String siguiente = controlador.siguienteEstado(estadoActual);

        if ("-".equals(siguiente)) {
            JOptionPane.showMessageDialog(this,
                    "El trámite ya se encuentra en su estado final (" + estadoActual + ").",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea avanzar el expediente " + codigo + " de '" + estadoActual + "' a '" + siguiente + "'?",
                "Confirmar avance de estado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        btnAvanzar.setEnabled(false);
        SwingWorker<Expediente, Void> worker = new SwingWorker<Expediente, Void>() {
            @Override
            protected Expediente doInBackground() {
                return controlador.avanzarEstado(codigo);
            }

            @Override
            protected void done() {
                try {
                    Expediente expActualizado = get();
                    cargarSolicitudes(filtroDniActual, expActualizado.getCodigo());
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(PanelMisSolicitudes.this,
                            "Error al avanzar estado: " + causa.getMessage(),
                            "Error de persistencia", JOptionPane.ERROR_MESSAGE);
                    btnAvanzar.setEnabled(true);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.execute();
    }

    public void recargar() {
        cargarSolicitudes(filtroDniActual, obtenerCodigoSeleccionado());
    }

    private String obtenerCodigoSeleccionado() {
        int fila = tablaSolicitudes.getSelectedRow();
        if (fila != -1) {
            return (String) modeloTabla.getValueAt(fila, 0);
        }
        return null;
    }

    private void cargarSolicitudes(String dniOpcional, String codigoASeleccionar) {
        SwingWorker<List<Expediente>, Void> worker = new SwingWorker<List<Expediente>, Void>() {
            @Override
            protected List<Expediente> doInBackground() {
                return controlador.listarMisSolicitudes(dniOpcional);
            }

            @Override
            protected void done() {
                try {
                    List<Expediente> lista = get();
                    modeloTabla.setRowCount(0);

                    if (lista == null || lista.isEmpty()) {
                        lblSinResultados.setVisible(true);
                        actualizarDetalleSeleccion();
                        return;
                    }

                    lblSinResultados.setVisible(false);
                    int filaSeleccionar = -1;

                    for (int i = 0; i < lista.size(); i++) {
                        Expediente e = lista.get(i);
                        modeloTabla.addRow(new Object[]{
                                e.getCodigo(),
                                e.getTipoDocumento(),
                                e.getNombreCiudadano(),
                                e.getEstado(),
                                controlador.situacionEstado(e.getEstado()),
                                e.getFechaRegistro()
                        });

                        if (codigoASeleccionar != null && codigoASeleccionar.equals(e.getCodigo())) {
                            filaSeleccionar = i;
                        }
                    }

                    if (filaSeleccionar != -1) {
                        tablaSolicitudes.setRowSelectionInterval(filaSeleccionar, filaSeleccionar);
                    } else if (tablaSolicitudes.getRowCount() > 0) {
                        tablaSolicitudes.setRowSelectionInterval(0, 0);
                    }

                    actualizarDetalleSeleccion();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(PanelMisSolicitudes.this,
                            "Error al consultar trámites: " + causa.getMessage(),
                            "Error de persistencia", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.execute();
    }
}

