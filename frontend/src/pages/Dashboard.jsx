import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import authApi from "../api/authApi";
import VehicleMap from "../components/VehicleMap";
import VehicleHistoryCharts from "../components/VehicleHistoryCharts";
import "./Dashboard.css";

const Dashboard = () => {
  const { user, logoutGlobal } = useAuth();
  const [vehicles, setVehicles] = useState([]);
  const [telemetryData, setTelemetryData] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [historyData, setHistoryData] = useState([]);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const config = {
          headers: { Authorization: `Bearer ${user.token}` },
        };

        const vehicleRes = await authApi.get("/fleet/vehicles", config);
        setVehicles(vehicleRes.data);

        const telemetryRes = await authApi.get(
          "/fleet/telemetry/live-locations",
          config,
        );
        setTelemetryData(telemetryRes.data);

       if (user.role === "ADMIN") {
          const alertRes = await authApi.get("/fleet/admin/alerts", config);
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
    if (user?.role === "ADMIN" && user?.token) {
      interval = setInterval(async () => {
        try {
          const config = { headers: { Authorization: `Bearer ${user.token}` } };
          const alertRes = await authApi.get("/fleet/admin/alerts", config);
          
          // Actualiza el estado con las alertas de la BD en tiempo real
          setAlerts(alertRes.data); 
        } catch (err) {
          console.error("Error actualizando alertas en segundo plano", err);
        }
      }, 5000);
    }

    return () => {
      if (interval) clearInterval(interval);
    };
  }, [user]);

  const handleSelectVehicle = async (vehicle) => {
    try {
      setSelectedVehicle(vehicle);
      const config = { headers: { Authorization: `Bearer ${user.token}` } };

      const res = await authApi.get(
        `/fleet/vehicles/${vehicle.id}/history`,
        config,
      );

      setHistoryData(Array.isArray(res) ? res : res.data);
    } catch (err) {
      console.error("Error cargando historial de telemetría", err);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <h3>Cargando panel de control...</h3>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div>
          <h2>Hola: {user.name}</h2>
          <small style={{ color: "#86868b" }}>
            Rol: {user.role} | Correo: {user.email}
          </small>
        </div>
        <button onClick={logoutGlobal} className="logout-btn">
          Cerrar Sesión
        </button>
      </header>

      <VehicleMap
        vehicles={Array.isArray(telemetryData) ? telemetryData : []}
      />

      <div className="dashboard-grid">
        <div className="panel-card">
          <h3>Estado General de la Flota</h3>
          <table className="fleet-table">
            <thead>
              <tr>
                <th>ID Interno</th>
                <th>Placa / Patente</th>
                <th>Modelo Vehículo</th>
                <th>ID Dispositivo IoT</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.map((v) => (
                <tr
                  key={v.id}
                  style={{ cursor: "pointer" }}
                  onClick={() => handleSelectVehicle(v)}
                  className={selectedVehicle?.id === v.id ? "active-row" : ""}
                >
                  <td>
                    <strong>#{v.id}</strong>
                  </td>
                  <td>{v.plate}</td>
                  <td>{v.model}</td>
                  <td style={{ fontFamily: "monospace", color: "#0071e3" }}>
                    {v.deviceId}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="panel-card">
          <h3>Alertas Predictivas de Combustible</h3>
          {user.role !== "ADMIN" ? (
            <p style={{ color: "#86868b", fontSize: "14px" }}>
              ⚠️ Vista bloqueada. Tu rol actual ({user.role}) no posee
              privilegios administrativos para auditar autonomía crítica.
            </p>
          ) : alerts.length === 0 ? (
            <p style={{ color: "#86868b", fontSize: "14px" }}>
              No hay alertas críticas en este momento. Operación segura.
            </p>
          ) : (
            <div className="alerts-scroll-container">
              {alerts.map((alert, index) => (
                <div key={`${alert.id}-${index}`} className="alert-item-box">
                  <p className="alert-message">{alert.message}</p>
                  <p className="alert-time">
                    Recibido en vivo • {alert.timestamp.toString()}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <VehicleHistoryCharts
        historyData={historyData}
        vehiclePlate={selectedVehicle?.plate || ""}
      />
    </div>
  );
};

export default Dashboard;
