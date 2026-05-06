package com.example.spring_boot_mysql_project.auth;

import com.example.spring_boot_mysql_project.config.JwtService;
import com.example.spring_boot_mysql_project.user.Role;
import com.example.spring_boot_mysql_project.user.User;
import com.example.spring_boot_mysql_project.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // **Registrazione**
    public AuthenticationResponse register(RegisterRequest request) {
        // Controlla se esiste già un utente con quella email
        // findByEmail restituisce un Optional — isPresent() è true se l'utente esiste
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata: " + request.getEmail());
        }
        //costruisco l'oggetto user con i parametri dalla richiesta,
        // la password criptata e Role che è user di default
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user); //salva l'utente nella tabella user (repository - database)
        var jwtToken = jwtService.generateToken(user); //genera token JWT contenente info utente
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build(); //restituisce il token al client
    }

    // **Autenticazione(login)**
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate( //controlla che email e password siano corrette
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ); // confronta la password inserita con quella salvata (criptata) nel database
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(); //recupero utente (e tutti i suoi dati) dal DB
        var jwtToken = jwtService.generateToken(user); //generazione JWT
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build(); //ritorna il token al client
    }
}
