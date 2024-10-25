package com.identitye2e.lms.service;

import com.identitye2e.lms.entity.Book;

import java.util.List;
import java.util.Optional;


public interface ILibrary {
    void addBook(Book book);
    void removeBook(String isbn);
    Optional<Book> findBookByISBN(String isbn);
    List<Optional<Book>> findBooksByAuthor(String author);
    void borrowBook(String isbn);
    void returnBook(String isbn);
}
