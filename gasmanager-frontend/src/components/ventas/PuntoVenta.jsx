import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const PuntoVenta = () => {
    const navigate = useNavigate();
    const { isAdmin, user } = useAuth();

    // ===== ESTADOS EXISTENTES =====
    const [mangueras, setMangueras] = useState([]);
    const [dispensarios, setDispensarios] = useState([]);
    const [turnoActivo, setTurnoActivo] = useState(null);
    const [turnoLoaded, setTurnoLoaded] = useState(false);
    const [cargasActivas, setCargasActivas] = useState({});
    const [loading, setLoading] = useState(true);
    const [modoEntrada, setModoEntrada] = useState({});
    const [cantidadInput, setCantidadInput] = useState({});
    const [despachadores, setDespachadores] = useState([]);
    const [despachadorPorIsla, setDespachadorPorIsla] = useState({});
    const [resumenTurno, setResumenTurno] = useState({
        totalVentas: 0,
        totalLitros: 0,
        cantidadVentas: 0
    });
    const [resumenPorDispensario, setResumenPorDispensario] = useState({});

    // ===== NUEVO: ESTADO PARA CARGA EN CURSO DESDE SIMULADOR =====
    const [cargaActiva, setCargaActiva] = useState(null);
    const [cargaDispensarioId, setCargaDispensarioId] = useState(null);

    const intervalosRef = useRef({});
    const intervalTurnoRef = useRef(null);
    const intervalPreciosRef = useRef(null);

    // ===== NUEVO: CONSULTAR CARGA ACTIVA CADA 100ms =====
    useEffect(() => {
        const interval = setInterval(() => {
            if (cargaDispensarioId) {
                fetch(`/api/iot/carga-activa/${cargaDispensarioId}`)
                    .then(res => {
                        if (res.status === 204) {
                            setCargaActiva(null);
                            setCargaDispensarioId(null);
                            return null;
                        }
                        return res.json();
                    })
                    .then(data => {
                        if (data) {
                            setCargaActiva(data);
                            if (data.estado === 'COMPLETADA') {
                                setTimeout(() => {
                                    setCargaActiva(null);
                                    setCargaDispensarioId(null);
                                    obtenerResumenTurno();
                                }, 3000);
                            }
                        }
                    })
                    .catch(() => {});
            }
        }, 100);

        return () => clearInterval(interval);
    }, [cargaDispensarioId]);

    // ===== CARGAR DESPACHADORES DESDE NÓMINA =====
    const cargarDespachadores = async () => {
        try {
            const response = await fetch('/api/empleados/despachadores');
            if (response.ok) {
                const data = await response.json();
                setDespachadores(data);
                console.log('Despachadores cargados:', data);

                const saved = localStorage.getItem('despachadoresPorIsla');
                if (saved) {
                    try {
                        const parsed = JSON.parse(saved);
                        setDespachadorPorIsla(parsed);
                    } catch (e) {}
                }
            }
        } catch (error) {
            console.error('Error cargando despachadores:', error);
        }
    };

    // ===== VERIFICAR TURNO ACTIVO =====
    const verificarTurnoActivo = async () => {
        try {
            const response = await fetch('/api/turnos?estado=ABIERTO');
            if (response.ok) {
                const turnos = await response.json();
                if (turnos && turnos.length > 0) {
                    const turno = turnos[0];
                    setTurnoActivo(turno);
                    console.log('✅ Turno activo encontrado:', turno);
                    setTurnoLoaded(true);
                    return;
                }
            }
            console.log('⚠️ No hay turno activo');
            setTurnoActivo(null);
            setTurnoLoaded(true);
        } catch (error) {
            console.error('Error verificando turno:', error);
            setTurnoActivo(null);
            setTurnoLoaded(true);
        }
    };

    // ===== OBTENER RESUMEN DEL TURNO =====
    const obtenerResumenTurno = async () => {
        if (!turnoActivo) return;

        try {
            const response = await fetch(`/api/ventas/turno/${turnoActivo.id}`);
            if (response.ok) {
                const ventas = await response.json();
                console.log('📊 Ventas del turno:', ventas);

                const totalVentas = ventas.reduce((sum, v) => sum + (v.total || 0), 0);
                const totalLitros = ventas.reduce((sum, v) => {
                    const litros = v.detalles?.reduce((s, d) => s + (d.cantidad || 0), 0) || 0;
                    return sum + litros;
                }, 0);
                setResumenTurno({
                    totalVentas,
                    totalLitros,
                    cantidadVentas: ventas.length
                });

                const resumenPorDisp = {};

                ventas.forEach(v => {
                    let dispensarioId = null;
                    let dispensarioNombre = null;

                    if (v.detalles && v.detalles.length > 0) {
                        for (const detalle of v.detalles) {
                            if (detalle.productoId) {
                                const manguera = mangueras.find(m => m.id === detalle.productoId);
                                if (manguera) {
                                    dispensarioId = manguera.dispensarioId;
                                    dispensarioNombre = manguera.dispensarioNombre;
                                    break;
                                }
                            }
                        }
                    }

                    if (!dispensarioId && v.surtidorId) {
                        const manguera = mangueras.find(m => m.id === v.surtidorId);
                        if (manguera) {
                            dispensarioId = manguera.dispensarioId;
                            dispensarioNombre = manguera.dispensarioNombre;
                        }
                    }

                    if (!dispensarioId && v.dispensarioId) {
                        dispensarioId = v.dispensarioId;
                        dispensarioNombre = v.dispensarioNombre || 'Dispensario ' + dispensarioId;
                    }

                    if (!dispensarioId) {
                        console.warn('⚠️ Venta sin dispensarioId:', v);
                        return;
                    }

                    if (!resumenPorDisp[dispensarioId]) {
                        resumenPorDisp[dispensarioId] = {
                            ventas: 0,
                            litros: 0,
                            total: 0,
                            nombre: dispensarioNombre || 'Dispensario ' + dispensarioId,
                            porTipo: {
                                MAGNA: { litros: 0, total: 0, ventas: 0 },
                                PREMIUM: { litros: 0, total: 0, ventas: 0 },
                                DIESEL: { litros: 0, total: 0, ventas: 0 }
                            }
                        };
                    }

                    resumenPorDisp[dispensarioId].ventas += 1;
                    resumenPorDisp[dispensarioId].total += v.total || 0;

                    if (v.detalles) {
                        v.detalles.forEach(detalle => {
                            const cantidad = detalle.cantidad || 0;
                            const importe = detalle.importe || 0;
                            resumenPorDisp[dispensarioId].litros += cantidad;

                            let tipo = 'MAGNA';
                            const nombre = detalle.productoNombre || '';
                            const tipoProducto = detalle.tipoProducto || '';

                            if (nombre.includes('PREMIUM') || tipoProducto.includes('PREMIUM')) {
                                tipo = 'PREMIUM';
                            } else if (nombre.includes('DIESEL') || tipoProducto.includes('DIESEL')) {
                                tipo = 'DIESEL';
                            } else if (nombre.includes('MAGNA') || tipoProducto.includes('MAGNA')) {
                                tipo = 'MAGNA';
                            }

                            if (resumenPorDisp[dispensarioId].porTipo[tipo]) {
                                resumenPorDisp[dispensarioId].porTipo[tipo].litros += cantidad;
                                resumenPorDisp[dispensarioId].porTipo[tipo].total += importe;
                                resumenPorDisp[dispensarioId].porTipo[tipo].ventas += 1;
                            }
                        });
                    }
                });

                console.log('📊 Resumen por dispensario:', resumenPorDisp);
                setResumenPorDispensario(resumenPorDisp);
            }
        } catch (error) {
            console.error('Error obteniendo resumen:', error);
        }
    };

    // ===== RECARGAR PRECIOS =====
    const recargarPrecios = async () => {
        try {
            const preciosResponse = await fetch('/api/precios/combustibles');
            if (!preciosResponse.ok) throw new Error('Error obteniendo precios');
            const preciosData = await preciosResponse.json();
            const preciosMap = {};
            if (Array.isArray(preciosData)) {
                preciosData.forEach(p => {
                    preciosMap[p.tipo] = p.precioActual;
                });
            }

            setMangueras(prevMangueras =>
                prevMangueras.map(m => ({
                    ...m,
                    precio: preciosMap[m.tipoCombustible] || m.precio
                }))
            );

            setCargasActivas(prevCargas => {
                const nuevasCargas = { ...prevCargas };
                Object.keys(nuevasCargas).forEach(id => {
                    const manguera = mangueras.find(m => m.id === parseInt(id));
                    if (manguera && nuevasCargas[id]) {
                        nuevasCargas[id] = {
                            ...nuevasCargas[id],
                            precioUnitario: preciosMap[manguera.tipoCombustible] || manguera.precio
                        };
                    }
                });
                return nuevasCargas;
            });
        } catch (error) {
            console.error('Error recargando precios:', error);
        }
    };

    // ===== CARGAR DISPENSARIOS Y MANGUERAS =====
    const cargarDispensarios = async () => {
        setLoading(true);
        try {
            const response = await fetch('/api/dispensarios/completos');
            if (!response.ok) throw new Error('Error cargando dispensarios');
            const dispensariosData = await response.json();

            console.log('🏗️ Dispensarios completos:', dispensariosData);

            if (!dispensariosData || dispensariosData.length === 0) {
                setMangueras([]);
                setDispensarios([]);
                setLoading(false);
                return;
            }

            const preciosResponse = await fetch('/api/precios/combustibles');
            const preciosData = await preciosResponse.json();
            const preciosMap = {};
            if (Array.isArray(preciosData)) {
                preciosData.forEach(p => {
                    preciosMap[p.tipo] = p.precioActual;
                });
            }

            const dispensariosMap = {};
            const todasLasMangueras = [];

            dispensariosData.forEach(d => {
                if (!d.activo) return;

                const manguerasDelDispensario = [];

                if (d.caras) {
                    d.caras.forEach(cara => {
                        if (cara.mangueras) {
                            cara.mangueras.forEach(m => {
                                if (!m.activo) return;
                                if (!m.tipoCombustible) return;

                                // Solo se muestra si el combustible existe en el catalogo.
                                // Si no hay combustible dado de alta, la manguera no se muestra.
                                const precio = preciosMap[m.tipoCombustible];
                                if (precio == null) return;

                                let nombreCombustible = m.tipoCombustible;
                                if (m.tipoCombustible === 'MAGNA') nombreCombustible = 'Magna';
                                else if (m.tipoCombustible === 'PREMIUM') nombreCombustible = 'Premium';
                                else if (m.tipoCombustible === 'DIESEL') nombreCombustible = 'Diesel';

                                const manguera = {
                                    id: m.id,
                                    codigo: m.codigo || '01',
                                    nombre: m.nombre || 'Manguera ' + m.codigo,
                                    nombreCompleto: `${d.nombre} - ${cara.nombre || ''} - ${m.nombre || m.codigo}`,
                                    tipoCombustible: m.tipoCombustible,
                                    nombreCombustible: nombreCombustible,
                                    precio: precio,
                                    activo: m.activo,
                                    lecturaActual: m.lecturaActual || 0,
                                    dispensarioId: d.id,
                                    dispensarioNombre: d.nombre,
                                    dispensarioNumero: d.numero || '',
                                    caraNombre: cara.nombre || '',
                                    numero: m.codigo || '01'
                                };

                                manguerasDelDispensario.push(manguera);
                                todasLasMangueras.push(manguera);
                            });
                        }
                    });
                }

                if (manguerasDelDispensario.length > 0) {
                    dispensariosMap[d.id] = {
                        id: d.id,
                        nombre: d.nombre || 'Dispensario ' + d.id,
                        numero: d.numero || '',
                        mangueras: manguerasDelDispensario
                    };
                }
            });

            const dispensariosArray = Object.values(dispensariosMap);
            setDispensarios(dispensariosArray);
            setMangueras(todasLasMangueras);

            console.log('🏗️ Dispensarios procesados:', dispensariosArray.length);
            console.log('📊 Total mangueras:', todasLasMangueras.length);

            const cargasInicial = {};
            const modoInicial = {};
            const cantidadInicial = {};

            todasLasMangueras.forEach(m => {
                cargasInicial[m.id] = {
                    litros: 0,
                    total: 0,
                    estado: 'DISPONIBLE',
                    precioUnitario: m.precio,
                    progreso: 0
                };
                modoInicial[m.id] = 'litros';
                cantidadInicial[m.id] = '';
            });

            setCargasActivas(cargasInicial);
            setModoEntrada(modoInicial);
            setCantidadInput(cantidadInicial);

        } catch (error) {
            console.error('Error cargando dispensarios:', error);
            setMangueras([]);
            setDispensarios([]);
        } finally {
            setLoading(false);
        }
    };

    // ===== MAPEAR TIPO DE PRODUCTO =====
    const mapearTipoProducto = (tipo) => {
        switch(tipo) {
            case 'MAGNA': return 'COMBUSTIBLE_GASOLINA_MAGNA';
            case 'PREMIUM': return 'COMBUSTIBLE_GASOLINA_PREMIUM';
            case 'DIESEL': return 'COMBUSTIBLE_DIESEL';
            default: return 'COMBUSTIBLE_GASOLINA_MAGNA';
        }
    };

    // ===== REGISTRAR VENTA =====
    const registrarVenta = async (ventaData) => {
        if (!turnoActivo) {
            alert('⚠️ No hay un turno activo. El supervisor debe abrir un turno antes de comenzar a vender.');
            return false;
        }

        console.log('📦 ventaData recibido:', ventaData);

        const total = ventaData.total;
        const subtotal = total / 1.16;
        const iva = total - subtotal;
        const tipoProducto = mapearTipoProducto(ventaData.tipoCombustible);

        const despachadorIsla = despachadorPorIsla[ventaData.dispensarioId] || null;

        const ventaCompleta = {
            turnoId: turnoActivo.id,
            metodoPago: 'EFECTIVO',
            subtotal: parseFloat(subtotal.toFixed(2)),
            iva: parseFloat(iva.toFixed(2)),
            total: parseFloat(total.toFixed(2)),
            surtidorId: ventaData.surtidorId,
            surtidorNumero: ventaData.surtidorNumero || '01',
            despachadorId: despachadorIsla?.id || 1,
            despachadorNombre: despachadorIsla ? despachadorIsla.nombre + ' ' + despachadorIsla.apellidoPaterno : 'SISTEMA',
            dispensarioId: ventaData.dispensarioId,
            dispensarioNombre: ventaData.dispensarioNombre,
            detalles: [{
                tipoProducto: tipoProducto,
                productoId: ventaData.surtidorId,
                productoNombre: ventaData.tipoCombustible,
                cantidad: parseFloat(ventaData.litros.toFixed(3)),
                precioUnitario: parseFloat((ventaData.total / ventaData.litros).toFixed(2)),
                importe: parseFloat(ventaData.total.toFixed(2)),
                unidadMedida: 'LITROS'
            }]
        };

        console.log('📤 Venta a enviar:', ventaCompleta);

        try {
            const response = await fetch('/api/ventas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ventaCompleta)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP ${response.status}: ${errorText}`);
            }

            await obtenerResumenTurno();
            return true;
        } catch (error) {
            console.error('❌ Error registrando venta:', error);
            alert('Error al registrar la venta: ' + error.message);
            return false;
        }
    };

    // ===== GUARDAR DESPACHADOR POR ISLA =====
    const handleDespachadorChange = (dispensarioId, despachadorId) => {
        const despachador = despachadores.find(d => d.id === parseInt(despachadorId));
        if (despachador) {
            const nuevos = { ...despachadorPorIsla, [dispensarioId]: despachador };
            setDespachadorPorIsla(nuevos);
            localStorage.setItem('despachadoresPorIsla', JSON.stringify(nuevos));
            console.log('Despachador asignado a isla:', despachador);
        }
    };

    // ===== CERRAR TURNO =====
    const handleCerrarTurno = async () => {
        if (!turnoActivo) {
            alert('⚠️ No hay un turno activo para cerrar.');
            return;
        }

        if (window.confirm(`¿Estás seguro de cerrar el turno "${turnoActivo.nombre}"?\n\nSe registrará el cierre y no se podrán hacer más ventas en este turno.`)) {
            try {
                const response = await fetch(`/api/turnos/${turnoActivo.id}/cerrar`, {
                    method: 'POST'
                });

                if (response.ok) {
                    alert('✅ Turno cerrado exitosamente');
                    await verificarTurnoActivo();
                    navigate('/cortes');
                } else {
                    const error = await response.json();
                    alert('❌ Error al cerrar turno: ' + (error.message || 'Error desconocido'));
                }
            } catch (error) {
                console.error('Error cerrando turno:', error);
                alert('❌ Error al cerrar turno: ' + error.message);
            }
        }
    };

    // ===== IR A GESTIÓN DE TURNOS =====
    const irAGestionTurnos = () => {
        navigate('/turnos');
    };

    // ===== EFECTOS =====
    useEffect(() => {
        cargarDespachadores();
        verificarTurnoActivo();
        cargarDispensarios();

        intervalTurnoRef.current = setInterval(() => {
            verificarTurnoActivo();
        }, 10000);

        intervalPreciosRef.current = setInterval(() => {
            recargarPrecios();
        }, 30000);

        const handlePreciosActualizados = () => {
            recargarPrecios();
        };
        window.addEventListener('preciosActualizados', handlePreciosActualizados);

        return () => {
            if (intervalTurnoRef.current) clearInterval(intervalTurnoRef.current);
            if (intervalPreciosRef.current) clearInterval(intervalPreciosRef.current);
            window.removeEventListener('preciosActualizados', handlePreciosActualizados);
            Object.values(intervalosRef.current).forEach(interval => {
                if (interval) clearInterval(interval);
            });
        };
    }, []);

    useEffect(() => {
        if (turnoActivo && mangueras.length > 0) {
            obtenerResumenTurno();
        }
    }, [turnoActivo, mangueras]);

    // ===== CAMBIAR MODO DE ENTRADA =====
    const cambiarModo = (id, modo) => {
        setModoEntrada(prev => ({ ...prev, [id]: modo }));
        setCantidadInput(prev => ({ ...prev, [id]: '' }));
    };

    const cambiarCantidad = (id, valor) => {
        setCantidadInput(prev => ({ ...prev, [id]: valor }));
    };

    const calcularPreview = (id) => {
        const m = mangueras.find(m => m.id === id);
        if (!m) return { litros: 0, total: 0 };
        const cantidad = parseFloat(cantidadInput[id]);
        if (isNaN(cantidad) || cantidad <= 0) return { litros: 0, total: 0 };
        if (modoEntrada[id] === 'litros') {
            return { litros: cantidad, total: cantidad * m.precio };
        } else {
            return { litros: cantidad / m.precio, total: cantidad };
        }
    };

    // ===== INICIAR CARGA =====
    const iniciarCarga = (id) => {
        if (!turnoActivo) {
            alert('⚠️ No hay turno activo. El supervisor debe abrir un turno.');
            return;
        }

        const m = mangueras.find(m => m.id === id);
        if (!m) return;

        console.log('🔍 Manguera m:', m);
        console.log('🔍 dispensarioId:', m.dispensarioId);
        console.log('🔍 dispensarioNombre:', m.dispensarioNombre);

        const cantidad = parseFloat(cantidadInput[id]);
        if (isNaN(cantidad) || cantidad <= 0) {
            alert(`Ingrese cantidad válida en ${modoEntrada[id] === 'litros' ? 'litros' : 'pesos'}`);
            return;
        }
        if (cargasActivas[id]?.estado === 'EN_CURSO') {
            alert('Ya hay una carga en curso');
            return;
        }

        let totalLitros = modoEntrada[id] === 'litros' ? cantidad : cantidad / m.precio;
        const totalPesos = totalLitros * m.precio;

        setCargasActivas(prev => ({
            ...prev,
            [id]: { litros: 0, total: 0, estado: 'EN_CURSO', totalLitros, progreso: 0, precioUnitario: m.precio }
        }));

        let litrosActual = 0;
        const incremento = totalLitros / 40;

        const intervalo = setInterval(() => {
            litrosActual += incremento;
            if (litrosActual >= totalLitros) {
                litrosActual = totalLitros;
                clearInterval(intervalo);
                delete intervalosRef.current[id];
                setCargasActivas(prev => ({ ...prev, [id]: { ...prev[id], litros: totalLitros, total: totalPesos, estado: 'COMPLETADA', progreso: 100 } }));

                registrarVenta({
                    tipoCombustible: m.tipoCombustible,
                    litros: totalLitros,
                    total: totalPesos,
                    surtidorId: id,
                    surtidorNumero: m.numero,
                    dispensarioId: m.dispensarioId,
                    dispensarioNombre: m.dispensarioNombre
                });
                setCantidadInput(prev => ({ ...prev, [id]: '' }));
                setTimeout(() => {
                    setCargasActivas(prev => ({ ...prev, [id]: { litros: 0, total: 0, estado: 'DISPONIBLE', progreso: 0, precioUnitario: m.precio } }));
                }, 2000);
            } else {
                setCargasActivas(prev => ({
                    ...prev,
                    [id]: { ...prev[id], litros: litrosActual, total: litrosActual * m.precio, progreso: (litrosActual / totalLitros) * 100 }
                }));
            }
        }, 50);
        intervalosRef.current[id] = intervalo;
    };

    const detenerCarga = (id) => {
        if (intervalosRef.current[id]) {
            clearInterval(intervalosRef.current[id]);
            delete intervalosRef.current[id];
        }
        setCargasActivas(prev => ({ ...prev, [id]: { ...prev[id], estado: 'DETENIDA' } }));
        setTimeout(() => {
            setCargasActivas(prev => ({ ...prev, [id]: { litros: 0, total: 0, estado: 'DISPONIBLE', progreso: 0, precioUnitario: prev[id]?.precioUnitario || 0 } }));
        }, 1500);
    };

    const getEstadoBadge = (estado) => {
        switch(estado) {
            case 'EN_CURSO': return <span className="badge bg-warning text-dark animate-pulse">🟡 EN CURSO</span>;
            case 'COMPLETADA': return <span className="badge bg-success">✅ LISTO</span>;
            case 'DETENIDA': return <span className="badge bg-danger">⛔ DETENIDA</span>;
            default: return <span className="badge bg-secondary">⚪ DISPONIBLE</span>;
        }
    };

    // ===== RENDER =====
    if (!isAdmin) return <div className="alert alert-danger m-3">No tienes permisos</div>;
    if (loading || !turnoLoaded) return <div className="text-center py-5"><div className="spinner-border text-primary" /><p>Cargando...</p></div>;

    return (
        <div className="container-fluid py-3" style={{ background: '#f0f2f5', minHeight: '100vh' }}>

            {/* ===== BARRA GENERAL DE TOTALES CON BOTONES ===== */}
            <div className="card border-0 shadow-sm rounded-4 mb-3" style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)' }}>
                <div className="card-body py-2 px-3">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <div>
                            <small className="text-white-50">TURNO ACTIVO</small>
                            <div className="text-white fw-bold">
                                {turnoActivo ? `${turnoActivo.codigoTurno} - ${turnoActivo.nombre}` : 'SIN TURNO'}
                            </div>
                        </div>
                        <div>
                            <small className="text-white-50">VENTAS</small>
                            <div className="text-white fw-bold fs-5">{resumenTurno.cantidadVentas}</div>
                        </div>
                        <div>
                            <small className="text-white-50">LITROS</small>
                            <div className="text-white fw-bold fs-5">{resumenTurno.totalLitros.toFixed(1)}</div>
                        </div>
                        <div>
                            <small className="text-white-50">TOTAL</small>
                            <div className="text-white fw-bold fs-5">${resumenTurno.totalVentas.toFixed(0)}</div>
                        </div>
                        <div className="d-flex gap-2">
                            {turnoActivo && (
                                <button
                                    className="btn btn-danger btn-sm"
                                    onClick={handleCerrarTurno}
                                    title="Cerrar turno actual"
                                >
                                    🔒 Cerrar Turno
                                </button>
                            )}
                            <button
                                className="btn btn-info btn-sm"
                                onClick={irAGestionTurnos}
                                title="Ir a Gestión de Turnos"
                            >
                                📋 Turnos
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* ===== CARGA EN CURSO DESDE SIMULADOR ===== */}
            {cargaActiva && cargaActiva.estado === 'EN_CURSO' && (
                <div className="card border-warning shadow-lg mb-4" style={{ borderLeft: '4px solid #ffc107' }}>
                    <div className="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                        <span><span className="text-warning">🔴</span> CARGA EN CURSO DESDE SIMULADOR</span>
                        <span className="badge bg-warning text-dark animate-pulse">⏳ EN CURSO</span>
                    </div>
                    <div className="card-body" style={{ background: '#1a1a2e' }}>
                        <div className="row">
                            <div className="col-md-3">
                                <small className="text-secondary">📍 Dispensario</small>
                                <h5 className="text-white">#{cargaActiva.dispensarioId}</h5>
                            </div>
                            <div className="col-md-3">
                                <small className="text-secondary">⛽ Combustible</small>
                                <h5 className="text-white">{cargaActiva.tipoCombustible}</h5>
                            </div>
                            <div className="col-md-3">
                                <small className="text-secondary">🧑‍💼 Despachador</small>
                                <h5 className="text-white">{cargaActiva.despachadorNombre || 'Asignado en isla'}</h5>
                            </div>
                            <div className="col-md-3">
                                <small className="text-secondary">📊 Progreso</small>
                                <h5 className="text-warning">{cargaActiva.progreso || 0}%</h5>
                            </div>
                        </div>
                        <hr className="border-secondary" />
                        <div className="row">
                            <div className="col-md-6 text-center">
                                <div className="bg-black rounded-3 p-3 border border-primary">
                                    <small className="text-secondary">📏 LITROS</small>
                                    <h2 className="text-primary fw-bold font-monospace">
                                        {cargaActiva.litros?.toFixed(3) || '0.000'}
                                    </h2>
                                </div>
                            </div>
                            <div className="col-md-6 text-center">
                                <div className="bg-black rounded-3 p-3 border border-success">
                                    <small className="text-secondary">💰 TOTAL</small>
                                    <h2 className="text-success fw-bold font-monospace">
                                        ${cargaActiva.total?.toFixed(2) || '0.00'}
                                    </h2>
                                </div>
                            </div>
                        </div>
                        <div className="progress mt-3" style={{ height: '10px', borderRadius: '10px' }}>
                            <div
                                className="progress-bar progress-bar-striped progress-bar-animated bg-success"
                                style={{ width: `${cargaActiva.progreso || 0}%`, borderRadius: '10px' }}
                            >
                            </div>
                        </div>
                        <div className="text-end mt-1">
                            <small className="text-secondary">⏱ Tiempo estimado: {Math.round((cargaActiva.progreso || 0) / 10)}s</small>
                        </div>
                    </div>
                </div>
            )}

            {!turnoActivo && (
                <div className="alert alert-warning py-2 mb-3" style={{ fontSize: '13px' }}>
                    ⚠️ No hay turno activo. <a href="/turnos" className="alert-link">Abrir turno</a>
                </div>
            )}

            {!turnoActivo ? (
                <div className="alert alert-warning text-center">No hay turno activo. Abra un turno para comenzar a vender.</div>
            ) : dispensarios.length === 0 ? (
                <div className="alert alert-info text-center">
                    <h5>⚠️ No hay mangueras activas configuradas</h5>
                    <p>El administrador debe crear dispensarios y configurar los tipos de combustible.</p>
                    <button className="btn btn-primary" onClick={() => window.location.href = '/dispensarios'}>Configurar Dispensarios</button>
                </div>
            ) : (
                dispensarios.map(dispensario => {
                    const resumen = resumenPorDispensario[dispensario.id] || {
                        ventas: 0,
                        litros: 0,
                        total: 0,
                        porTipo: {
                            MAGNA: { litros: 0, total: 0, ventas: 0 },
                            PREMIUM: { litros: 0, total: 0, ventas: 0 },
                            DIESEL: { litros: 0, total: 0, ventas: 0 }
                        }
                    };
                    const despachadorIsla = despachadorPorIsla[dispensario.id] || null;

                    return (
                        <div key={dispensario.id} className="card border-0 shadow-sm rounded-4 mb-4">
                            {/* ===== HEADER DEL DISPENSARIO CON DESGLOSE ===== */}
                            <div className="card-header bg-dark text-white rounded-top-4">
                                <div className="d-flex justify-content-between align-items-center flex-wrap">
                                    <h5 className="mb-0">⛽ {dispensario.nombre}</h5>
                                    <div className="d-flex gap-2 flex-wrap">
                                        <span className="badge bg-info">{resumen.ventas} ventas</span>
                                        <span className="badge bg-warning text-dark">{resumen.litros.toFixed(1)} L</span>
                                        <span className="badge bg-success">${resumen.total.toFixed(2)}</span>
                                    </div>
                                </div>
                                {/* ===== DESGLOSE POR TIPO DE COMBUSTIBLE ===== */}
                                <div className="mt-2 d-flex gap-2 flex-wrap" style={{ fontSize: '12px' }}>
                                    {resumen.porTipo && Object.entries(resumen.porTipo).map(([tipo, datos]) => {
                                        if (datos.litros === 0 && datos.total === 0) return null;
                                        const colores = {
                                            MAGNA: { bg: '#fd7e14', text: 'MAGNA' },
                                            PREMIUM: { bg: '#0dcaf0', text: 'PREMIUM' },
                                            DIESEL: { bg: '#6f42c1', text: 'DIESEL' }
                                        };
                                        const color = colores[tipo] || { bg: '#6c757d', text: tipo };
                                        return (
                                            <span key={tipo} className="badge" style={{ backgroundColor: color.bg, color: 'white', padding: '4px 8px' }}>
                                                {color.text}: {datos.litros.toFixed(1)}L (${datos.total.toFixed(0)})
                                            </span>
                                        );
                                    })}
                                    {(!resumen.porTipo || Object.values(resumen.porTipo).every(d => d.litros === 0 && d.total === 0)) && (
                                        <span className="text-light small">Sin ventas en este turno</span>
                                    )}
                                </div>
                            </div>

                            <div className="card-body">
                                {/* ===== SELECTOR DE DESPACHADOR ===== */}
                                <div className="mb-3 p-2 bg-light rounded-3">
                                    <div className="row align-items-center">
                                        <div className="col-md-2">
                                            <strong className="text-secondary">Despachador:</strong>
                                        </div>
                                        <div className="col-md-4">
                                            <select
                                                className="form-select form-select-sm"
                                                value={despachadorIsla?.id || ''}
                                                onChange={(e) => handleDespachadorChange(dispensario.id, e.target.value)}
                                            >
                                                <option value="">Seleccionar despachador</option>
                                                {despachadores
                                                    .filter(d => d.activo === true)
                                                    .map(d => (
                                                        <option key={d.id} value={d.id}>
                                                            {d.nombre} {d.apellidoPaterno}
                                                            {d.dispensarioNombre ? ` - ${d.dispensarioNombre}` : ''}
                                                        </option>
                                                    ))}
                                            </select>
                                        </div>
                                        <div className="col-md-6">
                                            <small className="text-muted">
                                                {despachadorIsla ?
                                                    `✅ Despachador asignado: ${despachadorIsla.nombre} ${despachadorIsla.apellidoPaterno}` :
                                                    '⚠️ Seleccione un despachador para esta isla'}
                                            </small>
                                        </div>
                                    </div>
                                </div>

                                {/* ===== MANGUERAS ===== */}
                                <div className="row g-3">
                                    {dispensario.mangueras.map(m => {
                                        const carga = cargasActivas[m.id] || { litros: 0, total: 0, estado: 'DISPONIBLE', progreso: 0 };
                                        const preview = calcularPreview(m.id);
                                        const enCurso = carga.estado === 'EN_CURSO';
                                        const completada = carga.estado === 'COMPLETADA';

                                        return (
                                            <div key={m.id} className="col-xl-3 col-lg-4 col-md-6 col-sm-12">
                                                <div className="card h-100" style={{
                                                    background: '#1e293b',
                                                    borderRadius: '12px',
                                                    border: `1px solid ${enCurso ? '#f59e0b' : completada ? '#10b981' : '#334155'}`
                                                }}>
                                                    <div className="card-header text-center" style={{
                                                        background: enCurso ? '#2d1f00' : completada ? '#0a2e1a' : '#0f172a',
                                                        borderBottom: `1px solid ${enCurso ? '#f59e0b' : completada ? '#10b981' : '#334155'}`
                                                    }}>
                                                        <h6 className="text-white mb-0">🔌 {m.nombre}</h6>
                                                        <small className="text-secondary">{m.nombreCombustible}</small>
                                                    </div>
                                                    <div className="card-body text-center">
                                                        {enCurso && carga.progreso > 0 && (
                                                            <div className="progress mb-2" style={{ height: '4px' }}>
                                                                <div className="progress-bar bg-warning" style={{ width: `${carga.progreso}%` }}></div>
                                                            </div>
                                                        )}

                                                        <div className="bg-dark rounded-3 p-2 mb-2">
                                                            <div className="row">
                                                                <div className="col-6">
                                                                    <small className="text-secondary">LITROS</small>
                                                                    <div className="text-warning fw-bold fs-4">
                                                                        {enCurso ? carga.litros.toFixed(2) : preview.litros.toFixed(2)}
                                                                    </div>
                                                                </div>
                                                                <div className="col-6">
                                                                    <small className="text-secondary">TOTAL</small>
                                                                    <div className="text-success fw-bold fs-4">
                                                                        ${enCurso ? carga.total.toFixed(2) : preview.total.toFixed(2)}
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div className="mt-1">
                                                                <small className="text-secondary">PRECIO/L</small>
                                                                <p className="text-white mb-0">${m.precio.toFixed(2)}</p>
                                                            </div>
                                                        </div>

                                                        <div className="mb-2">
                                                            <span className="badge bg-info">{m.nombreCombustible}</span>
                                                        </div>

                                                        {!enCurso && !completada && (
                                                            <>
                                                                <div className="d-flex gap-1 mb-2">
                                                                    <button className={`btn btn-sm flex-fill ${modoEntrada[m.id] === 'litros' ? 'btn-success' : 'btn-secondary'}`} onClick={() => cambiarModo(m.id, 'litros')}>📏 LITROS</button>
                                                                    <button className={`btn btn-sm flex-fill ${modoEntrada[m.id] === 'importe' ? 'btn-success' : 'btn-secondary'}`} onClick={() => cambiarModo(m.id, 'importe')}>💰 IMPORTE</button>
                                                                </div>
                                                                <div className="input-group input-group-sm mb-2">
                                                                    <span className="input-group-text bg-dark text-white">{modoEntrada[m.id] === 'litros' ? 'L' : '$'}</span>
                                                                    <input type="number" step="0.001" className="form-control bg-dark text-white border-secondary" value={cantidadInput[m.id] || ''} onChange={(e) => cambiarCantidad(m.id, e.target.value)} placeholder={modoEntrada[m.id] === 'litros' ? 'Litros' : 'Pesos'} />
                                                                </div>
                                                            </>
                                                        )}

                                                        <div className="mb-2">{getEstadoBadge(carga.estado)}</div>

                                                        <button className={`btn w-100 ${carga.estado === 'DISPONIBLE' ? 'btn-success' : carga.estado === 'EN_CURSO' ? 'btn-danger' : 'btn-secondary'}`} disabled={carga.estado !== 'DISPONIBLE' && carga.estado !== 'EN_CURSO'} onClick={() => carga.estado === 'DISPONIBLE' ? iniciarCarga(m.id) : detenerCarga(m.id)}>
                                                            {carga.estado === 'DISPONIBLE' ? '▶ INICIAR' : carga.estado === 'EN_CURSO' ? '⏹ DETENER' : '✅ COMPLETADA'}
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>

                                {/* ===== TOTAL DE LA ISLA ===== */}
                                <div className="mt-3 p-2 bg-light rounded-3 text-center">
                                    <strong>📊 Total {dispensario.nombre}:</strong>
                                    <span className="text-primary fw-bold ms-2">{resumen.litros.toFixed(1)} L</span>
                                    <span className="text-success fw-bold ms-3">${resumen.total.toFixed(2)}</span>
                                    <span className="text-muted ms-3">({resumen.ventas} ventas)</span>
                                </div>
                            </div>
                        </div>
                    );
                })
            )}

            <style>{`
                .animate-pulse { animation: pulse 1.5s infinite; }
                @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.6; } }
                input[type="number"]::-webkit-inner-spin-button { opacity: 0.5; }
            `}</style>
        </div>
    );
};

export default PuntoVenta;