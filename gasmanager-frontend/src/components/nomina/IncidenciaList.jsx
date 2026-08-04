import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { incidenciasService, empleadosService } from '../../api/nomina/auth';
import { useAuth } from '../../contexts/AuthContext';

const IncidenciaList = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAdmin } = useAuth();
    const [incidencias, setIncidencias] = useState([]);
    const [empleados, setEmpleados] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filtroEmpleado, setFiltroEmpleado] = useState('');
    const [filtroTipo, setFiltroTipo] = useState('');

    // Obtener empleadoId de la URL si viene
    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const empleadoId = params.get('empleadoId');
        if (empleadoId) {
            setFiltroEmpleado(empleadoId);
        }
    }, [location]);

    useEffect(() => {
        cargarEmpleados();
        cargarIncidencias();
    }, [filtroEmpleado, filtroTipo]);

    const cargarEmpleados = async () => {
        try {
            const data = await empleadosService.listarActivos();
            setEmpleados(data);
        } catch (error) {
            console.error('Error al cargar empleados:', error);
        }
    };

    const cargarIncidencias = async () => {
        setLoading(true);
        try {
            let data;
            if (filtroEmpleado) {
                data = await incidenciasService.listarPorEmpleado(filtroEmpleado);
            } else {
                data = await incidenciasService.listar();
            }

            if (filtroTipo) {
                data = data.filter(inc => inc.tipo === filtroTipo);
            }

            setIncidencias(data);
        } catch (error) {
            console.error('Error al cargar incidencias:', error);
        }
        setLoading(false);
    };

    const handleEliminar = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar esta incidencia?')) {
            try {
                await incidenciasService.eliminar(id);
                cargarIncidencias();
            } catch (error) {
                alert(error.response?.data?.message || 'Error al eliminar incidencia');
            }
        }
    };

    const getTipoBadge = (tipo) => {
        const colores = {
            FALTA: 'bg-danger',
            RETARDO: 'bg-warning text-dark',
            HORA_EXTRA_DOBLE: 'bg-info',
            HORA_EXTRA_TRIPLE: 'bg-primary',
            BONO: 'bg-success',
            FALTANTE: 'bg-danger',
            PERMISO_CON_GOCE: 'bg-secondary',
            PERMISO_SIN_GOCE: 'bg-secondary',
            VACACION: 'bg-secondary'
        };
        const labels = {
            FALTA: 'Falta',
            RETARDO: 'Retardo',
            HORA_EXTRA_DOBLE: 'Hora Extra (Doble)',
            HORA_EXTRA_TRIPLE: 'Hora Extra (Triple)',
            BONO: 'Bono',
            FALTANTE: 'Faltante',
            PERMISO_CON_GOCE: 'Permiso con goce',
            PERMISO_SIN_GOCE: 'Permiso sin goce',
            VACACION: 'Vacación'
        };
        return <span className={`badge ${colores[tipo] || 'bg-secondary'}`}>{labels[tipo] || tipo}</span>;
    };

    const formatMonto = (monto) => {
        if (!monto) return '-';
        return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(monto);
    };

    if (loading) {
        return <div className="card">Cargando incidencias...</div>;
    }

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2>Gestión de Incidencias</h2>
                {isAdmin && (
                    <button className="btn btn-primary" onClick={() => navigate('/incidencias/nuevo')}>
                        + Nueva Incidencia
                    </button>
                )}
            </div>

            <div className="card mb-3">
                <div className="card-body">
                    <div className="row g-2">
                        <div className="col-md-4">
                            <label className="form-label small">Filtrar por Empleado</label>
                            <select
                                className="form-select"
                                value={filtroEmpleado}
                                onChange={(e) => setFiltroEmpleado(e.target.value)}
                            >
                                <option value="">Todos los empleados</option>
                                {empleados.map(emp => (
                                    <option key={emp.id} value={emp.id}>
                                        {emp.nombre} {emp.apellidoPaterno}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="col-md-4">
                            <label className="form-label small">Filtrar por Tipo</label>
                            <select
                                className="form-select"
                                value={filtroTipo}
                                onChange={(e) => setFiltroTipo(e.target.value)}
                            >
                                <option value="">Todos los tipos</option>
                                <option value="FALTA">Falta</option>
                                <option value="RETARDO">Retardo</option>
                                <option value="HORA_EXTRA_DOBLE">Hora Extra (Doble)</option>
                                <option value="HORA_EXTRA_TRIPLE">Hora Extra (Triple)</option>
                                <option value="BONO">Bono</option>
                                <option value="FALTANTE">Faltante</option>
                                <option value="PERMISO_CON_GOCE">Permiso con goce</option>
                                <option value="PERMISO_SIN_GOCE">Permiso sin goce</option>
                                <option value="VACACION">Vacación</option>
                            </select>
                        </div>
                        <div className="col-md-4 d-flex align-items-end">
                            <button className="btn btn-secondary w-100" onClick={cargarIncidencias}>
                                Actualizar
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="table-container">
                    <table className="table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Empleado</th>
                            <th>Tipo</th>
                            <th>Fecha</th>
                            <th>Cantidad</th>
                            <th>Monto</th>
                            <th>Autorizado por</th>
                            <th>Observaciones</th>
                            {isAdmin && <th>Acciones</th>}
                        </tr>
                        </thead>
                        <tbody>
                        {incidencias.map(incidencia => (
                            <tr key={incidencia.id}>
                                <td>{incidencia.id}</td>
                                <td><strong>{incidencia.empleadoNombre}</strong></td>
                                <td>{getTipoBadge(incidencia.tipo)}</td>
                                <td>{new Date(incidencia.fecha).toLocaleDateString()}</td>
                                <td>{incidencia.cantidad || '-'}</td>
                                <td>{formatMonto(incidencia.monto)}</td>
                                <td>{incidencia.autorizadoPor || '-'}</td>
                                <td style={{ maxWidth: '200px' }}>{incidencia.observaciones || '-'}</td>
                                {isAdmin && (
                                    <td>
                                        <button
                                            className="btn btn-primary btn-sm me-1"
                                            onClick={() => navigate(`/incidencias/editar/${incidencia.id}`)}
                                        >
                                            Editar
                                        </button>
                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={() => handleEliminar(incidencia.id)}
                                        >
                                            Eliminar
                                        </button>
                                    </td>
                                )}
                            </tr>
                        ))}
                        {incidencias.length === 0 && (
                            <tr>
                                <td colSpan={isAdmin ? 9 : 8} className="text-center">
                                    No hay incidencias registradas
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
);
};

export default IncidenciaList;