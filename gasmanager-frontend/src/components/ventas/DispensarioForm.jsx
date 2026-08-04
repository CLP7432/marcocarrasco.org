import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { dispensariosService } from '../../api/ventas/auth';

const DispensarioForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [cargandoDatos, setCargandoDatos] = useState(false);

    const [formData, setFormData] = useState({
        numero: '',
        nombre: '',
        ubicacion: '',
        estado: 'ACTIVO'
    });

    // SIEMPRE 4 MANGUERAS
    const [mangueras, setMangueras] = useState([
        { id: null, cara: 'A', codigo: 'A1', nombre: 'Manguera A1', tipoCombustible: '', combustibleId: null },
        { id: null, cara: 'A', codigo: 'A2', nombre: 'Manguera A2', tipoCombustible: '', combustibleId: null },
        { id: null, cara: 'B', codigo: 'B1', nombre: 'Manguera B1', tipoCombustible: '', combustibleId: null },
        { id: null, cara: 'B', codigo: 'B2', nombre: 'Manguera B2', tipoCombustible: '', combustibleId: null }
    ]);

    const [combustibles, setCombustibles] = useState([]);

    // Los tipos de combustible salen del catalogo (no hardcodeados).
    // Si no hay combustibles dados de alta, el select queda solo con "Seleccione".
    const opcionesCombustible = [
        { value: '', label: '❌ Seleccione un combustible' },
        ...combustibles.map(c => ({ value: c.tipo, label: `⛽ ${c.nombre} (${c.tipo})` }))
    ];

    useEffect(() => {
        cargarCombustibles();
        if (id) {
            cargarDispensario();
        }
    }, [id]);

    const cargarCombustibles = async () => {
        try {
            const response = await fetch('/api/precios/combustibles');
            if (response.ok) {
                const data = await response.json();
                if (Array.isArray(data)) {
                    setCombustibles(data);
                }
            }
        } catch (error) {
            console.error('Error cargando combustibles:', error);
        }
    };

    const cargarDispensario = async () => {
        setCargandoDatos(true);
        try {
            const data = await dispensariosService.obtenerCompleto(id);

            setFormData({
                numero: data.numero,
                nombre: data.nombre,
                ubicacion: data.ubicacion || '',
                estado: data.estado || 'ACTIVO'
            });

            // Cargar mangueras existentes
            if (data.caras && data.caras.length > 0) {
                const nuevasMangueras = [...mangueras];
                for (const cara of data.caras) {
                    if (cara.mangueras && cara.mangueras.length > 0) {
                        for (const m of cara.mangueras) {
                            const index = nuevasMangueras.findIndex(mg => mg.codigo === m.codigo);
                            if (index !== -1) {
                                nuevasMangueras[index] = {
                                    ...nuevasMangueras[index],
                                    id: m.id,
                                    tipoCombustible: m.tipoCombustible || '',
                                    combustibleId: m.combustibleId
                                };
                            }
                        }
                    }
                }
                setMangueras(nuevasMangueras);
            }
        } catch (error) {
            console.error('Error cargando dispensario:', error);
            alert('Error al cargar el dispensario');
        }
        setCargandoDatos(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleMangueraCombustibleChange = (index, tipoCombustible) => {
        const nuevasMangueras = [...mangueras];
        const combustible = combustibles.find(c => c.tipo === tipoCombustible);
        nuevasMangueras[index] = {
            ...nuevasMangueras[index],
            tipoCombustible: tipoCombustible,
            combustibleId: combustible?.id || null
        };
        setMangueras(nuevasMangueras);
    };

    const construirDispensarioParaEnvio = () => {
        // Agrupar mangueras por cara (A y B)
        const carasMap = new Map();

        for (const m of mangueras) {
            if (!carasMap.has(m.cara)) {
                carasMap.set(m.cara, {
                    codigo: m.cara,
                    nombre: `Cara ${m.cara}`,
                    activo: true,
                    mangueras: []
                });
            }
            // Solo incluir mangueras que tienen combustible seleccionado
            if (m.tipoCombustible !== '') {
                carasMap.get(m.cara).mangueras.push({
                    id: m.id,
                    codigo: m.codigo,
                    nombre: m.nombre,
                    tipoCombustible: m.tipoCombustible,
                    combustibleId: m.combustibleId,
                    lecturaActual: 0,
                    activo: true
                });
            }
        }

        const dispensarioData = {
            numero: formData.numero,
            nombre: formData.nombre,
            ubicacion: formData.ubicacion,
            estado: formData.estado,
            tieneDosCaras: true,
            activo: true,
            caras: Array.from(carasMap.values())
        };

        console.log('📤 Datos a enviar al backend:', JSON.stringify(dispensarioData, null, 2));
        return dispensarioData;
    };

    const handleGuardar = async () => {
        if (!formData.numero || !formData.nombre) {
            alert('Complete los campos obligatorios (Número y Nombre)');
            return;
        }

        // Validar que al menos haya una manguera con combustible
        const tieneMangueraActiva = mangueras.some(m => m.tipoCombustible !== '');
        if (!tieneMangueraActiva) {
            alert('Debe configurar al menos una manguera con un tipo de combustible');
            return;
        }

        setLoading(true);
        try {
            const dispensarioData = construirDispensarioParaEnvio();

            if (id) {
                await dispensariosService.actualizarCompleto(id, dispensarioData);
                alert('Dispensario actualizado exitosamente');
            } else {
                const respuesta = await dispensariosService.crearCompleto(dispensarioData);
                console.log('✅ Respuesta del servidor:', respuesta);
                alert('Dispensario creado exitosamente');
            }
            navigate('/dispensarios');
        } catch (error) {
            console.error('Error al guardar:', error);
            alert('Error al guardar: ' + (error.response?.data?.message || error.message));
        }
        setLoading(false);
    };

    if (cargandoDatos) {
        return (
            <div className="card">
                <div className="text-center py-5">
                    <div className="spinner-border text-primary" />
                    <p className="mt-2">Cargando dispensario...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="card">
            <h2>{id ? 'Editar Dispensario' : 'Nuevo Dispensario'}</h2>
            <p className="text-muted">Configure el tipo de combustible para cada una de las 4 mangueras</p>

            {/* Datos básicos */}
            <div className="row mb-4">
                <div className="col-md-4 mb-3">
                    <label className="form-label">Número *</label>
                    <input type="text" className="form-control" name="numero" value={formData.numero} onChange={handleChange} placeholder="01" />
                </div>
                <div className="col-md-4 mb-3">
                    <label className="form-label">Nombre *</label>
                    <input type="text" className="form-control" name="nombre" value={formData.nombre} onChange={handleChange} placeholder="Surtidor 1" />
                </div>
                <div className="col-md-4 mb-3">
                    <label className="form-label">Ubicación</label>
                    <input type="text" className="form-control" name="ubicacion" value={formData.ubicacion} onChange={handleChange} placeholder="Isla 1" />
                </div>
            </div>

            {/* CARA A */}
            <div className="card mb-4" style={{ backgroundColor: '#e8f4f8' }}>
                <div className="card-header">
                    <h4 className="mb-0">🔵 Cara A</h4>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="mb-3">🔌 Manguera A1 (Izquierda)</h5>
                                    <label className="form-label fw-bold">Tipo de Combustible</label>
                                    <select
                                        className="form-select"
                                        value={mangueras[0].tipoCombustible}
                                        onChange={(e) => handleMangueraCombustibleChange(0, e.target.value)}
                                    >
                                        {opcionesCombustible.map(opt => (
                                            <option key={opt.value} value={opt.value}>
                                                {opt.label}
                                            </option>
                                        ))}
                                    </select>
                                    {mangueras[0].tipoCombustible === '' && (
                                        <small className="text-warning d-block mt-2">⚠️ Seleccione un combustible para esta manguera</small>
                                    )}
                                </div>
                            </div>
                        </div>
                        <div className="col-md-6 mb-3">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="mb-3">🔌 Manguera A2 (Derecha)</h5>
                                    <label className="form-label fw-bold">Tipo de Combustible</label>
                                    <select
                                        className="form-select"
                                        value={mangueras[1].tipoCombustible}
                                        onChange={(e) => handleMangueraCombustibleChange(1, e.target.value)}
                                    >
                                        {opcionesCombustible.map(opt => (
                                            <option key={opt.value} value={opt.value}>
                                                {opt.label}
                                            </option>
                                        ))}
                                    </select>
                                    {mangueras[1].tipoCombustible === '' && (
                                        <small className="text-warning d-block mt-2">⚠️ Seleccione un combustible para esta manguera</small>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* CARA B */}
            <div className="card mb-4" style={{ backgroundColor: '#f0e6f5' }}>
                <div className="card-header">
                    <h4 className="mb-0">🟣 Cara B</h4>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col-md-6 mb-3">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="mb-3">🔌 Manguera B1 (Izquierda)</h5>
                                    <label className="form-label fw-bold">Tipo de Combustible</label>
                                    <select
                                        className="form-select"
                                        value={mangueras[2].tipoCombustible}
                                        onChange={(e) => handleMangueraCombustibleChange(2, e.target.value)}
                                    >
                                        {opcionesCombustible.map(opt => (
                                            <option key={opt.value} value={opt.value}>
                                                {opt.label}
                                            </option>
                                        ))}
                                    </select>
                                    {mangueras[2].tipoCombustible === '' && (
                                        <small className="text-warning d-block mt-2">⚠️ Seleccione un combustible para esta manguera</small>
                                    )}
                                </div>
                            </div>
                        </div>
                        <div className="col-md-6 mb-3">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="mb-3">🔌 Manguera B2 (Derecha)</h5>
                                    <label className="form-label fw-bold">Tipo de Combustible</label>
                                    <select
                                        className="form-select"
                                        value={mangueras[3].tipoCombustible}
                                        onChange={(e) => handleMangueraCombustibleChange(3, e.target.value)}
                                    >
                                        {opcionesCombustible.map(opt => (
                                            <option key={opt.value} value={opt.value}>
                                                {opt.label}
                                            </option>
                                        ))}
                                    </select>
                                    {mangueras[3].tipoCombustible === '' && (
                                        <small className="text-warning d-block mt-2">⚠️ Seleccione un combustible para esta manguera</small>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Resumen */}
            <div className="alert alert-info mt-3">
                <strong>📋 Resumen de configuración:</strong>
                <ul className="mb-0 mt-2">
                    <li><strong>Dispensario:</strong> {formData.nombre || '(sin nombre)'}</li>
                    <li><strong>Mangueras configuradas:</strong> {mangueras.filter(m => m.tipoCombustible !== '').length} de 4</li>
                    <li><strong>Combustibles:</strong> {[...new Set(mangueras.filter(m => m.tipoCombustible !== '').map(m => m.tipoCombustible))].join(', ') || 'Ninguno'}</li>
                </ul>
            </div>

            <div className="mt-3 d-flex gap-2">
                <button className="btn btn-primary" onClick={handleGuardar} disabled={loading}>
                    {loading ? 'Guardando...' : (id ? 'Actualizar Dispensario' : 'Crear Dispensario')}
                </button>
                <button className="btn btn-secondary" onClick={() => navigate('/dispensarios')}>
                    Cancelar
                </button>
            </div>
        </div>
    );
};

export default DispensarioForm;