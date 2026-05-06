package com.example.spring_boot_mysql_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication combina 3 annotazioni:
// - @SpringBootConfiguration: dice che questa è la classe di configurazione principale
// - @EnableAutoConfiguration: Spring configura automaticamente tutto quello che trova nel classpath
// - @ComponentScan: Spring scansiona tutti i file del package e sottopacchetti
// cercando annotazioni come @Entity, @RestController, @Repository e li registra automaticamente
@SpringBootApplication
public class SampleAccessingOfMysqlApplication {
	public static void main(String[] args) { // Main: metodo di ingresso di qualsiasi programma Java
		// SpringApplication.run fa partire tutto:
		// 1. Avvia il contesto di Spring (crea tutti i Bean/oggetti gestiti)
		// 2. Avvia il server Tomcat integrato (di default sulla porta 8080)
		// 3. Registra tutti i controller, repository, entity trovati
		// Il primo argomento è la classe stessa, il secondo sono eventuali argomenti passati da riga di comando
		SpringApplication.run(SampleAccessingOfMysqlApplication.class, args);
	}
}