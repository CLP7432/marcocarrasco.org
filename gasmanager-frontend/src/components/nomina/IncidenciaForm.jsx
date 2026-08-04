import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { incidenciasService, empleadosService } from '../../api/nomina/auth';

const IncidenciaForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [empleados, setEmpleados] = useState([]);
    const [formData, setFormData] = useState({
        empleadoId: '',
        tipo: 'FALTA',
        fecha: new Date().toISOString().split('T')[0],
        cantidad: '',
        monto: '',
        observaciones: '',
        autorizadoPor: ''
    });

    useEffect(() => {
        cargarEmpleados();
        if (id) {
            cargarIncidencia();
        }
    }, [id]);

    const cargarEmpleados = async () => {
        try {
            const data = await empleadosService.listarActivos();
            setEmpleados(data);
        } catch (error) {
            console.error('Error al cargar empleados:', error);
        }
    };

    const cargarIncidencia = async () => {
        try {
            const data = await incidenciasService.obtenerPorId(id);
            setFormData({
                empleadoId: data.empleadoId,
                tipo: data.tipo,
                fecha: data.fecha,
                cantidad: data.cantidad || '',
                monto: data.monto || '',
                observaciones: data.observaciones || '',
                autorizadoPor: data.autorizadoPor || ''
            });
        } catch (error) {
            console.error('Error al cargar incidencia:', error);
            alert('Error al cargar incidencia');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const incidenciaData = {
                empleadoId: parseInt(formData.empleadoId),
                tipo: formData.tipo,
                fecha: formData.fecha,
                cantidad: formData.cantidad ? parseFloat(formData.cantidad) : null,
                monto: formData.monto ? parseFloat(formData.monto) : null,
                observaciones: formData.observaciones || null,
                autorizadoPor: formData.autorizadoPor || null
            };

            if (id) {
                await incidenciasService.actualizar(id, incidenciaData);
                alert('Incidencia actualizada exitosamente');
            } else {
                await incidenciasService.crear(incidenciaData);
                alert('Incidencia registrada exitosamente');
            }
            navigate('/incidencias');
        } catch (error) {
            const errorMsg = error.response?.data?.message || error.response?.data?.errors || 'Error al guardar incidencia';
            if (typeof errorMsg === 'object') {
                const firstError = Object.values(errorMsg)[0];
                alert(firstError);
            } else {
                alert(errorMsg);
            }
        }
        setLoading(false);
    };

    const tipoRequiereCantidad = ['FALTA', 'RETARDO', 'HORA_EXTRA_DOBLE', 'HORA_EXTRA_TRIPLE', 'VACACION'].includes(formData.tipo);
    const tipoRequiereMonto = ['BONO', 'FALTANTE'].includes(formData.tipo);

    return (
        <div className="container">
            <div className="card" style={{ maxWidth: '700px', margin: '0 auto' }}>
                <h2>{id ? 'Editar Incidencia' : 'Nueva Incidencia'}</h2>

                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Empleado *</label>
                        <select
                            className="form-select"
                            name="empleadoId"
                            value={formData.empleadoId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Seleccione un empleado</option>
                            {empleados.map(emp => (
                                <option key={emp.id} value={emp.id}>
                                    {emp.nombre} {emp.apellidoPaterno} - {emp.puestoNombre || 'Sin puesto'}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Tipo de Incidencia *</label>
                        <select
                            className="form-select"
                            name="tipo"
                            value={formData.tipo}
                            onChange={handleChange}
                            required
                        >
                            <option value="FALTA">Falta</option>
                            <option value="RETARDO">Retardo</option>
                            <option value="HORA_EXTRA_DOBLE">Hora Extra (Doble)</option>
                            <option value="HORA_EXTRA_TRIPLE">Hora Extra (Triple)</option>
                            <option value="BONO">Bono</option>
                            <option value="FALTANTE">Faltante (descuento)</option>
                            <option value="PERMISO_CON_GOCE">Permiso con goce de sueldo</option>
                            <option value="PERMISO_SIN_GOCE">Permiso sin goce de sueldo</option>
                            <option value="VACACION">Vacación</option>
                        </select>
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Fecha *</label>
                        <input
                            type="date"
                            className="form-control"
                            name="fecha"
                            value={formData.fecha}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    {tipoRequiereCantidad && (
                        <div className="mb-3">
                            <label className="form-label">
                                {formData.tipo === 'FALTA' ? 'Días de falta' :
                                    formData.tipo === 'RETARDO' ? 'Horas de retardo' :
                                        formData.tipo.includes('HORA_EXTRA') ? 'Horas extras' : 'Días'}
                            </label>
                            <input
                                type="number"
                                step="0.5"
                                min="0"
                                className="form-control"
                                name="cantidad"
                                value={formData.cantidad}
                                onChange={handleChange}
                                placeholder="0.00"
                            />
                            <small className="text-muted">
                                {formData.tipo === 'FALTA' && 'Se descontará el salario correspondiente'}
                                {formData.tipo.includes('HORA_EXTRA') && 'Las horas extras se pagan al doble o triple'}
                            </small>
                        </div>
                    )}

                    {tipoRequiereMonto && (
                        <div className="mb-3">
                            <label className="form-label">Monto del Bono *</label>
                            <div className="input-group">
                                <span className="input-group-text">$</span>
                                <input
                                    type="number"
                                    step="0.01"
                                    min="0.01"
                                    className="form-control"
                                    name="monto"
                                    value={formData.monto}
                                    onChange={handleChange}
                                    required={formData.tipo === 'BONO'}
                                    placeholder="0.00"
                                />
                            </div>
                        </div>
                    )}

                    <div className="mb-3">
                        <label className="form-label">Autorizado por</label>
                        <input
                            type="text"
                            className="form-control"
                            name="autorizadoPor"
                            value={formData.autorizadoPor}
                            onChange={handleChange}
                            placeholder="Nombre del supervisor que autoriza"
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Observaciones</label>
                        <textarea
                            className="form-control"
                            name="observaciones"
                            value={formData.observaciones}
                            onChange={handleChange}
                            rows="2"
                            placeholder="Información adicional"
                        />
                    </div>

                    <div className="alert alert-info">
                        <small>
                            <strong>Nota:</strong> Las faltas, retardos y horas extras afectarán el cálculo de la nómina.
                            Los bonos incrementarán el total a pagar.
                        </small>
                    </div>

                    <div className="d-flex gap-2 mt-3">
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Guardando...' : 'Guardar'}
                        </button>
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/incidencias')}>
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default IncidenciaForm;