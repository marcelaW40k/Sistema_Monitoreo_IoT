import  { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import authApi from '../api/authApi';
import { useAuth } from '../context/AuthContext';
import InputField from '../components/InputField';
import './Login.css'; 

const Login = () => {
    const { loginGlobal } = useAuth();
    const navigate = useNavigate();

    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSubmitting(true);

        try {
            const response = await authApi.post('/auth/login', credentials);
            loginGlobal(response.data);
            navigate('/dashboard');
        } catch (err) {
            const serverMessage = err.response?.data || 'Error de conexión con el servidor.';
            setError(typeof serverMessage === 'string' ? serverMessage : 'Credenciales inválidas.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="login-page-container">
            <div className="login-card">
                <h1 className="login-title">Simon Movilidad</h1>
                <p className="login-subtitle">Monitoreo de Flotas IoT</p>

                {error && <div className="login-error-box">{error}</div>}

                <form onSubmit={handleSubmit}>
                    <InputField
                        label="Correo electrónico"
                        type="email"
                        name="email"
                        value={credentials.email}
                        onChange={handleChange}
                        placeholder="marcela@simon.com"
                    />

                    <InputField
                        label="Contraseña"
                        type="password"
                        name="password"
                        value={credentials.password}
                        onChange={handleChange}
                        placeholder="••••••••"
                    />

                    <button type="submit" disabled={submitting} className="login-submit-btn">
                        {submitting ? 'Iniciando sesión...' : 'Iniciar Sesión'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;