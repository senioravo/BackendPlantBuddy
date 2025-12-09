package com.example.BackendPlantBuddy.service;

import com.example.BackendPlantBuddy.dto.AuthResponseDTO;
import com.example.BackendPlantBuddy.dto.LoginRequestDTO;
import com.example.BackendPlantBuddy.dto.RegisterRequestDTO;
import com.example.BackendPlantBuddy.dto.UsuarioDTO;
import com.example.BackendPlantBuddy.entity.Usuario;
import com.example.BackendPlantBuddy.mapper.EntityMapper;
import com.example.BackendPlantBuddy.repository.UsuarioRepository;
import com.example.BackendPlantBuddy.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EntityMapper mapper;
    
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Validar contraseñas coinciden
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }
        
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Verificar si el username ya existe
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        // createdAt se establece automáticamente con @CreationTimestamp
        
        Usuario guardado = usuarioRepository.save(usuario);
        
        // Generar token
        String token = jwtUtil.generateToken(guardado.getEmail());
        
        UsuarioDTO usuarioDTO = mapper.toUsuarioDTO(guardado);
        return new AuthResponseDTO(token, usuarioDTO);
    }
    
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
        // Generar token
        String token = jwtUtil.generateToken(usuario.getEmail());
        
        UsuarioDTO usuarioDTO = mapper.toUsuarioDTO(usuario);
        return new AuthResponseDTO(token, usuarioDTO);
    }
    
    public UsuarioDTO obtenerPerfilPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapper.toUsuarioDTO(usuario);
    }
    
    @Transactional
    public UsuarioDTO actualizarPerfil(String email, String profileImageUrl) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            usuario.setProfileImageUrl(profileImageUrl);
        }
        
        Usuario actualizado = usuarioRepository.save(usuario);
        return mapper.toUsuarioDTO(actualizado);
    }
}
