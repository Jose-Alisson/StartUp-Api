package br.start.up.filters;

import br.start.up.detail.UserAuthLoader;
import br.start.up.jwt.JwtService;
import br.start.up.services.AccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Service
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService service;

    @Autowired
    private UserAuthLoader authLoader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");

        if (authorization != null && !authorization.isBlank()) {
            if (authorization.startsWith("Bearer ")) {
                var token = authorization.substring(7);

                var payload = service.verify(token);
                var detail = authLoader.loadUserById(payload.getClaim("id").asLong());

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(detail.getPrincipal(), null, detail.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList()
                        ));

            }
        }

        filterChain.doFilter(request, response);
    }
}
