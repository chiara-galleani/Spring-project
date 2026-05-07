package com.example.spring_boot_mysql_project.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController combina @Controller + @ResponseBody
//indica che questa classe gestisce richieste HTTP
//e ogni metodo restituisce direttamente il body della risposta
@RestController
//@RequestMapping: definisce il path base per tutti gli endpoint di questa classe
@RequestMapping(value = "/api/v1/demo-controller")
public class DemoController {

    //@GetMapping gestisce le richieste HTTP GET su /api/v1/demo-controller
    @GetMapping
    //ResponseEntity<String> permette di controllare sia il body che lo status HTTP
    public ResponseEntity<String> sayHello() {
        //ResponseEntity.ok restituisce una risposta con:
        // - status HTTP 200 OK
        // - body: la stringa "Hello from secured endpoint"
        //questo endpoint è protetto da JWT (vedi SecurityConfiguration):
        //solo utenti autenticati con token valido possono raggiungerlo
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
