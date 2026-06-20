import axios from 'axios';

// Creamos un cliente de Axios con la configuración base para el backend de Spring Boot
const authApi = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});

export default authApi;