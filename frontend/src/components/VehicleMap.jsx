import { useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

const AutoFitBounds = ({ vehicles }) => {
    const map = useMap();
    
    useEffect(() => {
        if (!vehicles || vehicles.length === 0) return;

        // Filtramos solo las coordenadas válidas
        const validPoints = vehicles
            .filter(s => !isNaN(Number(s.latitude)) && !isNaN(Number(s.longitude)))
            .map(s => [Number(s.latitude), Number(s.longitude)]);

        if (validPoints.length > 0) {
            // Creamos un área perimetral que contenga a todos los vehículos
            const bounds = L.latLngBounds(validPoints);
            
            // Hacemos que el mapa se acerque (Zoom) automáticamente a ese grupo con una animación suave
            map.fitBounds(bounds, { padding: [50, 50], maxZoom: 14 });
        }
    }, [vehicles, map]);

    return null;
};

const VehicleMap = ({ vehicles }) => {
    const defaultCenter = [4.6097, -74.0817]; 

    return (
        <div className="map-wrapper" style={{ height: '450px', width: '100%', marginBottom: '20px', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
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

                <AutoFitBounds vehicles={vehicles} />

                {vehicles && vehicles.map(sensorReport => {
                    const lat = Number(sensorReport.latitude);
                    const lng = Number(sensorReport.longitude);
                    const vehicleEntity = sensorReport.vehicle;

                    if (!isNaN(lat) && !isNaN(lng) && vehicleEntity) {
                        return (
                            <Marker key={vehicleEntity.id} position={[lat, lng]}>
                                <Popup>
                                    <div style={{ fontSize: '13px', minWidth: '160px' }}>
                                        <h4 style={{ margin: '0 0 5px 0', color: '#0071e3' }}>
                                            Placa: {vehicleEntity.plate}
                                        </h4>
                                        <p style={{ margin: '3px 0' }}><strong>Modelo:</strong> {vehicleEntity.model}</p>
                                        <p style={{ margin: '3px 0' }}><strong>Dispositivo:</strong> {vehicleEntity.deviceId}</p>
                                        
                                        {sensorReport.speed !== undefined && (
                                            <p style={{ margin: '3px 0' }}><strong>Velocidad:</strong> {sensorReport.speed} km/h</p>
                                        )}
                                        {sensorReport.fuelLevel !== undefined && (
                                            <p style={{ margin: '3px 0' }}><strong>Combustible:</strong> {sensorReport.fuelLevel}%</p>
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