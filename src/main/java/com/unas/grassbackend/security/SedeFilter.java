package com.unas.grassbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SedeFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    try {
	        String headerSede = request.getHeader("X-Sede-Activa");
	        
	        // Para pruebas temporales mientras activamos la seguridad estricta:
	        if (headerSede != null && !headerSede.isEmpty()) {
	            SedeContextHolder.setSede(headerSede);
	            System.out.println(" Ojo de Dios activado -> Sede interceptada: " + headerSede);
	        } else {
	            SedeContextHolder.setSede("GLOBAL");
	        }

	        filterChain.doFilter(request, response);
	    } finally {
	        SedeContextHolder.clear();
	    }
	}
}