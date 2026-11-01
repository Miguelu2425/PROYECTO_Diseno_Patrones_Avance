package repositorios;

import modelos.Ciudadano;
import patrones.creacionales.builder.Expediente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositorio: implementacion en memoria para cuando SQL Server no esta disponible.
 * Builder: usa ExpedienteBuilder para crear cada Expediente.
 */
public class TramiteRepositorioMemoria implements TramiteRepositorio {

    private final List<Expediente> almacen = new ArrayList<>();
    private final AtomicInteger secuencia = new AtomicInteger(0);

    @Override
    public Expediente registrar(Ciudadano ciudadano, String tipoTramite) {
        int seq = secuencia.incrementAndGet();
        String anio = String.valueOf(LocalDateTime.now().getYear());
        String codigo = "EXP-" + anio + "-" + String.format("%04d", seq);
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Expediente exp = new Expediente.ExpedienteBuilder()
                .setCodigo(codigo)
                .setEstado("En Revision")
                .setTipoDocumento(tipoTramite)
                .setDni(ciudadano.getDni())
                .setNombreCiudadano(ciudadano.getNombre())
                .setFechaRegistro(fecha)
                .build();
        almacen.add(exp);
        return exp;
    }

    @Override
    public Expediente cambiarEstado(String codigo, String estadoNuevo) {
        for (int i = 0; i < almacen.size(); i++) {
            if (almacen.get(i).getCodigo().equals(codigo)) {
                Expediente actual = almacen.get(i);
                Expediente actualizado = new Expediente.ExpedienteBuilder()
                        .setCodigo(actual.getCodigo())
                        .setEstado(estadoNuevo)
                        .setTipoDocumento(actual.getTipoDocumento())
                        .setDni(actual.getDni())
                        .setNombreCiudadano(actual.getNombreCiudadano())
                        .setFechaRegistro(actual.getFechaRegistro())
                        .build();
                almacen.set(i, actualizado);
                return actualizado;
            }
        }
        throw new RepositorioException("Expediente no encontrado: " + codigo);
    }

    @Override
    public List<Expediente> listar(String dniOpcional) {
        List<Expediente> resultado = new ArrayList<>();
        for (Expediente exp : almacen) {
            if (dniOpcional == null || dniOpcional.equals(exp.getDni())) {
                resultado.add(exp);
            }
        }
        // Ordenar por codigo descendente (los mas nuevos primero)
        Collections.sort(resultado, (a, b) -> b.getCodigo().compareTo(a.getCodigo()));
        return resultado;
    }

    @Override
    public String descripcionOrigen() {
        return "modo memoria (sin conexion)";
    }
}