import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { inventarioCombustibleService, combustibleService } from '../../api/inventarios/auth';

const CargaPipaForm = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [combustibles, setCombustibles] = useState([]);
    const [formData, setFormData] = useState({
        tipoCombustible: '',
        proveedor: '',
        volumen: '',
        precioCompra: '',
        factura: '',
        observaciones: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    useEffect(() => {
        // Los tipos de combustible salen del catalogo, no hardcodeados.
        combustibleService.listarActivos()
            .then(data => {
                setCombustibles(Array.isArray(data) ? data : []);
            })
            .catch(err => {
                console.error('Error cargando combustibles:', err);
                setCombustibles([]);
            });
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const cargaData = {
                tipoCombustible: formData.tipoCombustible,
                proveedor: formData.proveedor,
                volumen: parseFloat(formData.volumen),
                precioCompra: formData.precioCompra ? parseFloat(formData.precioCompra) : null,
                factura: formData.factura,
                observaciones: formData.observaciones
            };
            await inventarioCombustibleService.registrarCarga(cargaData);
            alert('Carga de pipa registrada exitosamente');
            navigate('/inventario-combustible');
        } catch (error) {
            alert(error.response?.data?.message || 'Error al registrar carga');
        }
        setLoading(false);
    };

    return (
        <div className="container">
            <div className="card" style={{ maxWidth: '600px', margin: '0 auto' }}>
                <h2>Registrar Carga de Pipa</h2>

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Tipo de Combustible *</label>
                        <select
                            name="tipoCombustible"
                            value={formData.tipoCombustible}
                            onChange={handleChange}
                            required
                        >
                            <option value="">❌ Seleccione un combustible</option>
                            {combustibles.map(c => (
                                <option key={c.id} value={c.tipo}>⛽ {c.nombre} ({c.tipo})</option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>Proveedor</label>
                        <input
                            type="text"
                            name="proveedor"
                            value={formData.proveedor}
                            onChange={handleChange}
                            placeholder="Ej: PEMEX, Repsol"
                        />
                    </div>

                    <div className="form-group">
                        <label>Volumen (litros) *</label>
                        <input
                            type="number"
                            step="0.001"
                            min="0.001"
                            name="volumen"
                            value={formData.volumen}
                            onChange={handleChange}
                            required
                            placeholder="0.00"
                        />
                    </div>

                    <div className="form-group">
                        <label>Precio por litro</label>
                        <input
                            type="number"
                            step="0.01"
                            min="0"
                            name="precioCompra"
                            value={formData.precioCompra}
                            onChange={handleChange}
                            placeholder="0.00"
                        />
                    </div>

                    <div className="form-group">
                        <label>Factura</label>
                        <input
                            type="text"
                            name="factura"
                            value={formData.factura}
                            onChange={handleChange}
                            placeholder="Número de factura"
                        />
                    </div>

                    <div className="form-group">
                        <label>Observaciones</label>
                        <textarea
                            name="observaciones"
                            value={formData.observaciones}
                            onChange={handleChange}
                            rows="3"
                            placeholder="Observaciones (opcional)"
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                        />
                    </div>

                    <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Registrando...' : 'Registrar Carga'}
                        </button>
                        <button type="button" className="btn" onClick={() => navigate('/inventario-combustible')}>
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CargaPipaForm;