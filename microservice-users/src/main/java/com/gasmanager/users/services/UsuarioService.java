package com.gasmanager.users.services;

import com.gasmanager.users.entities.core.AuditoriaAccion;
import com.gasmanager.users.entities.core.EstadoUsuario;
import com.gasmanager.users.entities.core.Usuario;
import com.gasmanager.users.entities.security.TipoAcccion;
import com.gasmanager.users.repositories.AuditoriaAccionRepository;
import com.gasmanager.users.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaAccionRepository auditoriaAccion;
    private final BCryptPasswordEncoder passwordEncoder;

    // Metodos CRUD

    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya esta registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());

        Usuario nuevo = usuarioRepository.save(usuario);

        registrarAuditoriaCompleta(
                nuevo.getIdUsuario(),
                TipoAcccion.CREAR,
                "Usuario creado exitosamente",
                "Usuarios",
                "Sistema",
                null,
                "{\"nombre\":\"" + usuario.getNombre() +
                        "\",\"correo\":\"" + usuario.getCorreo() +
                        "\",\"rol\":\"" + (usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Sin asignar") + "\"}"
        );
        return nuevo;
    }

    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findAllByActivoTrue();
    }

    public List<Usuario> listarUsuariosBloqueados() {
        return usuarioRepository.findByBloqueadoTrue();
    }

    // Actualizar Usuario
    public Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String datosAnteriores = String.format(
                "{\"nombre\":\"%s\",\"rol\":\"%s\",\"estado\":\"%s\",\"activo\":%s}",
                usuarioExistente.getNombre(),
                usuarioExistente.getRol() != null ? usuarioExistente.getRol().getNombreRol() : "null",
                usuarioExistente.getEstado(),
                usuarioExistente.isActivo()
        );
        if (usuarioActualizado.getNombre() != null) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
        }
        if (usuarioActualizado.getRol() != null) {
            usuarioExistente.setRol(usuarioActualizado.getRol());
        }
        if (usuarioActualizado.getEstado() != null) {
            usuarioExistente.setEstado(usuarioActualizado.getEstado());

            if (usuarioActualizado.getEstado() == EstadoUsuario.ACTIVO) {
                usuarioExistente.setActivo(true);
            }
            if (usuarioActualizado.getEstado() == EstadoUsuario.INACTIVO ||
                    usuarioActualizado.getEstado() == EstadoUsuario.BLOQUEADO) {
                usuarioExistente.setActivo(false);
            }
        }
        if(usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()){
            usuarioExistente.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }

        Usuario guardado = usuarioRepository.save(usuarioExistente);

        registrarAuditoriaCompleta(
                usuarioExistente.getIdUsuario(),
                TipoAcccion.ACTUALIZAR,
                "Usuario actualizado",
                "Usuarios",
                "Sistema",
                datosAnteriores,
                String.format(
                        "{\"nombre\":\"%s\",\"rol\":\"%s\",\"estado\":\"%s\",\"activo\":%s}",
                        guardado.getNombre(),
                        guardado.getRol() != null ? guardado.getRol().getNombreRol() : "null",
                        guardado.getEstado(),
                        guardado.isActivo()
                )
        );
        return guardado;
    }

    // Desactivar Usuario
    public boolean desactivarUsuario(Integer id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setActivo(false);
        usuario.setEstado(EstadoUsuario.INACTIVO);

        usuarioRepository.save(usuario);

        registrarAuditoriaCompleta(
                usuario.getIdUsuario(),
                TipoAcccion.ELIMINAR,
                "Usuario desactivado",
                "Usuarios",
                "Sistema",
                "{\"activo\":true}",
                "{\"activo\":false,\"estado\":\"INACTIVO\"}"
        );
        return true;
    }

    // Metodos de Autenticar
    public Optional<Usuario> autenticar(String correo, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (usuario.isBloqueado()) {
                registrarAuditoriaCompleta(
                        usuario.getIdUsuario(),
                        TipoAcccion.VALIDAR,
                        "Intento de login con usuario bloqueado",
                        "Login",
                        "Web",
                        null,
                        "{\"estado\":\"BLOQUEADO\"}"
                );
                return Optional.empty();
            }

            boolean passwordMatches = passwordEncoder.matches(password, usuario.getPassword());

            if (passwordMatches) {
                usuario.setIntentosFallidos(0);
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);

                registrarAuditoriaCompleta(
                        usuario.getIdUsuario(),
                        TipoAcccion.VALIDAR,
                        "Login exitoso",
                        "Login",
                        "WEB",
                        null,
                        "{\"ultimoAcceso\":\"" + LocalDateTime.now() +
                                "\",\"rol\":\"" + (usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Sin rol") + "\"}"
                );
                return Optional.of(usuario);
            } else {
                usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
                String descripcion = "Login fallido - Intento #" + usuario.getIntentosFallidos();

                if (usuario.getIntentosFallidos() >= 3) {
                    usuario.setBloqueado(true);
                    usuario.setEstado(EstadoUsuario.BLOQUEADO);
                    descripcion = "USUARIO BLOQUEADO - 3 intentos fallidos";

                    registrarAuditoriaCompleta(
                            usuario.getIdUsuario(),
                            TipoAcccion.VALIDAR,
                            descripcion,
                            "Login",
                            "WEB",
                            null,
                            "{\"intentosFallidos\":3,\"bloqueado\":true}"
                    );
                }
                usuarioRepository.save(usuario);
                return Optional.empty();
            }
        } else {
            log.warn("Intento de login con usuario inexistente: {}", correo);
            return Optional.empty();
        }
    }

    // Metodos de SEGURIDAD
    public boolean desbloquearYResetearPassword(Integer idUsuario, String nuevaPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();

        String datosAnteriores = String.format(
                "{\"bloqueado\":%s,\"intentosFallidos\":%d,\"passwordHash\":\"%s\"}",
                usuario.isBloqueado(),
                usuario.getIntentosFallidos(),
                usuario.getPassword().substring(0, Math.min(20, usuario.getPassword().length())) + "..."
        );
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        registrarAuditoriaCompleta(
                usuario.getIdUsuario(),
                TipoAcccion.ACTUALIZAR,
                "Usuario desbloqueado y contraseña restablecida",
                "Seguridad",
                "SISTEMA",
                datosAnteriores,
                "{\"bloqueado\":false,\"intentosFallidos\":0,\"estado\":\"ACTIVO\"}"
        );
        return true;
    }

    public boolean requiereResetPassword(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        return usuarioOpt.map(usuario -> usuario.isBloqueado() && usuario.getIntentosFallidos() >= 3)
                .orElse(false);
    }

    // Metodo de AUDITORIA
    private void registrarAuditoriaCompleta(
            Integer idUsuarioEjecutor,
            TipoAcccion acccion,
            String descripcion,
            String moduloAfectado,
            String origen,
            String datosAnteriores,
            String datosNuevos) {
        AuditoriaAccion auditoria = new AuditoriaAccion();
        auditoria.setIdUsuarioEjecutor(idUsuarioEjecutor);
        auditoria.setTipoAcccion(acccion);
        auditoria.setDescripcion(descripcion);
        auditoria.setModuloAfectado(moduloAfectado);
        auditoria.setOrigen(origen);
        auditoria.setDatosAnteriores(datosAnteriores);
        auditoria.setDatosNuevos(datosNuevos);
        auditoria.setFechaHora(LocalDateTime.now());

        auditoriaAccion.save(auditoria);
    }
}