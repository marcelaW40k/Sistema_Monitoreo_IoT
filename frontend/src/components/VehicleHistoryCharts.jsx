import {  Line, Bar } from 'react-chartjs-2'; // <-- Cambiamos Line por Bar
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    Filler,
   
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    BarElement, 
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    Filler
);

const VehicleHistoryCharts = ({ historyData, vehiclePlate }) => {
    if (!historyData || historyData.length === 0) {
        return (
            <div className="panel-card" style={{ textAlign: 'center', padding: '30px', color: '#86868b' }}>
                <p>Selecciona un vehículo de la flota para auditar su gráfica de rendimiento histórico.</p>
            </div>
        );
    }

    // Preparar las etiquetas de tiempo (X)
    const labels = historyData.map(report => 
        new Date(report.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    ).reverse(); 

    // Datos de Velocidad y Combustible
    const speedData = historyData.map(report => report.speed).reverse();
    const fuelData = historyData.map(report => report.fuelLevel).reverse();

    // Configuración común de estilos para las gráficas
    const commonOptions = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false }, 
            tooltip: {
                backgroundColor: '#1d1d1f',
                padding: 12,
                cornerRadius: 8,
                titleFont: { size: 13 },
                bodyFont: { size: 13 }
            }
        },
        scales: {
            x: { 
                grid: { display: false }, 
                ticks: { color: '#86868b' } 
            },
            y: { 
                grid: { color: 'rgba(0, 0, 0, 0.04)' }, 
                ticks: { color: '#86868b' } 
            }
        }
    };

    // Configuración específica para las barras de combustible (0% a 100%)
    const fuelOptions = {
        ...commonOptions,
        scales: {
            ...commonOptions.scales,
            y: {
                ...commonOptions.scales.y,
                min: 0,
                max: 100,
                ticks: {
                    color: '#86868b',
                    callback: (value) => `${value}%`
                }
            }
        }
    };

    // 1. Gráfica de Velocidad (Se queda en Línea con Gradiente, ideal para curvas de aceleración)
    const speedChartConfig = {
        labels,
        datasets: [{
            label: 'Velocidad (km/h)',
            data: speedData,
            borderColor: '#0071e3',
            borderWidth: 3,
            pointBackgroundColor: '#0071e3',
            pointHoverRadius: 6,
            fill: true,
            tension: 0.4,
            backgroundColor: (context) => {
                const ctx = context.chart.ctx;
                const gradient = ctx.createLinearGradient(0, 0, 0, 250);
                gradient.addColorStop(0, 'rgba(0, 113, 227, 0.35)');
                gradient.addColorStop(1, 'rgba(0, 113, 227, 0.00)');
                return gradient;
            }
        }]
    };

    const fuelChartConfig = {
        labels,
        datasets: [{
            label: 'Nivel Combustible (%)',
            data: fuelData,
            borderRadius: 6, 
            borderSkipped: false,
            backgroundColor: (context) => {
                const ctx = context.chart.ctx;
                const gradient = ctx.createLinearGradient(0, 0, 0, 250);
                gradient.addColorStop(0, '#34c759'); 
                gradient.addColorStop(1, 'rgba(52, 199, 89, 0.2)');
                return gradient;
            }
        }]
    };

    return (
        <div className="charts-section-container">
            <h3>Análisis Histórico en Vivo: Placa {vehiclePlate}</h3>
            
            <div className="charts-grid-layout">
          
                <div className="chart-wrapper">
                    <h4 style={{ color: '#0071e3', margin: '0 0 10px 0' }}>Comportamiento de Velocidad (km/h)</h4>
                    <Line options={commonOptions} data={speedChartConfig} />
                </div>

                <div className="chart-wrapper">
                    <h4 style={{ color: '#34c759', margin: '0 0 10px 0' }}>Evolución del Combustible (%)</h4>
                    <Bar options={fuelOptions} data={fuelChartConfig} /> 
                </div>

            </div>
        </div>
    );
};

export default VehicleHistoryCharts;