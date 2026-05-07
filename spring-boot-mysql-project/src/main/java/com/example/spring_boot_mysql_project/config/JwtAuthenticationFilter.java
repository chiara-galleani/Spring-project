package com.example.spring_boot_mysql_project.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component: registra questa classe come bean Spring, gestita dal container
@Component
//@RequiredArgsConstructor: Lombok genera automaticamente un costruttore con
//tutti i campi final come parametri (iniezione delle dipendenze)
@RequiredArgsConstructor
//OncePerRequestFilter garantisce che questo filtro venga eseguito
//esattamente UNA volta per ogni richiesta HTTP
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //servizio per operazioni JWT (estrazione claims, validazione token...)
    private final JwtService jwtService;
    //servizio Spring Security per caricare i dettagli utente dal database
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request, //richiesta HTTP in arrivo
            @Nonnull HttpServletResponse response, //risposta HTTP in uscita
            @Nonnull FilterChain filterChain //catena dei filtri successivi
    ) throws ServletException, IOException {
        //legge l'header "Authorization" dalla richiesta HTTP
        //formato atteso: "Bearer <token_jwt>
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        //se l'header è assente oppure non inizia con Bearer, la richiesta non contiene un JWT
        //si passa al filtro successivo senza autenticare l'utente (bloccato da Spring Security
        //se l'endpoint richiede autenticazione
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //estrae il token JWT rimuovendo il prefisso "Bearer_" (7 caratteri)
        jwt = authHeader.substring(7);
        //estrae lo username (email) dal payload del token JWT
        userEmail = jwtService.extractUsername(jwt);
        //Procede solo se:
        // 1. lo username è stato estratto con successo (token non malformato)
        // 2. non esiste già un'autenticazione nel contesto di sicurezza corrente
        //    (evita di ri-autenticare una richiesta già autenticata)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //carica i dettagli dell'utente dal database tramite lo username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            //verifica che il token JWT sia valido:
            // - la firma è corretta
            // - il token non è scaduto
            // - lo username nel token corrisponde all'utente caricato
            if (jwtService.isTokenValid(jwt, userDetails)) {
                //crea un oggetto di autenticazione Spring Security con:
                // - principal: i dettagli dell'utente
                // - credentials: null (non servono, autenticazione già avvenuta via JWT)
                // - authorities: i ruoli e permessi dell'utente
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                //aggiunge dettagli extra alla richiesta (indirizzo IP, session ID)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //salva il token di autenticazione nel SecurityContext
                //rendendo l'utente autenticato per tutta la durata della richiesta
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //passa la richiesta al filtro successivo della catena
        filterChain.doFilter(request, response);
    }
}
