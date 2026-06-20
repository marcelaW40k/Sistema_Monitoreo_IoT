import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

const VehicleMap = ({ vehicles }) => {
    // Coordenadas por defecto (Centro en Bogotá)
    const defaultCenter = [4.6097, -74.0817]; 

    return (
        <div className="map-wrapper" style={{ height: '400px', width: '100%', marginBottom: '20px', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
            <MapContainer 
                center={defaultCenter} 
                zoom={6} 
                style={{ height: '100%', width: '100%' }}
                scrollWheelZoom={true}
            >
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />

                {/* Mapeo iterativo de vehículos */}
                {vehicles && vehicles.map(vehicle => {
                    // Validamos y convertimos a número por seguridad
                    const lat = Number(vehicle.latitude);
                    const lng = Number(vehicle.longitude);

                    if (!isNaN(lat) && !isNaN(lng)) {
                        return (
                            // Usamos vehicle_id que viene de la query SQL anterior
                            <Marker key={vehicle.vehicle_id} position={[lat, lng]}>
                                <Popup>
                                    <div style={{ fontSize: '13px', minWidth: '160px' }}>
                                        <h4 style={{ margin: '0 0 5px 0', color: '#0071e3' }}>
                                            Placa: {vehicle.plate}
                                        </h4>
                                        <p style={{ margin: '3px 0' }}><strong>Modelo:</strong> {vehicle.model}</p>
                                        {/* Ajustado a snake_case para coincidir con la DB */}
                                        <p style={{ margin: '3px 0' }}><strong>Dispositivo:</strong> {vehicle.device_id}</p>
                                        {vehicle.speed !== undefined && (
                                            <p style={{ margin: '3px 0' }}><strong>Velocidad:</strong> {vehicle.speed} km/h</p>
                                        )}
                                        {vehicle.fuel_level !== undefined && (
                                            <p style={{ margin: '3px 0' }}><strong>Combustible:</strong> {vehicle.fuel_level}%</p>
                                        )}
                                    </div>
                                </Popup>
                            </Marker>
                        );
                    }
                    return null;
                })}
            </MapContainer>
        </div>
    );
};

export default VehicleMap;