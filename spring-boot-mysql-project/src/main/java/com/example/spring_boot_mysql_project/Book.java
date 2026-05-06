package com.example.spring_boot_mysql_project;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// @Entity dice all'Hibernate (l'ORM usato da Spring)
// di creare e gestire una tabella chiamata "book" nel database
// ORM = Object Relational Mapping: traduce automaticamente
// oggetti Java <-> righe del database, senza scrivere SQL a mano
@Entity
public class Book {
    // @Id è la chiave primaria della tabella (colonna "id")
    @Id
    // @GeneratedValue = il valore dell'id viene generato automaticamente
    // GenerationType.AUTO = lascia decidere a Hibernate la strategia
    // (di solito usa una sequenza o auto-increment di MySQL)
    // Non serve impostare l'id a mano quando creo Book
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    // Questi due campi diventano colonne della tabella "book"
    // Hibernate le crea automaticamente con il tipo VARCHAR
    private String bookName;
    private String isbnNumber;

    // GETTER e SETTER
    // Metodi standard Java per leggere e scrivere i campi privati
    // Hibernate ne ha bisogno per costruire e leggere gli oggetti dal database
    // Su IntelliJ si generano automaticamente con: tasto destro -> Generate -> Getter and Setter
    public String getBookName() {
        return bookName; // legge il valore di bookName
    }

    public void setBookName(String bookName) {
        this.bookName = bookName; //this.bookName = campo della classe
                                  //bookName = parametro del metodo
    }

    public String getIsbnNumber() {
        return isbnNumber;
    }

    public void setIsbnNumber(String isbnNumber) {
        this.isbnNumber = isbnNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
