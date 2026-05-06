package com.example.spring_boot_mysql_project;

import org.springframework.data.repository.CrudRepository;
// This will be AUTO IMPLEMENTED by Spring
// into a Bean called Book
// CRUD refers Create, Read, Update, Delete
public interface BookRepository extends CrudRepository<Book, Integer> {

}
