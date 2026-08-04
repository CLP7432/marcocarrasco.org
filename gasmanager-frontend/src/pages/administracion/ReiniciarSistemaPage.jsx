import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../components/common/Layout.jsx';
import { useAuth } from '../../contexts/AuthContext.jsx';

const ReiniciarSistemaPage = () => {
    const navigate = useNavigate();
    const { isAdmin } = useAuth();
    const [loading, setLoading] = useState(false);
    const [confirmacion, setConfirmacion] = useState('');

    const handleReiniciar = async () => {
        if (confirmacion !== 'REINICIAR') {
            alert('Escriba "REINICIAR" para confirmar');
            return;
        }

        if (!window.confirm('⚠️ ¿ESTÁ SEGURO? Esta acción eliminará TODOS los datos: ventas, turnos, cortes, dispensarios, mangueras, combustibles y tanques. La gasolinera quedará en cero. No se puede deshacer.')) {
            return;
        }

        setLoading(true);
        try {
            const response = await fetch('/api/admin/reiniciar-sistema', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });
            const data = await response.json();
            if (response.ok) {
                alert('✅ ' + data.mensaje);
                navigate('/modulo-administracion');
            } else {
                alert('❌ Error: ' + data.error);
            }
        } catch (error) {
            alert('❌ Error al reiniciar el sistema');
        }
        setLoading(false);
    };

    if (!isAdmin) {
        return <Layout><div className="alert alert-danger">No tienes permisos para acceder</div></Layout>;
    }

    return (
        <Layout>
            <div className="card">
                <h2 className="text-danger">🔄 Reiniciar Sistema</h2>
                <div className="alert alert-warning">
                    <strong>⚠️ ADVERTENCIA:</strong>
                    <ul className="mt-2 mb-0">
                        <li>Se eliminarán TODAS las <strong>ventas</strong></li>
                        <li>Se eliminarán TODOS los <strong>turnos</strong></li>
                        <li>Se eliminarán TODOS los <strong>cortes</strong></li>
                        <li>Se eliminarán TODAS las <strong>lecturas iniciales y finales</strong></li>
                        <li>Se eliminarán TODOS los <strong>dispensarios y mangueras</strong></li>
                        <li>Se eliminarán TODOS los <strong>combustibles y tanques</strong></li>
                        <li className="text-danger">🚫 La gasolinera queda en CERO. Deberá reconfigurarse desde el inicio.</li>
                    </ul>
                </div>

                <div className="mb-3">
                    <label className="form-label">
                        Escriba <strong className="text-danger">REINICIAR</strong> para confirmar:
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        value={confirmacion}
                        onChange={(e) => setConfirmacion(e.target.value)}
                        placeholder="REINICIAR"
                    />
                </div>

                <div className="d-flex gap-2">
                    <button
                        className="btn btn-danger"
                        onClick={handleReiniciar}
                        disabled={loading || confirmacion !== 'REINICIAR'}
                    >
                        {loading ? 'Reiniciando...' : '🔄 Reiniciar Sistema'}
                    </button>
                    <button
                        className="btn btn-secondary"
                        onClick={() => navigate('/modulo-administracion')}
                    >
                        Cancelar
                    </button>
                </div>
            </div>
        </Layout>
    );
};

export default ReiniciarSistemaPage;