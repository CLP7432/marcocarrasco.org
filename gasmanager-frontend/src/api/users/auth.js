import axios from "axios";

const API_URL = '/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Interceptor: agrega el token JWT a cada peticion
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// Servicio de Autenticacion
export const authService = {
    login: async (correo, password) => {
        const response = await api.post('/usuarios/login', { correo, password });

        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('user', JSON.stringify(response.data));
        }
        return response.data;
    },
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },
    getCurrentUser: () => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },
    validarToken: async () => {
        const response = await api.get('/usuarios/validar-token');
        return response.data;
    },
};

// Servicios de Usuarios
export const usuarioService = {
    listar: async () => {
        const response = await api.get('/usuarios');
        return response.data;
    },

    listarActivos: async () => {
        const response = await api.get('/usuarios/activos');
        return response.data;
    },
    listarBloqueados: async () => {
        const response = await api.get('/usuarios/bloqueados');
        return response.data;
    },
    obtenerPorId: async (id) => {
        const response = await api.get(`/usuarios/${id}`);
        return response.data;
    },
    crear: async (usuario) => {
        const response = await api.post('/usuarios', usuario);
        return response.data;
    },
    actualizar: async (id, usuario) => {
        const response = await api.put(`/usuarios/${id}`, usuario);
        return response.data;
    },
    desactivar: async (id) => {
        const response = await api.delete(`/usuarios/${id}`);
        return response.data;
    }
};

// Servicios de Roles
export const rolService = {
    listar: async () => {
        const response = await api.get('/roles');
        return response.data;
    },
    listarActivos: async () => {
        const response = await api.get('/roles/activos');
        return response.data;
    },
    obtenerPorId: async (id) => {
        const response = await api.get(`/roles/${id}`);
        return response.data;
    },
    crear: async (rol) => {
        const response = await api.post('/roles', rol);
        return response.data;
    },
    actualizar: async (id, rol) => {
        const response = await api.put(`/roles/${id}`, rol);
        return response.data;
    },
    eliminar: async (id) => {
        const response = await api.delete(`/roles/${id}`);
        return response.data;
    }
};

// Servicio de Permisos
export const permisoService = {
    listar: async () => {
        const response = await api.get('/permisos');
        return response.data;
    },
    obtenerPorId: async (id) => {
        const response = await api.get(`/permisos/${id}`);
        return response.data;
    },
    crear: async (permiso) => {
        const response = await api.post('/permisos', permiso);
        return response.data;
    },
    actualizar: async (id, permiso) => {
        const response = await api.put(`/permisos/${id}`, permiso);
        return response.data;
    },
    eliminar: async (id) => {
        const response = await api.delete(`/permisos/${id}`);
        return response.data;
    }
};
// Servicio de Auditoria
export const auditoriaService = {
    listarTodas: async () => {
        const response = await api.get('/auditorias');
        return response.data;
    },
    listarPorUsuario: async (idUsuario) => {
        const response = await api.get(`/auditorias/usuario/${idUsuario}`);
        return response.data;
    },
    listarPorRango: async (inicio, fin) => {
        const response = await api.get('/auditorias/rango', {
            params: { inicio, fin }
        });
        return response.data;
    }
};