package com.mariluz.sales.security;

import com.mariluz.sales.model.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final HandlerExceptionResolver exceptionResolver;

    @Autowired
    public JwtAuthenticationFilter(
        JwtUtil jwtUtil,
        // Inyección de dependencia para resolver excepciones
        @Qualifier(
            "handlerExceptionResolver"
        ) HandlerExceptionResolver exceptionResolver
    ) {
        this.jwtUtil = jwtUtil;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    User currentUser = jwtUtil.getUserFromToken(token);

                    List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(
                            "ROLE_" + currentUser.getRole()
                        )
                    );

                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            authorities
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(
                            request
                        )
                    );

                    SecurityContextHolder.getContext().setAuthentication(
                        authToken
                    );
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                // ademas de limpiar el contexto arrojamos el error para manejarlo desde el handler
                exceptionResolver.resolveException(request, response, null, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (
            StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")
        ) {
            return authHeader.substring(7);
        }

        return null;
    }
}
