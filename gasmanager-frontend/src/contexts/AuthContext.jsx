import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../api/users/auth';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // useEffect(() => {
    //     const currentUser = authService.getCurrentUser();
    //     if (currentUser) {
    //         setUser(currentUser);
    //     }
    //     setLoading(false);
    // }, []);
    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
            authService.validarToken()
                .then(esValido => {
                    if (esValido) {
                        setUser(currentUser);
                    } else {
                        authService.logout();
                    }
                })
                .catch(() => {
                    authService.logout();
                })
                .finally(() => setLoading(false));
        } else {
            setLoading(false);
        }
    }, []);

    const login = async (correo, password) => {
        setError(null);
        try {
            const data = await authService.login(correo, password);
            setUser(data);
            return { success: true, data };
        } catch (err) {
            const errorMsg = err.response?.data?.error ||
                err.response?.data?.mensaje ||
                'Error al iniciar sesión';
            setError(errorMsg);
            return { success: false, error: errorMsg };
        }
    };

    const logout = () => {
        authService.logout();
        setUser(null);
    };

    // =============================================
    // MODIFICADO: Ahora verifica múltiples roles de admin
    // =============================================
    const isAdmin = user?.rol === 'ADMIN' ||
        user?.rol === 'ADMINISTRADOR' ||
        user?.rol === 'ROLE_ADMIN' ||
        user?.rol === 'ADMIN_ROLE';

    // También agregamos un helper para permisos
    const hasPermission = (permiso) => {
        // Si es admin, tiene todos los permisos
        if (isAdmin) return true;
        // Aquí se pueden agregar verificaciones de permisos específicos
        return true; // Por ahora, todo true
    };

    const value = {
        user,
        loading,
        error,
        login,
        logout,
        isAuthenticated: !!user,
        isAdmin,
        hasPermission,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};