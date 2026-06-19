package com.sistemaMonitoreo.backend.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Permitir solicitudes de pre-vuelo CORS (crucial para conectar con React)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        // Excluir de forma explícita el endpoint público de Login y de Registro
        if (path.startsWith("/api/auth/")) {
            return true;
        }

        // Obtener el encabezado HTTP 'Authorization'
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Error: Token de autenticacion ausente.");
            return false; // Bloquea la petición
        }

        String token = authHeader.substring(7); // Extrae la cadena de texto después de "Bearer "

        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Error: Token invalido o expirado.");
            return false;
        }

        // Inyectar la metadata del usuario en los atributos del Request para uso posterior en el controlador
        request.setAttribute("userEmail", jwtUtil.extractEmail(token));
        request.setAttribute("userRole", jwtUtil.extractRole(token));

        // Validación de Rutas Administrativas
        if (path.contains("/admin/") && !"ADMIN".equals(jwtUtil.extractRole(token))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Error: No tienes permisos de administrador para este recurso.");
            return false;
        }

        return true; // Acceso concedido, continúa al Controller
    }
}