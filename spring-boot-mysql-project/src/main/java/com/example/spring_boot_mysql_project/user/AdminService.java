package com.example.spring_boot_mysql_project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//@Service dice a Spring che questa classe contiene logica di business
//e la registra come Bean - può essere iniettata in altri componenti
@Service
@RequiredArgsConstructor //genera il costruttore per l'iniezione di UserRepository
public class AdminService {

    //User repository è l'interfaccia che parla con il database
    //Spring la inietta automaticamente tramite il costruttore
    private final UserRepository userRepository;

    // Cambia il ruolo di un utente dato il suo id
    public String updateRole(Integer userId, Role newRole) {
        //cerca l'utente nel database per id
        //findById restituisce lo userId oppure se non c'è lancia un'eccezione
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con id: " + userId));

        //aggiorna il ruolo dell'utente con il nuovo valore ricevuto
        user.setRole(newRole);
        //salva le modifiche nel database, JPA genera automaticamente le query UPDATE
        userRepository.save(user);

        //restituisce un messaggio di conferma al controller
        return "Ruolo aggiornato: " + user.getEmail() + " è ora " + newRole.name();
    }

    // Metodo per ottenere tutti gli utenti dal database
    // utile per sapere gli id prima di cambiare un ruolo
    public Iterable<User> getAllUsers() {
        //JPA genera automaticamente SELECT * FROM user
        return userRepository.findAll();
    }
}
