package com.sistemaMonitoreo.backend.service;

import com.sistemaMonitoreo.backend.DTO.LoginRequestDTO;
import com.sistemaMonitoreo.backend.DTO.LoginResponseDTO;
import com.sistemaMonitoreo.backend.Security.JwtUtil;
import com.sistemaMonitoreo.backend.model.User;
import com.sistemaMonitoreo.backend.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Valida las credenciales e inicia sesión.
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas: Usuario no encontrado."));

        // 2. Verificar contraseña hash de forma manual mediante BCrypt
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas: Contraseña erronea.");
        }

        // 3. Generar el Token JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponseDTO(token, user.getEmail(), user.getRole().name());
    }

    /**
     * Registra un nuevo usuario encriptando su contraseña de forma segura.
     */
    public User register(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya esta en uso.");
        }
        // Encriptar la contraseña antes de guardar en la DB
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }
}