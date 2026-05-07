package com.example.spring_boot_mysql_project.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
//User Repository è l'interfaccia che gestisce tutte le operazioni sul database per la tabella user
//JpaRepository richiede:
//- User --> la classe Entity su cui opera
//- Integer --> il tipo della chiave primaria
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); //trova utente WHERE email = ?
}
