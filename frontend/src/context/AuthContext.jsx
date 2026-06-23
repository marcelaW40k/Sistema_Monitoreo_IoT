/* eslint-disable react-refresh/only-export-components */
import { createContext, useState, useContext } from 'react';

// Creación del contexto de seguridad
const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem('token');
        const email = localStorage.getItem('email');
        const role = localStorage.getItem('role');
        const name = localStorage.getItem('name');
        if (token && email && role && name) {
            return { token, email, role, name };
        }
        return null;
    });

    // Función para actualizar el estado global cuando el backend responde con éxito
    const loginGlobal = (authData) => {
        localStorage.setItem('token', authData.token);
        localStorage.setItem('email', authData.email);
        localStorage.setItem('role', authData.role);
        localStorage.setItem('name', authData.name);
        
        setUser({
            token: authData.token,
            email: authData.email,
            role: authData.role,
            name: authData.name
        });
    };

    // Función para limpiar credenciales al salir
    const logoutGlobal = () => {
        localStorage.clear();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loginGlobal, logoutGlobal }}>
            {children}
        </AuthContext.Provider>
    );
};

// Hook personalizado para consumir la sesión de forma rápida y limpia en los componentes
export const useAuth = () => useContext(AuthContext);