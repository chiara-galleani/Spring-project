package com.example.spring_boot_mysql_project.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//gestisce le richieste HTTP e serializza le risposte in JSON
@RestController
//tutte le rotte di questo controller iniziano con /api/v1/admin
//nel SecurityConfig abbiamo detto che /api/v1/admin/** richiede ruolo ADMIN
//quindi Spring blocca automaticamente chi non è admin prima di arrivare qui
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    //il controller non contiene logica, ma delega tutto all'AdminService
    private final AdminService adminService;

    // ET /api/v1/admin/users
    //Restituisce la lista di tutti gli utenti registrati
    //Serve principalmente per vedere gli id prima di cambiare un ruolo
    @GetMapping(value = "/users")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        //risposta HTTP 200 con il body specificato
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    //PATCH /api/v1/admin/users/{userId}/role
    //Cambia il ruolo di un utente specifico
    //Uso PATCH e non PUT perché modifichiamo SOLO il ruolo, non l'intera risorsa utente
    @PatchMapping(value = "/users/{userId}/role")
    public ResponseEntity<String> updateRole(
            //@PathVariable estrae il valore {userId} dall'URL
            //es: /api/v1/admin/users/3/role → userId = 3
            @PathVariable Integer userId,
            //@RequestBody de-serializza il JSON del body in un oggetto Java
            //es: { "role": "ADMIN" } → RoleUpdateRequest con role = ADMIN
            @RequestBody RoleUpdateRequest request
    ) {
        //passa l'id e il nuovo ruolo al Service che gestisce la logica
        return ResponseEntity.ok(adminService.updateRole(userId, request.getRole()));
    }
}
