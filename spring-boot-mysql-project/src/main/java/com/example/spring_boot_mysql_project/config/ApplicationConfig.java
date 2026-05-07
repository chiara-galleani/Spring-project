package com.example.spring_boot_mysql_project.config;

import com.example.spring_boot_mysql_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    //UserDetailsService è un'interfaccia di Spring Security con un solo metodo:
    //loadUserByUsername(String username) --> Spring la chiama durante il login
    //per caricare i dati dell'utente nel database
    @Bean
    public UserDetailsService userDetailsService() {
        //username per noi corrisponde all'email
        return username -> repository.findByEmail(username)
                //se l'utente non esiste lancia UsernameNotFoundException
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    //AuthenticationProvider è il componente che gestisce la logica di autenticazione
    //confronta le credenziali inserite dall'utente con quelle nel database
    @Bean
    public AuthenticationProvider authenticationProvider() {
        //implementazione standard di Spring che usa il database per autenticare
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        //dice al provider come caricare l'utente dal database
        authProvider.setUserDetailsService(userDetailsService());
        //dice al provider come verificare la password (usa BCrypt)
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //AuthenticationManager è il coordinatore dell'autenticazione
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //gestisce l'hashing delle password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
