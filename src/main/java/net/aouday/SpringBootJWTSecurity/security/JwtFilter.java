package net.aouday.SpringBootJWTSecurity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aouday.SpringBootJWTSecurity.exceptions.handlers.RestAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final RequestMatcher requestMatcher=new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher("/error"),
            PathPatternRequestMatcher.withDefaults().matcher("/public/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/auth/**")
    );

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint entryPoint;

    public JwtFilter(JwtUtils jwtUtils,
                     UserDetailsService userDetailsService,
                     RestAuthenticationEntryPoint entryPoint
                     ) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(requestMatcher.matches(request)){
            filterChain.doFilter(request, response);
            return;
        }

        if(SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }


        try {
            //récupérer le header
            String authHeader = request.getHeader("Authorization");

            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                throw new BadCredentialsException("Missing or invalid Authorization header");
            }

            //extraire token
            String token = authHeader.substring(7);

            //valider le token
            if(!jwtUtils.validateToken(token)){
                throw new BadCredentialsException("Invalid or expired JWT");
            }
            try {
                //extraire email
                String email = jwtUtils.getEmailFromToken(token);

                //charger l'utilisateur
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // crée l'authentification
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                // mettre l'utilisateur dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (Exception e) {
                logger.error("Could not authenticate user", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Authentication failed\"}");
                return;
            }
            // continuer la chaine de filters
            filterChain.doFilter(request, response);
        }catch (AuthenticationException ex){
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, ex);
        }catch (Exception e){
            logger.error("Unexpected auth error", e);
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, new BadCredentialsException("Authentication failed"));
        }


    }
}
