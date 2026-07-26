package com.gasmanager.users.config;

import com.gasmanager.users.entities.core.EstadoUsuario;
import com.gasmanager.users.entities.core.Usuario;
import com.gasmanager.users.entities.security.Permiso;
import com.gasmanager.users.entities.security.Rol;
import com.gasmanager.users.repositories.PermisoRepository;
import com.gasmanager.users.repositories.RolRepository;
import com.gasmanager.users.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.initial-data.enabled:true}")
    private boolean initialDataEnabled;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (!initialDataEnabled) {
            System.out.println("=== INICIALIZACION DE DATOS DESHABILITADA ===");
            return;
        }

        if (usuarioRepository.count() > 0) {
            System.out.println("=== YA EXISTEN USUARIOS EN EL SISTEMA - NO SE INICIALIZAN DATOS ===");
            return;
        }

        System.out.println("=== INICIALIZANDO DATOS DEL SISTEMA (PRIMERA EJECUCION) ===");

        crearPermisosSiNoExisten();

        crearRolesSiNoExisten();

        crearUsuarioAdmin();

        System.out.println("=== INICIALIZACION COMPLETADA ===");
        System.out.println("========================================");
        System.out.println("USUARIO ADMINISTRADOR CREADO");
        System.out.println("Correo: admin@gasmanager.com");
        System.out.println("Cambia la contraseña desde el panel de administración");
        System.out.println("========================================");
    }

    private void crearPermisosSiNoExisten() {
        if (permisoRepository.count() > 0) {
            return;
        }
        Permiso[] permisos = {
                new Permiso("USUARIO_CREAR", "Crear Usuario", "Permite crear nuevos usuarios"),
                new Permiso("USUARIO_LEER", "Leer Usuario", "Permite ver usuarios"),
                new Permiso("USUARIO_ACTUALIZAR", "Actualizar Usuario", "Permite modificar usuarios"),
                new Permiso("USUARIO_ELIMINAR", "Eliminar Usuario", "Permite desactivar usuarios"),
                new Permiso("ROL_CREAR", "Crear Rol", "Permite crear roles"),
                new Permiso("ROL_LEER", "Leer Rol", "Permite ver roles"),
                new Permiso("ROL_ACTUALIZAR", "Actualizar Rol", "Permite modificar roles"),
                new Permiso("ROL_ELIMINAR", "Eliminar Rol", "Permite eliminar roles"),
                new Permiso("PERMISO_CREAR", "Crear Permiso", "Permite crear permisos"),
                new Permiso("PERMISO_LEER", "Leer Permiso", "Permite ver permisos"),
                new Permiso("AUDITORIA_LEER", "Leer Auditoria", "Permite ver registros de auditoria")
        };

        for (Permiso permiso : permisos) {
            permisoRepository.save(permiso);
            System.out.println("Permiso creado: " + permiso.getCodigoPermiso());
        }
    }

    private void crearRolesSiNoExisten() {
        if (rolRepository.count() > 0) {
            return;
        }

        Rol adminRol = new Rol("ADMIN", "Administrador del sistema con todos los permisos");
        permisoRepository.findAll().forEach(adminRol.getPermisos()::add);
        rolRepository.save(adminRol);
        System.out.println("Rol ADMIN creado");

        Rol usuarioRol = new Rol("USUARIO", "Usuario estandar con permisos basicos");
        permisoRepository.findByCodigoPermiso("USUARIO_LEER").ifPresent(usuarioRol.getPermisos()::add);
        permisoRepository.findByCodigoPermiso("USUARIO_ACTUALIZAR").ifPresent(usuarioRol.getPermisos()::add);
        rolRepository.save(usuarioRol);
        System.out.println("Rol USUARIO creado");

        Rol auditorRol = new Rol("AUDITOR", "Usuario con permisos solo de auditoria");
        permisoRepository.findByCodigoPermiso("AUDITORIA_LEER").ifPresent(auditorRol.getPermisos()::add);
        permisoRepository.findByCodigoPermiso("USUARIO_LEER").ifPresent(auditorRol.getPermisos()::add);
        rolRepository.save(auditorRol);
        System.out.println("Rol AUDITOR creado");
    }

    private void crearUsuarioAdmin() {
        Rol adminRol = rolRepository.findByNombreRol("ADMIN")
                .orElseThrow(() -> new RuntimeException("No se encontro el rol ADMIN"));

        Usuario admin = new Usuario();
        admin.setNombre("Administrador del Sistema");
        admin.setCorreo("admin@gasmanager.com");
        String adminPassword = System.getenv("ADMIN_INITIAL_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            adminPassword = "Cambiami123!";
        }
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRol(adminRol);
        admin.setEstado(EstadoUsuario.ACTIVO);
        admin.setActivo(true);
        admin.setIntentosFallidos(0);
        admin.setBloqueado(false);
        admin.setFechaCreacion(LocalDateTime.now());

        usuarioRepository.save(admin);
    }
}