package com.example.spring_boot_mysql_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@RestController = @Controller + @ResponseBody
// Dice a Spring che questa classe gestisce richieste HTTP
// e che le risposte vanno serializzate in JSON automaticamente
@RestController
// Tutte le rotte di questo Controller iniziano con /geek
@RequestMapping(value = "/geek")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // Gestisce le richieste POST su /geek/addbook
    @PostMapping(value = "/addbook")
    public String addBooks(@RequestParam String bookName, // parametro dall'URL
                           @RequestParam String isbnNumber) { // parametro dall'URL
        // Crea un oggetto Book e imposta i campi
        Book book = new Book();
        book.setBookName(bookName);
        book.setIsbnNumber(isbnNumber);
        // Salva il libro nel database tramite JPA
        // Spring Data genera la query SQL INSERT in automatico
        bookRepository.save(book);
        return "Details got Saved"; // risposta testuale al client
    }

    // Gestisce le richieste GET su /geek/books
    @GetMapping(value = "/books")
    // Restituisce tutti i libri nel database come array JSON
    public Iterable<Book> getAllUsers() {
        // Spring Data genera la query SELECT * FROM book in automatico
        return bookRepository.findAll();
    }
}