package com.gasmanager.nomina.services;

import com.gasmanager.nomina.dto.*;
import com.gasmanager.nomina.entities.*;
import com.gasmanager.nomina.enums.EstadoNomina;
import com.gasmanager.nomina.enums.TipoIncidencia;
import com.gasmanager.nomina.exceptions.ResourceNotFoundException;
import com.gasmanager.nomina.exceptions.ValidationException;
import com.gasmanager.nomina.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NominaService {

    private final NominaRepository nominaRepository;
    private final NominaDetalleRepository nominaDetalleRepository;
    private final EmpleadoRepository empleadoRepository;
    private final IncidenciaRepository incidenciaRepository;

    @Value("${nomina.isr.limite_inferior_1:0.01}")
    private BigDecimal isrLimiteInferior1;

    @Value("${nomina.isr.limite_superior_1:895.24}")
    private BigDecimal isrLimiteSuperior1;

    @Value("${nomina.isr.cuota_fija_1:0.00}")
    private BigDecimal isrCuotaFija1;

    @Value("${nomina.isr.porcentaje_1:1.92}")
    private BigDecimal isrPorcentaje1;

    @Value("${nomina.isr.limite_inferior_2:895.25}")
    private BigDecimal isrLimiteInferior2;

    @Value("${nomina.isr.limite_superior_2:1528.02}")
    private BigDecimal isrLimiteSuperior2;

    @Value("${nomina.isr.cuota_fija_2:17.19}")
    private BigDecimal isrCuotaFija2;

    @Value("${nomina.isr.porcentaje_2:6.40}")
    private BigDecimal isrPorcentaje2;

    @Value("${nomina.isr.limite_inferior_3:1528.03}")
    private BigDecimal isrLimiteInferior3;

    @Value("${nomina.isr.limite_superior_3:2829.06}")
    private BigDecimal isrLimiteSuperior3;

    @Value("${nomina.isr.cuota_fija_3:57.68}")
    private BigDecimal isrCuotaFija3;

    @Value("${nomina.isr.porcentaje_3:10.88}")
    private BigDecimal isrPorcentaje3;

    @Value("${nomina.deducciones.seguro_social:4.00}")
    private BigDecimal seguroSocialPorcentaje;

    @Value("${nomina.deducciones.infonavit:2.50}")
    private BigDecimal infonavitPorcentaje;

    @Value("${nomina.deducciones.cuota_sindical:1.00}")
    private BigDecimal cuotaSindicalPorcentaje;

    public NominaDTO procesarNomina(ProcesarNominaRequestDTO request, Long usuarioId, String usuarioNombre) {
        // Verificar si ya existe nómina para el periodo
        if (nominaRepository.existsByPeriodoInicioAndPeriodoFin(request.getPeriodoInicio(), request.getPeriodoFin())) {
            throw new ValidationException("Ya existe una nómina procesada para el periodo " +
                    request.getPeriodoInicio() + " al " + request.getPeriodoFin());
        }

        // Obtener empleados activos
        List<Empleado> empleados = empleadoRepository.findEmpleadosActivos();
        if (empleados.isEmpty()) {
            throw new ValidationException("No hay empleados activos para procesar nómina");
        }

        // Crear la nómina
        Nomina nomina = Nomina.builder()
                .folioNomina(generarFolioNomina())
                .periodoInicio(request.getPeriodoInicio())
                .periodoFin(request.getPeriodoFin())
                .fechaPago(request.getFechaPago() != null ? request.getFechaPago() : LocalDate.now())
                .fechaProcesamiento(LocalDateTime.now())
                .estado(EstadoNomina.PROCESADA)
                .observaciones(request.getObservaciones())
                .createdBy(usuarioNombre)
                .updatedBy(usuarioNombre)
                .build();

        nomina = nominaRepository.save(nomina);

        BigDecimal totalSueldos = BigDecimal.ZERO;
        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalBonos = BigDecimal.ZERO;
        BigDecimal totalDeducciones = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;

        // Procesar cada empleado
        for (Empleado empleado : empleados) {
            NominaDetalle detalle = calcularNominaEmpleado(empleado, nomina, request);
            detalle.setNomina(nomina);
            detalle = nominaDetalleRepository.save(detalle);
            nomina.addDetalle(detalle);

            totalSueldos = totalSueldos.add(detalle.getSueldoBase());
            totalHorasExtras = totalHorasExtras.add(detalle.getHorasExtrasMonto() != null ? detalle.getHorasExtrasMonto() : BigDecimal.ZERO);
            totalBonos = totalBonos.add(detalle.getBonos() != null ? detalle.getBonos() : BigDecimal.ZERO);
            totalDeducciones = totalDeducciones.add(detalle.getTotalDeducciones());
            totalImpuestos = totalImpuestos.add(detalle.getIsr() != null ? detalle.getIsr() : BigDecimal.ZERO);
            totalNeto = totalNeto.add(detalle.getNetoPagar());
        }

        nomina.setTotalEmpleados(empleados.size());
        nomina.setTotalSueldos(totalSueldos);
        nomina.setTotalHorasExtras(totalHorasExtras);
        nomina.setTotalBonos(totalBonos);
        nomina.setTotalDeducciones(totalDeducciones);
        nomina.setTotalImpuestos(totalImpuestos);
        nomina.setTotalNeto(totalNeto);

        nomina = nominaRepository.save(nomina);
        return mapToDTO(nomina);
    }

    private NominaDetalle calcularNominaEmpleado(Empleado empleado, Nomina nomina, ProcesarNominaRequestDTO request) {
        BigDecimal salarioDiario = empleado.getSalarioDiario();
        BigDecimal sueldoBase = salarioDiario.multiply(new BigDecimal("30"));

        // Obtener incidencias del periodo
        List<Incidencia> incidencias = incidenciaRepository.findByEmpleadoIdAndFechaBetween(
                empleado.getId(), request.getPeriodoInicio(), request.getPeriodoFin());

        BigDecimal horasExtras = BigDecimal.ZERO;
        BigDecimal horasExtrasMonto = BigDecimal.ZERO;
        BigDecimal faltas = BigDecimal.ZERO;
        BigDecimal faltasDescuento = BigDecimal.ZERO;
        BigDecimal bonos = BigDecimal.ZERO;
        BigDecimal descuentosFaltante = BigDecimal.ZERO;

        for (Incidencia inc : incidencias) {
            switch (inc.getTipo()) {
                case HORA_EXTRA_DOBLE:
                    horasExtras = horasExtras.add(inc.getCantidad());
                    horasExtrasMonto = horasExtrasMonto.add(inc.getMonto());
                    break;
                case HORA_EXTRA_TRIPLE:
                    horasExtras = horasExtras.add(inc.getCantidad());
                    horasExtrasMonto = horasExtrasMonto.add(inc.getMonto());
                    break;
                case FALTA:
                    faltas = faltas.add(inc.getCantidad());
                    faltasDescuento = faltasDescuento.add(salarioDiario.multiply(inc.getCantidad()));
                    break;
                case FALTANTE:
                    // Faltante de efectivo/inventario detectado en el corte: se descuenta al despachador.
                    descuentosFaltante = descuentosFaltante.add(inc.getMonto() != null ? inc.getMonto() : BigDecimal.ZERO);
                    break;
                case BONO:
                    bonos = bonos.add(inc.getMonto() != null ? inc.getMonto() : BigDecimal.ZERO);
                    break;
                default:
                    break;
            }
        }

        // Total gravado
        BigDecimal totalGravado = sueldoBase.add(horasExtrasMonto).add(bonos).subtract(faltasDescuento);
        if (totalGravado.compareTo(BigDecimal.ZERO) < 0) {
            totalGravado = BigDecimal.ZERO;
        }

        // Calcular ISR
        BigDecimal isr = calcularISR(totalGravado);

        // Deducciones
        BigDecimal seguroSocial = totalGravado.multiply(seguroSocialPorcentaje.divide(new BigDecimal("100")));
        BigDecimal infonavit = totalGravado.multiply(infonavitPorcentaje.divide(new BigDecimal("100")));
        BigDecimal cuotaSindical = totalGravado.multiply(cuotaSindicalPorcentaje.divide(new BigDecimal("100")));
        BigDecimal otrasDeducciones = descuentosFaltante;
        BigDecimal totalDeducciones = isr.add(seguroSocial).add(infonavit).add(cuotaSindical).add(otrasDeducciones);

        // Neto a pagar
        BigDecimal netoPagar = totalGravado.subtract(totalDeducciones);
        if (netoPagar.compareTo(BigDecimal.ZERO) < 0) {
            netoPagar = BigDecimal.ZERO;
        }

        return NominaDetalle.builder()
                .empleado(empleado)
                .diasTrabajados(new BigDecimal("30"))
                .sueldoBase(sueldoBase)
                .horasExtras(horasExtras)
                .horasExtrasMonto(horasExtrasMonto)
                .faltas(faltas)
                .faltasDescuento(faltasDescuento)
                .bonos(bonos)
                .totalGravado(totalGravado)
                .isr(isr)
                .cuotaSindical(cuotaSindical)
                .seguroSocial(seguroSocial)
                .infonavit(infonavit)
                .otrasDeducciones(otrasDeducciones)
                .totalDeducciones(totalDeducciones)
                .netoPagar(netoPagar)
                .build();
    }

    private BigDecimal calcularISR(BigDecimal totalGravado) {
        if (totalGravado.compareTo(isrLimiteInferior1) < 0) {
            return BigDecimal.ZERO;
        }

        if (totalGravado.compareTo(isrLimiteSuperior1) <= 0) {
            BigDecimal excedente = totalGravado.subtract(isrLimiteInferior1);
            BigDecimal impuesto = isrCuotaFija1.add(
                    excedente.multiply(isrPorcentaje1.divide(new BigDecimal("100"))));
            return impuesto.setScale(2, RoundingMode.HALF_UP);
        }

        if (totalGravado.compareTo(isrLimiteSuperior2) <= 0) {
            BigDecimal excedente = totalGravado.subtract(isrLimiteInferior2);
            BigDecimal impuesto = isrCuotaFija2.add(
                    excedente.multiply(isrPorcentaje2.divide(new BigDecimal("100"))));
            return impuesto.setScale(2, RoundingMode.HALF_UP);
        }

        // Para montos mayores
        BigDecimal excedente = totalGravado.subtract(isrLimiteInferior3);
        BigDecimal impuesto = isrCuotaFija3.add(
                excedente.multiply(isrPorcentaje3.divide(new BigDecimal("100"))));
        return impuesto.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public List<NominaDTO> listarNominas() {
        return nominaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NominaDTO obtenerNomina(Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nómina no encontrada con ID: " + id));
        return mapToDTO(nomina);
    }

    @Transactional(readOnly = true)
    public List<NominaDTO> listarNominasPorEstado(EstadoNomina estado) {
        return nominaRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NominaDTO marcarComoPagada(Long id, Long usuarioId, String usuarioNombre) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nómina no encontrada con ID: " + id));

        nomina.setEstado(EstadoNomina.PAGADA);
        nomina.setUpdatedBy(usuarioNombre);
        nomina = nominaRepository.save(nomina);
        return mapToDTO(nomina);
    }

    public NominaDTO cancelarNomina(Long id, String motivo, Long usuarioId, String usuarioNombre) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nómina no encontrada con ID: " + id));

        if (nomina.getEstado() == EstadoNomina.PAGADA) {
            throw new ValidationException("No se puede cancelar una nómina que ya ha sido pagada");
        }

        nomina.setEstado(EstadoNomina.CANCELADA);
        String observaciones = nomina.getObservaciones() != null ? nomina.getObservaciones() : "";
        nomina.setObservaciones(observaciones + "\n[CANCELADA] Motivo: " + motivo);
        nomina.setUpdatedBy(usuarioNombre);
        nomina = nominaRepository.save(nomina);
        return mapToDTO(nomina);
    }

    private String generarFolioNomina() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long secuencial = nominaRepository.count() + 1;
        return String.format("NOM-%s-%04d", fecha, secuencial);
    }

    private NominaDTO mapToDTO(Nomina nomina) {
        List<NominaDetalleDTO> detalles = nomina.getDetalles().stream()
                .map(this::mapDetalleToDTO)
                .collect(Collectors.toList());

        return NominaDTO.builder()
                .id(nomina.getId())
                .folioNomina(nomina.getFolioNomina())
                .periodoInicio(nomina.getPeriodoInicio())
                .periodoFin(nomina.getPeriodoFin())
                .fechaPago(nomina.getFechaPago())
                .fechaProcesamiento(nomina.getFechaProcesamiento())
                .totalEmpleados(nomina.getTotalEmpleados())
                .totalSueldos(nomina.getTotalSueldos())
                .totalHorasExtras(nomina.getTotalHorasExtras())
                .totalBonos(nomina.getTotalBonos())
                .totalDeducciones(nomina.getTotalDeducciones())
                .totalImpuestos(nomina.getTotalImpuestos())
                .totalNeto(nomina.getTotalNeto())
                .estado(nomina.getEstado())
                .observaciones(nomina.getObservaciones())
                .detalles(detalles)
                .createdAt(nomina.getCreatedAt())
                .updatedAt(nomina.getUpdatedAt())
                .build();
    }

    private NominaDetalleDTO mapDetalleToDTO(NominaDetalle detalle) {
        return NominaDetalleDTO.builder()
                .id(detalle.getId())
                .empleadoId(detalle.getEmpleado().getId())
                .empleadoNombre(detalle.getEmpleado().getNombreCompleto())
                .empleadoCodigo(detalle.getEmpleado().getCodigoEmpleado())
                .diasTrabajados(detalle.getDiasTrabajados())
                .sueldoBase(detalle.getSueldoBase())
                .horasExtras(detalle.getHorasExtras())
                .horasExtrasMonto(detalle.getHorasExtrasMonto())
                .faltas(detalle.getFaltas())
                .faltasDescuento(detalle.getFaltasDescuento())
                .bonos(detalle.getBonos())
                .totalGravado(detalle.getTotalGravado())
                .isr(detalle.getIsr())
                .cuotaSindical(detalle.getCuotaSindical())
                .seguroSocial(detalle.getSeguroSocial())
                .infonavit(detalle.getInfonavit())
                .otrasDeducciones(detalle.getOtrasDeducciones())
                .totalDeducciones(detalle.getTotalDeducciones())
                .netoPagar(detalle.getNetoPagar())
                .build();
    }
}