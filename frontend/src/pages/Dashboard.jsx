import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import authApi from '../api/authApi';
import VehicleMap from '../components/VehicleMap'; 
import './Dashboard.css'; 

const Dashboard = () => {
    const { user, logoutGlobal } = useAuth();
    const [vehicles, setVehicles] = useState([]);
    const [alerts, setAlerts] = useState([]);
    const [loading, setLoading] = useState(true);

   useEffect(() => {
    const fetchDashboardData = async () => {
        try {
            const config = {
                headers: { Authorization: `Bearer ${user.token}` }
            };

            const vehicleRes = await authApi.get('/fleet/vehicles', config);
            setVehicles(vehicleRes.data);

            if (user.role === 'ADMIN') {
                const alertRes = await authApi.get('/fleet/admin/alerts', config);
                setAlerts(alertRes.data);
            }
        } catch (err) {
            console.error("Error cargando datos del dashboard", err);
        } finally {
            setLoading(false);
        }
    };

    if (user?.token) {
        fetchDashboardData();
    }

    let interval;
    if (user.role === 'ADMIN') {
        interval = setInterval(() => {
            const randomTrigger = Math.random() > 0.7;
            
            if (randomTrigger) {
                // Usamos 'prevVehicles' para acceder al estado actual sin poner 'vehicles' en las dependencias
                setVehicles(prevVehicles => {
                    if (prevVehicles.length > 0) {
                        const randomVehicle = prevVehicles[Math.floor(Math.random() * prevVehicles.length)];
                        
                        const newAlert = {
                            id: Date.now(),
                            vehicle: randomVehicle,
                            message: `ALERTA PREDICTIVA: Al vehículo con placa ${randomVehicle.plate} le quedan 38.40 minutos de autonomía.`,
                            timestamp: new Date().toLocaleTimeString()
                        };
                        
                      
                        setAlerts(prevAlerts => [newAlert, ...prevAlerts]);
                    }
                    return prevVehicles; 
                });
            }
        }, 5000); 
    }

    return () => clearInterval(interval);
    
}, [user]); 

    if (loading) {
        return <div className="dashboard-container"><h3>Cargando panel de control...</h3></div>;
    }

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <div>
                    <h2>Hola Marcela 👋</h2>
                    <small style={{ color: '#86868b' }}>Rol: {user.role} | Cuenta: {user.email}</small>
                </div>
                <button onClick={logoutGlobal} className="logout-btn">Cerrar Sesión</button>
            </header>

         
            <VehicleMap vehicles={vehicles} />

            <div className="dashboard-grid">
    
                <div className="panel-card">
                    <h3>Estado General de la Flota</h3>
                    <table className="fleet-table">
                        <thead>
                            <tr>
                                <th>ID Interno</th>
                                <th>Placa / Patente</th>
                                <th>Modelo Vehículo</th>
                                <th>ID Dispositivo IoT (Mapeado)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vehicles.map(v => (
                                <tr key={v.id}>
                                    <td><strong>#{v.id}</strong></td>
                                    <td>{v.plate}</td>
                                    <td>{v.model}</td>
                                    <td style={{ fontFamily: 'monospace', color: '#0071e3' }}>
                                        {v.deviceId}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
                <div className="panel-card">
                    <h3>Alertas Predictivas de Combustible</h3>
                    {user.role !== 'ADMIN' ? (
                        <p style={{ color: '#86868b', fontSize: '14px' }}>
                            ⚠️ Vista bloqueada. Tu rol actual ({user.role}) no posee privilegios administrativos para auditar autonomía crítica.
                        </p>
                    ) : alerts.length === 0 ? (
                        <p style={{ color: '#86868b', fontSize: '14px' }}>No hay alertas críticas en este momento. Operación segura.</p>
                    ) : (
                        alerts.map(alert => (
                            <div key={alert.id} className="alert-item-box">
                                <p className="alert-message">{alert.message}</p>
                                <p className="alert-time">Recibido en vivo • {alert.timestamp.toString()}</p>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default Dashboard;