import React, {useState, useEffect} from 'react';
import {useParams} from 'react-router-dom';
import {inventarioCombustibleService, combustibleService} from '../../api/inventarios/auth';

 const CargaPipaList = () => {
    const {tipo} = useParams();
    const [cargas, setCargas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [combustibles, setCombustibles] = useState([]);
    const [tipoSeleccionado, setTipoSeleccionado] = useState(tipo || 'todos');

    useEffect(() => {
        cargarCargas();
    }, [tipoSeleccionado]);

    useEffect(() => {
        combustibleService.listarActivos()
            .then(data => setCombustibles(Array.isArray(data) ? data : []))
            .catch(err => {
                console.error('Error cargando combustibles:', err);
                setCombustibles([]);
            });
    }, []);

    const cargarCargas = async () => {
        setLoading(true);
        try {
            let data;
            if (tipoSeleccionado !== 'todos') {
                data = await inventarioCombustibleService.listarCargasPorTipo(tipoSeleccionado);
            } else {
                data = await inventarioCombustibleService.listarCargas();
            }
            setCargas(data);
        } catch (error) {
            console.error('Error al cargar cargas:', error);
        }
        setLoading(false);
    };

    if (loading) {
        return <div className="card">Cargando historial de cargas...</div>;
    }

    return (
        <>
            <h2>Historial de Cargas de Pipa</h2>

            <div className="card">
                <div style={{marginBottom: '15px', display: 'flex', gap: '10px', alignItems: 'center'}}>
                    <label>Filtrar por tipo:</label>
                    <select
                        value={tipoSeleccionado}
                        onChange={(e) => setTipoSeleccionado(e.target.value)}
                        style={{padding: '5px'}}
                    >
                        <option value="todos">Todos</option>
                        {combustibles.map(c => (
                            <option key={c.id} value={c.tipo}>{c.nombre}</option>
                        ))}
                    </select>
                    <button onClick={cargarCargas} className="btn">Actualizar</button>
                </div>

                <div className="table-container">
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Folio</th>
                            <th>Fecha</th>
                            <th>Tipo</th>
                            <th>Proveedor</th>
                            <th>Volumen (L)</th>
                            <th>Precio/L</th>
                            <th>Costo Total</th>
                            <th>Factura</th>
                            <th>Cargado por</th>
                        </tr>
                        </thead>
                        <tbody>
                        {cargas.map(carga => (
                            <tr key={carga.id}>
                                <td><strong>{carga.folio}</strong></td>
                                <td>{new Date(carga.fechaCarga).toLocaleString()}</td>
                                <td>
                                    <span className="badge badge-primary">{carga.tipoCombustible}</span>
                                </td>
                                <td>{carga.proveedor || '-'}</td>
                                <td>{carga.volumen?.toLocaleString()} L</td>
                                <td>{carga.precioCompra ? `$${carga.precioCompra}` : '-'}</td>
                                <td>{carga.costoTotal ? `$${carga.costoTotal.toLocaleString()}` : '-'}</td>
                                <td>{carga.factura || '-'}</td>
                                <td>{carga.cargadoPor || '-'}</td>
                            </tr>
                        ))}
                        {cargas.length === 0 && (
                            <tr>
                                <td colSpan="9" style={{textAlign: 'center'}}>
                                    No hay cargas de pipa registradas
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    )
}
export default CargaPipaList;
