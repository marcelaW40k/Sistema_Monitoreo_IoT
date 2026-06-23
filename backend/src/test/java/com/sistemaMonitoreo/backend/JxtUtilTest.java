package com.sistemaMonitoreo.backend;

import com.sistemaMonitoreo.backend.Security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Inicializamos el componente antes de cada test
        jwtUtil = new JwtUtil();
    }

    @Test
    @DisplayName("Debería retornar falso si se intenta validar un Token corrupto o inválido")
    void tokenInvalidoRetornaFalso() {
        String tokenInvalido = "un-token-falso-y-mal-estructurado";
        boolean resultadoValido = jwtUtil.validateToken(tokenInvalido);
        assertFalse(resultadoValido, "El interceptor debe rechazar tokens corruptos para proteger la API.");
    }
}