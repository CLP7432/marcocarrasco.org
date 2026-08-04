import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cortesService, turnosService, ventasService, creditosVentasService } from '../../api/ventas/auth';
import { useAuth } from '../../contexts/AuthContext';

const CorteTurnoForm = () => {
    const navigate = useNavigate();
    const { user, isAdmin } = useAuth();
    const [loading, setLoading] = useState(true);
    const [calculando, setCalculando] = useState(false);
    const [turnosCerrados, setTurnosCerrados] = useState([]);
    const [turnosDisponibles, setTurnosDisponibles] = useState([]);
    const [turnoSeleccionado, setTurnoSeleccionado] = useState('');
    const [dispensariosDisponibles, setDispensariosDisponibles] = useState([]);
    const [dispensarioSeleccionado, setDispensarioSeleccionado] = useState(null);
    const [lecturasIniciales, setLecturasIniciales] = useState([]);
    const [notasCredito, setNotasCredito] = useState([]);
    const [aceites, setAceites] = useState([]);
    const [lecturasInicialesAceites, setLecturasInicialesAceites] = useState({});
    const [lecturasFinalesAceites, setLecturasFinalesAceites] = useState({});
    const [efectivoRecibido, setEfectivoRecibido] = useState('');
    const [tarjetaRecibido, setTarjetaRecibido] = useState('');
    const [transferenciaRecibido, setTransferenciaRecibido] = useState('');
    const [observaciones, setObservaciones] = useState('');
    const [procesando, setProcesando] = useState(false);
    const [paso, setPaso] = useState(1);
    const [cortesRealizados, setCortesRealizados] = useState([]);
    const [mostrarHistorial, setMostrarHistorial] = useState(false);
    const [litrosVendidosCalculados, setLitrosVendidosCalculados] = useState({});
    const [clientesCredito, setClientesCredito] = useState([]);
    const [despachadores, setDespachadores] = useState([]);
    const [despachadorSeleccionado, setDespachadorSeleccionado] = useState('');
    const [cargandoDispensarios, setCargandoDispensarios] = useState(false);
    const [combustibles, setCombustibles] = useState([]);

    useEffect(() => {
        cargarTurnosCerrados();
        cargarCortesRealizados();
        cargarClientesConCredito();
        cargarDespachadores();
        cargarCombustibles();
    }, []);

    const cargarCombustibles = async () => {
        try {
            const response = await fetch('/api/precios/combustibles');
            if (response.ok) {
                const data = await response.json();
                setCombustibles(Array.isArray(data) ? data : []);
            }
        } catch (error) {
            console.error('Error cargando combustibles:', error);
        }
    };

    const cargarDespachadores = async () => {
        try {
            const response = await fetch('/api/empleados/despachadores');
            if (response.ok) {
                const data = await response.json();
                setDespachadores(data);
                console.log('Despachadores cargados:', data);
            } else {
                console.warn('No se pudieron cargar despachadores, usando datos de prueba');
                setDespachadores([
                    { id: 1, nombre: 'Juan', apellidoPaterno: 'Pérez', apellidoMaterno: 'García', activo: true },
                    { id: 2, nombre: 'María', apellidoPaterno: 'López', apellidoMaterno: 'Martínez', activo: true },
                ]);
            }
        } catch (error) {
            console.error('Error cargando despachadores:', error);
            setDespachadores([
                { id: 1, nombre: 'Juan', apellidoPaterno: 'Pérez', apellidoMaterno: 'García', activo: true },
                { id: 2, nombre: 'María', apellidoPaterno: 'López', apellidoMaterno: 'Martínez', activo: true },
            ]);
        }
    };

    const cargarTurnosCerrados = async () => {
        try {
            const data = await turnosService.listarPorEstado('CERRADO');
            setTurnosCerrados(data);

            const cortes = await cortesService.listar();
            const turnosConCorte = new Set(cortes.map(c => c.turnoId));
            const disponibles = data.filter(t => !turnosConCorte.has(t.id));
            setTurnosDisponibles(disponibles);
        } catch (error) {
            console.error('Error cargando turnos:', error);
        }
        setLoading(false);
    };

    const cargarCortesRealizados = async () => {
        try {
            const data = await cortesService.listar();
            setCortesRealizados(data);
        } catch (error) {
            console.error('Error cargando cortes:', error);
        }
    };

    const cargarClientesConCredito = async () => {
        try {
            const data = await creditosVentasService.listarConSaldoPendiente();
            setClientesCredito(data);
        } catch (error) {
            console.error('Error cargando clientes con crédito:', error);
            setClientesCredito([]);
        }
    };

    // ===== CARGAR ACEITES DISPONIBLES =====
    const cargarAceites = async () => {
        try {
            const response = await fetch('/api/aceites/activos');
            if (response.ok) {
                const data = await response.json();
                setAceites(data);

                // Inicializar lecturas de aceites
                const iniciales = {};
                data.forEach(a => {
                    iniciales[a.id] = {
                        cantidadInicial: 0,
                        aceiteNombre: a.nombre,
                        precioUnitario: a.precioVenta || 0
                    };
                });
                setLecturasInicialesAceites(iniciales);

                const finales = {};
                data.forEach(a => {
                    finales[a.id] = 0;
                });
                setLecturasFinalesAceites(finales);
            }
        } catch (error) {
            console.error('Error cargando aceites:', error);
        }
    };

    // ===== CARGAR DISPENSARIOS DISPONIBLES =====
    const cargarDispensariosDisponibles = async (turnoId) => {
        setCargandoDispensarios(true);
        try {
            console.log('📡 Cargando dispensarios para turno:', turnoId);
            const response = await fetch(`/api/cortes/dispensarios-disponibles/${turnoId}`);

            if (!response.ok) {
                console.error('❌ Error HTTP:', response.status);
                throw new Error(`HTTP ${response.status}`);
            }

            const data = await response.json();
            console.log('✅ Dispensarios disponibles:', data);

            if (data && Array.isArray(data)) {
                setDispensariosDisponibles(data);
            } else {
                console.warn('⚠️ Datos inesperados:', data);
                setDispensariosDisponibles([]);
            }
        } catch (error) {
            console.error('❌ Error cargando dispensarios:', error);
            setDispensariosDisponibles([]);
            try {
                const diagResponse = await fetch(`/api/cortes/diagnostico/${turnoId}`);
                if (diagResponse.ok) {
                    const diag = await diagResponse.json();
                    console.log('🔍 Diagnóstico:', diag);
                    if (diag.dispensariosActivos === 0) {
                        alert('⚠️ No hay dispensarios activos. Debes crear al menos un dispensario con mangueras activas.');
                    } else if (diag.ventasEnTurno === 0) {
                        alert('⚠️ No hay ventas registradas en este turno. No se puede generar un corte sin ventas.');
                    } else if (diag.manguerasActivas === 0) {
                        alert('⚠️ No hay mangueras activas. Debes configurar mangueras en los dispensarios.');
                    }
                }
            } catch (e) {
                console.error('Error en diagnóstico:', e);
            }
        }
        setCargandoDispensarios(false);
    };

    // ===== BUSCAR VENTA POR FOLIO =====
    const buscarVentaPorFolio = async (folio) => {
        if (!folio || folio.trim() === '') return null;

        try {
            const response = await fetch(`/api/ventas/folio/${folio.trim()}`);
            if (!response.ok) {
                if (response.status === 404) {
                    alert('❌ No se encontró ninguna venta con ese folio');
                    return null;
                }
                throw new Error('Error al buscar la venta');
            }
            const venta = await response.json();
            return venta;
        } catch (error) {
            console.error('Error buscando venta:', error);
            alert('❌ Error al buscar la venta: ' + error.message);
            return null;
        }
    };

    // ===== ACTUALIZAR NOTA DE CRÉDITO =====
    const actualizarNotaCredito = async (index, campo, valor) => {
        const nuevasNotas = [...notasCredito];
        nuevasNotas[index][campo] = valor;

        if (campo === 'folioNota' && valor && valor.trim() !== '') {
            const venta = await buscarVentaPorFolio(valor);
            if (venta) {
                const detalle = venta.detalles && venta.detalles.length > 0 ? venta.detalles[0] : null;

                if (detalle) {
                    let tipoCombustible = 'MAGNA';
                    const productoNombre = detalle.productoNombre || '';
                    if (productoNombre.includes('PREMIUM')) {
                        tipoCombustible = 'PREMIUM';
                    } else if (productoNombre.includes('DIESEL')) {
                        tipoCombustible = 'DIESEL';
                    } else if (productoNombre.includes('MAGNA')) {
                        tipoCombustible = 'MAGNA';
                    }
                    nuevasNotas[index].tipoCombustible = tipoCombustible;
                    nuevasNotas[index].litros = detalle.cantidad || 0;
                    nuevasNotas[index].monto = venta.total || detalle.importe || 0;

                    if (venta.clienteNombre) {
                        nuevasNotas[index].clienteNombre = venta.clienteNombre;
                    }
                }
            }
        }

        if (campo === 'clienteId' && valor) {
            const cliente = clientesCredito.find(c => c.clienteId === parseInt(valor));
            if (cliente) {
                nuevasNotas[index].clienteNombre = cliente.clienteNombre;
                nuevasNotas[index].estado = cliente.estado;
                nuevasNotas[index].saldoPendiente = cliente.saldoPendiente;
            }
        }

        setNotasCredito(nuevasNotas);
    };

    // ===== SELECCIONAR TURNO =====
    const handleSeleccionarTurno = async () => {
        if (!turnoSeleccionado) {
            alert('Seleccione un turno');
            return;
        }
        setCalculando(true);
        setLoading(true);
        try {
            // Cargar dispensarios disponibles para este turno
            await cargarDispensariosDisponibles(turnoSeleccionado);

            // Cargar aceites disponibles
            await cargarAceites();

            const lecturas = await cortesService.obtenerLecturasIniciales(turnoSeleccionado);
            setLecturasIniciales(lecturas);

            const ventas = await ventasService.listarPorTurno(turnoSeleccionado);

            const litrosPorManguera = {};
            for (const venta of ventas) {
                for (const detalle of venta.detalles || []) {
                    const mangueraId = detalle.productoId;
                    const cantidad = parseFloat(detalle.cantidad) || 0;
                    if (mangueraId && cantidad > 0) {
                        litrosPorManguera[mangueraId] = (litrosPorManguera[mangueraId] || 0) + cantidad;
                    }
                }
            }
            setLitrosVendidosCalculados(litrosPorManguera);

            setPaso(2);
        } catch (error) {
            console.error('Error cargando lecturas:', error);
            alert('Error al cargar lecturas iniciales');
        }
        setLoading(false);
        setCalculando(false);
    };

    // ===== SELECCIONAR DISPENSARIO =====
    const handleSeleccionarDispensario = (dispensario) => {
        if (dispensario.tieneCorte) {
            alert('⚠️ Este dispensario ya tiene un corte realizado');
            return;
        }
        setDispensarioSeleccionado(dispensario);
        // Auto-seleccionar el primer despachador disponible
        if (despachadores.length > 0) {
            const despachadorActivo = despachadores.find(d => d.activo === true);
            if (despachadorActivo) {
                setDespachadorSeleccionado(despachadorActivo.id.toString());
            } else {
                setDespachadorSeleccionado(despachadores[0].id.toString());
            }
        }
    };

    // ===== VALIDAR CORTE =====
    const handleValidarCorte = async (id) => {
        if (window.confirm('¿Validar este corte? El supervisor confirma que los datos son correctos.')) {
            try {
                await cortesService.validar(id, user?.idUsuario, user?.nombre);
                alert('✅ Corte validado exitosamente');
                cargarCortesRealizados();
                cargarTurnosCerrados();
            } catch (error) {
                alert('❌ Error al validar corte');
            }
        }
    };

    // ===== CERRAR CORTE =====
    const handleCerrarCorte = async (id) => {
        if (window.confirm('¿Cerrar este corte? Esta acción finaliza el proceso.')) {
            try {
                await cortesService.cerrar(id);
                alert('✅ Corte cerrado exitosamente');
                cargarCortesRealizados();
                cargarTurnosCerrados();
            } catch (error) {
                alert('❌ Error al cerrar corte');
            }
        }
    };

    const verDetalleCorte = (id) => {
        navigate(`/cortes/${id}`);
    };

    const agregarNotaCredito = () => {
        setNotasCredito([...notasCredito, {
            folioNota: '',
            clienteId: '',
            clienteNombre: '',
            estado: '',
            saldoPendiente: 0,
            tipoCombustible: 'MAGNA',
            litros: '',
            monto: '',
            autorizadoPor: ''
        }]);
    };

    const eliminarNotaCredito = (index) => {
        setNotasCredito(notasCredito.filter((_, i) => i !== index));
    };

    // ===== CALCULAR TOTALES =====
    const calcularTotales = () => {
        let totalCombustibles = 0;
        let totalLitros = 0;

        // Filtrar lecturas por dispensario seleccionado
        const manguerasDispensario = dispensarioSeleccionado?.mangueras?.map(m => m.id) || [];

        for (const l of lecturasIniciales) {
            if (l.tipo === 'COMBUSTIBLE' && l.mangueraId && manguerasDispensario.includes(l.mangueraId)) {
                const litros = litrosVendidosCalculados[l.mangueraId] || 0;
                if (litros > 0) {
                    totalLitros += litros;
                    totalCombustibles += litros * (l.precioPorLitro || 0);
                }
            }
        }

        // Calcular total de aceites
        let totalAceites = 0;
        let totalAceitesVendidos = 0;
        for (const aceite of aceites) {
            const inicial = lecturasInicialesAceites[aceite.id]?.cantidadInicial || 0;
            const final = lecturasFinalesAceites[aceite.id] || 0;
            const vendidos = Math.max(0, final - inicial);
            totalAceitesVendidos += vendidos;
            totalAceites += vendidos * (aceite.precioVenta || 0);
        }

        const totalNotas = notasCredito.reduce((sum, n) => sum + (parseFloat(n.monto) || 0), 0);
        const totalVenta = totalCombustibles + totalAceites;
        const efectivoNeto = totalVenta - (parseFloat(tarjetaRecibido) || 0) - (parseFloat(transferenciaRecibido) || 0) - totalNotas;

        return {
            totalCombustibles,
            totalAceites,
            totalAceitesVendidos,
            totalVenta,
            totalNotas,
            efectivoNeto,
            totalLitros
        };
    };

    // ===== PROCESAR CORTE =====
    const handleProcesarCorte = async () => {
        if (!dispensarioSeleccionado) {
            alert('Seleccione un dispensario');
            return;
        }

        if (!despachadorSeleccionado) {
            alert('Seleccione un despachador');
            return;
        }

        const despachador = despachadores.find(d => d.id === parseInt(despachadorSeleccionado));

        // Filtrar lecturas finales solo para las mangueras del dispensario seleccionado
        const manguerasDispensario = dispensarioSeleccionado.mangueras?.map(m => m.id) || [];

        const lecturasFinalesList = lecturasIniciales
            .filter(l => l.tipo === 'COMBUSTIBLE' && l.mangueraId &&
                manguerasDispensario.includes(l.mangueraId))
            .map(l => ({
                mangueraId: l.mangueraId,
                mangueraNombre: l.mangueraNombre,
                tipoCombustible: l.tipoCombustible,
                lecturaFinal: (l.lecturaInicial || 0) + (litrosVendidosCalculados[l.mangueraId] || 0),
                tipo: 'COMBUSTIBLE'
            }));

        // ===== ACEITES FINALES =====
        const aceitesFinalesList = aceites.map(a => ({
            aceiteId: a.id,
            aceiteNombre: a.nombre,
            cantidadFinal: lecturasFinalesAceites[a.id] || 0,
            tipo: 'ACEITE'
        }));

        // ===== LECTURAS INICIALES DE ACEITES =====
        const lecturasInicialesAceitesList = aceites.map(a => ({
            aceiteId: a.id,
            aceiteNombre: a.nombre,
            cantidadInicial: lecturasInicialesAceites[a.id]?.cantidadInicial || 0,
            precioUnitario: a.precioVenta || 0,
            tipo: 'ACEITE'
        }));

        const requestData = {
            turnoId: parseInt(turnoSeleccionado),
            dispensarioId: dispensarioSeleccionado.id,
            dispensarioNombre: dispensarioSeleccionado.nombre,
            despachadorId: despachador ? despachador.id : null,
            despachadorNombre: despachador ? `${despachador.nombre} ${despachador.apellidoPaterno}` : null,
            lecturasFinales: lecturasFinalesList,
            notasCredito: notasCredito,
            aceitesFinales: aceitesFinalesList,
            lecturasInicialesAceites: lecturasInicialesAceitesList,
            efectivoRecibido: parseFloat(efectivoRecibido) || 0,
            tarjetaRecibido: parseFloat(tarjetaRecibido) || 0,
            transferenciaRecibido: parseFloat(transferenciaRecibido) || 0,
            observaciones: observaciones
        };

        console.log('📤 Datos del corte:', requestData);

        setProcesando(true);
        try {
            const resultado = await cortesService.procesarCorte(requestData);
            alert(`✅ Corte generado exitosamente\n\nCódigo: ${resultado.codigoCorte}\nDispensario: ${resultado.dispensarioNombre}\nDespachador: ${resultado.despachadorNombre}\nTotal: $${resultado.totalVentaCombustiblesYAceites?.toFixed(2) || '0.00'}`);

            // Resetear pero mantener el turno seleccionado
            setDispensarioSeleccionado(null);
            setDespachadorSeleccionado('');
            setNotasCredito([]);
            setEfectivoRecibido('');
            setTarjetaRecibido('');
            setTransferenciaRecibido('');
            setObservaciones('');
            setLecturasFinalesAceites({});
            setLecturasInicialesAceites({});

            // Recargar dispensarios disponibles
            await cargarDispensariosDisponibles(turnoSeleccionado);
            cargarCortesRealizados();
            cargarTurnosCerrados();
        } catch (error) {
            console.error('Error procesando corte:', error);
            const mensaje = error.response?.data?.message || error.message;
            alert('❌ Error al procesar el corte: ' + mensaje);
        }
        setProcesando(false);
    };

    const { totalCombustibles, totalAceites, totalAceitesVendidos, totalVenta, totalNotas, efectivoNeto, totalLitros } = calcularTotales();

    const getEstadoBadge = (estado) => {
        const colores = {
            PENDIENTE: 'badge bg-warning text-dark',
            VALIDADO: 'badge bg-info text-white',
            CERRADO: 'badge bg-success text-white'
        };
        const textos = {
            PENDIENTE: '⏳ PENDIENTE',
            VALIDADO: '✓ VALIDADO',
            CERRADO: '✅ CERRADO'
        };
        return <span className={colores[estado] || 'badge bg-secondary'} style={{ padding: '6px 12px' }}>{textos[estado] || estado}</span>;
    };

    if (loading) {
        return (
            <div className="card text-center p-5">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
                <p className="mt-3">Cargando turnos...</p>
            </div>
        );
    }

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Corte de Turno por Dispensario</h2>
                <button className="btn btn-secondary" onClick={() => setMostrarHistorial(!mostrarHistorial)}>
                    {mostrarHistorial ? '📝 Nuevo Corte' : '📋 Historial de Cortes'}
                </button>
            </div>

            {mostrarHistorial ? (
                <div className="card">
                    <h3>📋 Historial de Cortes Realizados</h3>
                    {cortesRealizados.length === 0 ? (
                        <p className="text-muted">No hay cortes realizados</p>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-striped">
                                <thead className="table-dark">
                                <tr>
                                    <th>Código</th>
                                    <th>Turno</th>
                                    <th>Dispensario</th>
                                    <th>Despachador</th>
                                    <th>Fecha</th>
                                    <th>Total</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                                </thead>
                                <tbody>
                                {cortesRealizados.map(corte => (
                                    <tr key={corte.id}>
                                        <td><code className="fw-bold">{corte.codigoCorte}</code></td>
                                        <td>{corte.turnoNombre}</td>
                                        <td><span className="badge bg-primary">{corte.dispensarioNombre || '-'}</span></td>
                                        <td>{corte.despachadorNombre || '-'}</td>
                                        <td>{new Date(corte.createdAt).toLocaleString()}</td>
                                        <td className="text-end fw-bold">${corte.totalVentaCombustiblesYAceites?.toFixed(2) || '0.00'}</td>
                                        <td>{getEstadoBadge(corte.estado)}</td>
                                        <td>
                                            <div className="btn-group btn-group-sm" role="group">
                                                {corte.estado === 'PENDIENTE' && isAdmin && (
                                                    <button
                                                        className="btn btn-success"
                                                        onClick={() => handleValidarCorte(corte.id)}
                                                        title="Validar corte"
                                                    >
                                                        ✓ Validar
                                                    </button>
                                                )}
                                                {corte.estado === 'VALIDADO' && isAdmin && (
                                                    <button
                                                        className="btn btn-danger"
                                                        onClick={() => handleCerrarCorte(corte.id)}
                                                        title="Cerrar corte"
                                                    >
                                                        🔒 Cerrar
                                                    </button>
                                                )}
                                                <button
                                                    className="btn btn-primary"
                                                    onClick={() => verDetalleCorte(corte.id)}
                                                    title="Ver detalle"
                                                >
                                                    👁 Ver
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            ) : (
                <>
                    {paso === 1 && (
                        <div className="card">
                            <h3>📋 Seleccionar Turno Cerrado</h3>
                            <div className="form-group mb-3">
                                <label className="form-label">Turno *</label>
                                <select
                                    className="form-select"
                                    value={turnoSeleccionado}
                                    onChange={(e) => setTurnoSeleccionado(e.target.value)}
                                >
                                    <option value="">Seleccione un turno</option>
                                    {turnosDisponibles.map(turno => (
                                        <option key={turno.id} value={turno.id}>
                                            {turno.codigoTurno} - {turno.nombre} - {new Date(turno.fechaTurno).toLocaleDateString()} (${turno.totalVentas?.toFixed(2) || '0.00'})
                                        </option>
                                    ))}
                                </select>
                                {turnosDisponibles.length === 0 && (
                                    <small className="text-warning">⚠️ No hay turnos cerrados disponibles para generar corte</small>
                                )}
                            </div>
                            <button
                                className="btn btn-primary"
                                onClick={handleSeleccionarTurno}
                                disabled={calculando || turnosDisponibles.length === 0}
                            >
                                {calculando ? 'Cargando...' : 'Continuar →'}
                            </button>
                        </div>
                    )}

                    {paso === 2 && (
                        <>
                            {/* ===== SELECCIONAR DISPENSARIO ===== */}
                            <div className="card mb-3">
                                <h3>⛽ Seleccionar Dispensario para Corte</h3>
                                {cargandoDispensarios ? (
                                    <div className="text-center py-3">
                                        <div className="spinner-border text-primary" role="status">
                                            <span className="visually-hidden">Cargando...</span>
                                        </div>
                                        <p className="mt-2">Cargando dispensarios disponibles...</p>
                                    </div>
                                ) : dispensariosDisponibles.length === 0 ? (
                                    <div className="alert alert-warning">
                                        ⚠️ No hay dispensarios disponibles para corte en este turno.
                                        <br />
                                        <strong>Posibles causas:</strong>
                                        <ul className="mt-2 mb-0">
                                            <li>No hay dispensarios activos configurados</li>
                                            <li>No hay mangueras activas en los dispensarios</li>
                                            <li>No hay ventas registradas en este turno</li>
                                            <li>Todos los dispensarios ya tienen corte realizado</li>
                                        </ul>
                                        <button
                                            className="btn btn-sm btn-outline-primary mt-2"
                                            onClick={() => cargarDispensariosDisponibles(turnoSeleccionado)}
                                        >
                                            🔄 Reintentar
                                        </button>
                                    </div>
                                ) : (
                                    <>
                                        <p className="text-muted mb-3">Seleccione un dispensario para generar su corte:</p>
                                        <div className="row g-3">
                                            {dispensariosDisponibles.map(d => (
                                                <div key={d.id} className="col-md-4 col-lg-3">
                                                    <div
                                                        className={`card h-100 ${dispensarioSeleccionado?.id === d.id ? 'border-primary border-3' : ''}`}
                                                        onClick={() => handleSeleccionarDispensario(d)}
                                                        style={{
                                                            cursor: d.tieneCorte ? 'not-allowed' : 'pointer',
                                                            opacity: d.tieneCorte ? 0.6 : 1,
                                                            transition: 'all 0.2s'
                                                        }}
                                                        onMouseEnter={(e) => {
                                                            if (!d.tieneCorte) {
                                                                e.currentTarget.style.transform = 'translateY(-5px)';
                                                                e.currentTarget.style.boxShadow = '0 8px 20px rgba(0,0,0,0.15)';
                                                            }
                                                        }}
                                                        onMouseLeave={(e) => {
                                                            e.currentTarget.style.transform = 'translateY(0)';
                                                            e.currentTarget.style.boxShadow = 'none';
                                                        }}
                                                    >
                                                        <div className="card-body text-center">
                                                            <h5 className="card-title">⛽ {d.nombre}</h5>
                                                            <p className="card-text">
                                                                <span className="badge bg-info">{d.mangueras?.length || 0} mangueras</span>
                                                            </p>
                                                            <p className="card-text">
                                                                <small>Ventas: {d.ventasCount || 0}</small>
                                                                <br />
                                                                <strong className="text-success">${d.totalVentas?.toFixed(2) || '0.00'}</strong>
                                                            </p>
                                                            {d.tieneCorte ? (
                                                                <span className="badge bg-secondary">✅ Corte ya realizado</span>
                                                            ) : (
                                                                <span className="badge bg-success">✅ Disponible</span>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </>
                                )}
                            </div>

                            {/* ===== SELECCIONAR DESPACHADOR ===== */}
                            {dispensarioSeleccionado && !dispensarioSeleccionado.tieneCorte && (
                                <div className="card mb-3">
                                    <h3>👤 Seleccionar Despachador</h3>
                                    <div className="form-group">
                                        <label className="form-label">Despachador *</label>
                                        <select
                                            className="form-select"
                                            value={despachadorSeleccionado}
                                            onChange={(e) => setDespachadorSeleccionado(e.target.value)}
                                            required
                                        >
                                            <option value="">Seleccione un despachador</option>
                                            {despachadores
                                                .filter(d => d.activo === true)
                                                .map(d => (
                                                    <option key={d.id} value={d.id}>
                                                        {d.nombre} {d.apellidoPaterno} {d.apellidoMaterno || ''}
                                                        {d.dispensarioNombre ? ` - ${d.dispensarioNombre}` : ''}
                                                    </option>
                                                ))}
                                        </select>
                                        {despachadores.filter(d => d.activo === true).length === 0 && (
                                            <small className="text-warning">⚠️ No hay despachadores activos disponibles</small>
                                        )}
                                    </div>
                                </div>
                            )}

                            {/* ===== RESUMEN DE VENTAS DEL DISPENSARIO ===== */}
                            {dispensarioSeleccionado && !dispensarioSeleccionado.tieneCorte && (
                                <div className="card mb-3">
                                    <h3>⛽ Resumen de Ventas - {dispensarioSeleccionado.nombre}</h3>
                                    <div className="table-responsive">
                                        <table className="table table-bordered">
                                            <thead className="table-dark">
                                            <tr>
                                                <th>Manguera</th>
                                                <th>Tipo</th>
                                                <th>Lectura Inicial (L)</th>
                                                <th>Litros Vendidos</th>
                                                <th>Precio x L</th>
                                                <th>Importe</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {lecturasIniciales
                                                .filter(l => l.tipo === 'COMBUSTIBLE' &&
                                                    dispensarioSeleccionado.mangueras?.some(m => m.id === l.mangueraId))
                                                .map(l => {
                                                    const litrosVendidos = litrosVendidosCalculados[l.mangueraId] || 0;
                                                    const importe = litrosVendidos * (l.precioPorLitro || 0);
                                                    return (
                                                        <tr key={l.mangueraId}>
                                                            <td><strong>{l.mangueraNombre}</strong></td>
                                                            <td>{l.tipoCombustible}</td>
                                                            <td className="text-end">{(l.lecturaInicial || 0).toFixed(3)}</td>
                                                            <td className="text-end fw-bold text-primary">{litrosVendidos.toFixed(3)}</td>
                                                            <td className="text-end">${(l.precioPorLitro || 0).toFixed(2)}</td>
                                                            <td className="text-end text-success fw-bold">${importe.toFixed(2)}</td>
                                                        </tr>
                                                    );
                                                })}
                                            </tbody>
                                            <tfoot className="table-light">
                                            <tr>
                                                <td colSpan="3" className="text-end fw-bold">Total Litros: {totalLitros.toFixed(3)} L</td>
                                                <td colSpan="2" className="text-end fw-bold">Total Combustibles:</td>
                                                <td className="text-end fw-bold text-success">${totalCombustibles.toFixed(2)}</td>
                                            </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                </div>
                            )}

                            {/* ===== ACEITES Y ADITIVOS ===== */}
                            {dispensarioSeleccionado && !dispensarioSeleccionado.tieneCorte && aceites.length > 0 && (
                                <div className="card mb-3">
                                    <div className="card-header bg-secondary text-white">
                                        <h5 className="mb-0">🛢️ Aceites y Aditivos - {dispensarioSeleccionado.nombre}</h5>
                                    </div>
                                    <div className="card-body">
                                        <p className="text-muted mb-2">
                                            <small>Registre el stock inicial y final de cada producto. Los vendidos e importe se calcularán automáticamente.</small>
                                        </p>
                                        <div className="table-responsive">
                                            <table className="table table-bordered">
                                                <thead className="table-dark">
                                                <tr>
                                                    <th>Código</th>
                                                    <th>Nombre</th>
                                                    <th>Precio Unitario</th>
                                                    <th style={{ minWidth: '100px' }}>Stock Inicial</th>
                                                    <th style={{ minWidth: '100px' }}>Stock Final</th>
                                                    <th>Vendidos</th>
                                                    <th>Importe</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                {aceites.map(aceite => {
                                                    const inicial = lecturasInicialesAceites[aceite.id]?.cantidadInicial || 0;
                                                    const final = lecturasFinalesAceites[aceite.id] || 0;
                                                    const vendidos = Math.max(0, final - inicial);
                                                    const importe = vendidos * (aceite.precioVenta || 0);

                                                    return (
                                                        <tr key={aceite.id}>
                                                            <td><code>{aceite.codigo}</code></td>
                                                            <td>{aceite.nombre}</td>
                                                            <td className="text-end fw-bold">${aceite.precioVenta?.toFixed(2) || '0.00'}</td>
                                                            <td>
                                                                <input
                                                                    type="number"
                                                                    className="form-control form-control-sm text-center"
                                                                    min="0"
                                                                    value={inicial || ''}
                                                                    onChange={(e) => {
                                                                        const val = parseInt(e.target.value) || 0;
                                                                        setLecturasInicialesAceites(prev => ({
                                                                            ...prev,
                                                                            [aceite.id]: {
                                                                                ...prev[aceite.id],
                                                                                cantidadInicial: val,
                                                                                aceiteNombre: aceite.nombre,
                                                                                precioUnitario: aceite.precioVenta || 0
                                                                            }
                                                                        }));
                                                                    }}
                                                                    placeholder="0"
                                                                />
                                                            </td>
                                                            <td>
                                                                <input
                                                                    type="number"
                                                                    className="form-control form-control-sm text-center"
                                                                    min="0"
                                                                    value={final || ''}
                                                                    onChange={(e) => {
                                                                        const val = parseInt(e.target.value) || 0;
                                                                        setLecturasFinalesAceites(prev => ({
                                                                            ...prev,
                                                                            [aceite.id]: val
                                                                        }));
                                                                    }}
                                                                    placeholder="0"
                                                                />
                                                            </td>
                                                            <td className="text-center fw-bold text-primary">{vendidos}</td>
                                                            <td className="text-end fw-bold text-success">${importe.toFixed(2)}</td>
                                                        </tr>
                                                    );
                                                })}
                                                </tbody>
                                                <tfoot className="table-light">
                                                <tr>
                                                    <td colSpan="5" className="text-end fw-bold">Total Aceites Vendidos:</td>
                                                    <td className="text-center fw-bold">{totalAceitesVendidos}</td>
                                                    <td className="text-end fw-bold text-success">${totalAceites.toFixed(2)}</td>
                                                </tr>
                                                </tfoot>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* ===== NOTAS DE CRÉDITO ===== */}
                            {dispensarioSeleccionado && !dispensarioSeleccionado.tieneCorte && (
                                <div className="card mb-3">
                                    <div className="card-header d-flex justify-content-between align-items-center">
                                        <h5 className="mb-0">📝 Notas de Crédito</h5>
                                        <button className="btn btn-primary btn-sm" onClick={agregarNotaCredito}>
                                            + Agregar Nota de Crédito
                                        </button>
                                    </div>
                                    <div className="card-body">
                                        {notasCredito.map((nota, index) => (
                                            <div key={index} className="card mb-2" style={{ backgroundColor: nota.estado === 'VENCIDO' ? '#fff3cd' : '#f8f9fa' }}>
                                                <div className="card-body">
                                                    {nota.estado === 'VENCIDO' && (
                                                        <div className="alert alert-danger alert-sm py-1 mb-2">
                                                            ⚠️ <strong>Cliente con crédito VENCIDO</strong> - Notificar a oficina
                                                        </div>
                                                    )}
                                                    <div className="row g-2">
                                                        <div className="col-md-2">
                                                            <label className="form-label small">Folio Nota *</label>
                                                            <div className="input-group input-group-sm">
                                                                <input
                                                                    type="text"
                                                                    className="form-control"
                                                                    value={nota.folioNota}
                                                                    onChange={(e) => {
                                                                        const nuevasNotas = [...notasCredito];
                                                                        nuevasNotas[index].folioNota = e.target.value;
                                                                        setNotasCredito(nuevasNotas);
                                                                    }}
                                                                    onBlur={(e) => {
                                                                        if (e.target.value && e.target.value.trim() !== '') {
                                                                            actualizarNotaCredito(index, 'folioNota', e.target.value);
                                                                        }
                                                                    }}
                                                                    placeholder="VENTA-20260616-..."
                                                                />
                                                                <button
                                                                    className="btn btn-outline-primary"
                                                                    type="button"
                                                                    onClick={() => {
                                                                        const folio = notasCredito[index].folioNota;
                                                                        if (folio && folio.trim() !== '') {
                                                                            actualizarNotaCredito(index, 'folioNota', folio);
                                                                        } else {
                                                                            alert('Ingrese un folio de venta primero');
                                                                        }
                                                                    }}
                                                                >
                                                                    🔍
                                                                </button>
                                                            </div>
                                                            <small className="text-muted">Ingrese el folio de la venta</small>
                                                        </div>

                                                        <div className="col-md-3">
                                                            <label className="form-label small">Cliente</label>
                                                            <select
                                                                className="form-select form-select-sm"
                                                                value={nota.clienteId || ''}
                                                                onChange={(e) => {
                                                                    const clienteId = e.target.value;
                                                                    actualizarNotaCredito(index, 'clienteId', clienteId);
                                                                    if (clienteId) {
                                                                        const cliente = clientesCredito.find(c => c.clienteId === parseInt(clienteId));
                                                                        if (cliente && cliente.estado === 'VENCIDO') {
                                                                            alert(`⚠️ ATENCIÓN: El cliente ${cliente.clienteNombre} tiene créditos VENCIDOS. Notificar a oficina.`);
                                                                        }
                                                                    }
                                                                }}
                                                            >
                                                                <option value="">Seleccione un cliente</option>
                                                                {clientesCredito.map(c => {
                                                                    const esVencido = c.estado === 'VENCIDO';
                                                                    return (
                                                                        <option key={c.id} value={c.clienteId} style={{
                                                                            backgroundColor: esVencido ? '#fff3cd' : 'white',
                                                                            color: esVencido ? '#856404' : 'black'
                                                                        }}>
                                                                            {c.clienteNombre} - Saldo: ${c.saldoPendiente?.toFixed(2)}
                                                                            {esVencido && ' ⚠️ VENCIDO'}
                                                                        </option>
                                                                    );
                                                                })}
                                                            </select>
                                                            <small className="text-muted">⚠️ = cliente con crédito vencido</small>
                                                        </div>

                                                        <div className="col-md-2">
                                                            <label className="form-label small">Tipo</label>
                                                            <select
                                                                className="form-select form-select-sm"
                                                                value={nota.tipoCombustible}
                                                                onChange={(e) => {
                                                                    const nuevasNotas = [...notasCredito];
                                                                    nuevasNotas[index].tipoCombustible = e.target.value;
                                                                    setNotasCredito(nuevasNotas);
                                                                }}
                                                            >
                                                                <option value="">❌ Seleccione un combustible</option>
                                                                    {combustibles.map(c => (
                                                                        <option key={c.id} value={c.tipo}>{c.nombre}</option>
                                                                    ))}
                                                            </select>
                                                        </div>

                                                        <div className="col-md-2">
                                                            <label className="form-label small">Litros</label>
                                                            <input
                                                                type="number"
                                                                step="0.001"
                                                                className="form-control form-control-sm"
                                                                value={nota.litros}
                                                                onChange={(e) => {
                                                                    const nuevasNotas = [...notasCredito];
                                                                    nuevasNotas[index].litros = e.target.value;
                                                                    setNotasCredito(nuevasNotas);
                                                                }}
                                                                placeholder="0.000"
                                                            />
                                                        </div>

                                                        <div className="col-md-2">
                                                            <label className="form-label small">Monto</label>
                                                            <input
                                                                type="number"
                                                                step="0.01"
                                                                className="form-control form-control-sm"
                                                                value={nota.monto}
                                                                onChange={(e) => {
                                                                    const nuevasNotas = [...notasCredito];
                                                                    nuevasNotas[index].monto = e.target.value;
                                                                    setNotasCredito(nuevasNotas);
                                                                }}
                                                                placeholder="0.00"
                                                            />
                                                        </div>

                                                        <div className="col-md-1 d-flex align-items-end">
                                                            <button className="btn btn-danger btn-sm" onClick={() => eliminarNotaCredito(index)}>
                                                                🗑️
                                                            </button>
                                                        </div>
                                                    </div>

                                                    <div className="row mt-2">
                                                        <div className="col-md-4">
                                                            <label className="form-label small">Autorizado Por</label>
                                                            <input
                                                                type="text"
                                                                className="form-control form-control-sm"
                                                                value={nota.autorizadoPor}
                                                                onChange={(e) => {
                                                                    const nuevasNotas = [...notasCredito];
                                                                    nuevasNotas[index].autorizadoPor = e.target.value;
                                                                    setNotasCredito(nuevasNotas);
                                                                }}
                                                                placeholder="Nombre del responsable"
                                                            />
                                                        </div>
                                                        <div className="col-md-8">
                                                            <small className="text-muted">
                                                                Cliente: <strong>{nota.clienteNombre || 'Ninguno'}</strong> |
                                                                Saldo: <strong className={nota.estado === 'VENCIDO' ? 'text-danger' : 'text-success'}>
                                                                ${nota.saldoPendiente?.toFixed(2) || '0.00'}
                                                            </strong>
                                                                {nota.estado === 'VENCIDO' && (
                                                                    <span className="badge bg-danger ms-2">⚠️ VENCIDO</span>
                                                                )}
                                                            </small>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {notasCredito.length === 0 && <p className="text-muted">No hay notas de crédito registradas</p>}
                                        <div className="alert alert-info mt-2">
                                            <strong>Total Notas de Crédito:</strong> ${totalNotas.toFixed(2)}
                                        </div>
                                    </div>
                                </div>
                            )}

                            {/* ===== RESUMEN DE PAGOS ===== */}
                            {dispensarioSeleccionado && !dispensarioSeleccionado.tieneCorte && (
                                <>
                                    <div className="card mb-3">
                                        <h3>💰 Resumen de Pagos</h3>
                                        <div className="row">
                                            <div className="col-md-4">
                                                <label className="form-label">Efectivo Recibido</label>
                                                <input
                                                    type="number"
                                                    step="0.01"
                                                    className="form-control"
                                                    value={efectivoRecibido}
                                                    onChange={(e) => setEfectivoRecibido(e.target.value)}
                                                    placeholder="0.00"
                                                />
                                            </div>
                                            <div className="col-md-4">
                                                <label className="form-label">Tarjeta</label>
                                                <input
                                                    type="number"
                                                    step="0.01"
                                                    className="form-control"
                                                    value={tarjetaRecibido}
                                                    onChange={(e) => setTarjetaRecibido(e.target.value)}
                                                    placeholder="0.00"
                                                />
                                            </div>
                                            <div className="col-md-4">
                                                <label className="form-label">Transferencia</label>
                                                <input
                                                    type="number"
                                                    step="0.01"
                                                    className="form-control"
                                                    value={transferenciaRecibido}
                                                    onChange={(e) => setTransferenciaRecibido(e.target.value)}
                                                    placeholder="0.00"
                                                />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="card mb-3">
                                        <h3>📋 Resumen Final del Corte</h3>
                                        <table className="table table-bordered">
                                            <tbody>
                                            <tr className="table-secondary">
                                                <td width="50%"><strong>Total Ventas Combustibles:</strong></td>
                                                <td className="text-end">${totalCombustibles.toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-secondary">
                                                <td><strong>Total Ventas Aceites y Aditivos:</strong></td>
                                                <td className="text-end">${totalAceites.toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-primary">
                                                <td><strong>Total Ventas (Combustibles + Aceites):</strong></td>
                                                <td className="text-end fw-bold">${totalVenta.toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-danger">
                                                <td><strong>Total Notas de Crédito:</strong></td>
                                                <td className="text-end">- $${totalNotas.toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-danger">
                                                <td><strong>Total Tarjeta:</strong></td>
                                                <td className="text-end">- $${(parseFloat(tarjetaRecibido) || 0).toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-danger">
                                                <td><strong>Total Transferencia:</strong></td>
                                                <td className="text-end">- $${(parseFloat(transferenciaRecibido) || 0).toFixed(2)}</td>
                                            </tr>
                                            <tr className="table-success">
                                                <td><strong>💰 EFECTIVO QUE DEBE ENTREGAR EL DESPACHADOR:</strong></td>
                                                <td className="text-end fw-bold text-success fs-4">${efectivoNeto.toFixed(2)}</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div className="card mb-3">
                                        <h3>📝 Observaciones</h3>
                                        <textarea
                                            className="form-control"
                                            rows="2"
                                            value={observaciones}
                                            onChange={(e) => setObservaciones(e.target.value)}
                                            placeholder="Notas adicionales sobre el corte..."
                                        ></textarea>
                                    </div>

                                    <div className="d-flex gap-2">
                                        <button className="btn btn-secondary" onClick={() => setPaso(1)}>← Volver</button>
                                        <button
                                            className="btn btn-success"
                                            onClick={handleProcesarCorte}
                                            disabled={procesando || !dispensarioSeleccionado || !despachadorSeleccionado}
                                        >
                                            {procesando ? 'Procesando...' : '✅ Procesar Corte'}
                                        </button>
                                    </div>
                                </>
                            )}
                        </>
                    )}
                </>
            )}
        </div>
    );
};

export default CorteTurnoForm;