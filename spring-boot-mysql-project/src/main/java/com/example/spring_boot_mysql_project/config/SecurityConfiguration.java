package com.example.spring_boot_mysql_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration: indica a Spring che questa classe contiene definizioni di bean
@Configuration
//@EnableWebSecurity: abilita il modulo Spring Security e ne prende il controllo
@EnableWebSecurity
//@RequiredArgsConstructor: lombok genera il costruttore con i campi final
@RequiredArgsConstructor
public class SecurityConfiguration {

    //il filtro JWT custom che intercetta ogni richiesta per validare il token
    private final JwtAuthenticationFilter jwtAuthFilter;
    //provider che gestisce la logica di autenticazione (verifica credenziali, caricamento utente)
    //definito in ApplicationConfig
    private final AuthenticationProvider authenticationProvider;

    //definisce la catena di filtri di sicurezza applicata ad ogni richiesta HTTP
    //è il cuore della configurazione di Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    //disabilita la protezione CSRF (Cross-Site Request Forgery)
                //lecito in API REST stateless: il JWT stesso protegge dalle richieste
                //non autorizzate e non si usano sessioni/cookie di sessione
                .csrf(AbstractHttpConfigurer::disable)

                //configura le regole di autorizzazione degli altri endpoint
                .authorizeHttpRequests(auth -> auth
                        //endpoint pubblici: chiunque può accedere senza autenticazione
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        //endpoint riservati: solo utenti con ruolo ADMIN possono accedere
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        //qualsiasi altro endpoint richiede autenticazione (JWT valido)
                        .anyRequest().authenticated()
                )

                //configura la gestione della sessione come stateless:
                //Spring Security non crea né usa sessioni HTTP lato server
                //ogni richiesta deve portare con sé il JWT per essere autenticata
                //questo è il comportamento corretto per API REST con JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                //registra il provider di autenticazione custom
                //definito in ApplicationConfig con password encoder e UserDetailsService
                .authenticationProvider(authenticationProvider)
                //aggiungo il filtro JWT prima del filtro standard di Spring Security
                //così ogni richiesta viene prima autenticata tramite JWT, prima ancora
                // che Spring tenti l'autenticazione classica username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); //costruisce e restituisce la SecurityFilterChain configurata
    }
}
