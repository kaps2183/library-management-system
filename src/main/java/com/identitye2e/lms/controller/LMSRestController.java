package com.identitye2e.lms.controller;

import com.identitye2e.lms.entity.Book;
import com.identitye2e.lms.service.ILibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("lms")
public class LMSRestController {
    private final ILibrary library;

    @Autowired
    public LMSRestController(ILibrary library) {
        this.library = library;
    }

    @PostMapping("/addBook")
    public void addBook(@RequestBody Book book) {
        library.addBook(book);
    }

    @GetMapping(value = "/findBookByISBN")
    @ResponseBody
    public Book findBookByISBN(@RequestParam String isbn) {
        Optional<Book> optionalBookByISBN = library.findBookByISBN(isbn);
        return optionalBookByISBN.orElseThrow();
    }


    @GetMapping(value = "/findBookByAuthor")
    @ResponseBody
    public List<Book> findBookByAuthor(@RequestParam String author) {
        List<Optional<Book>> optionalsBookByAuthor = library.findBooksByAuthor(author);
        List<Book> booksByAuthor = optionalsBookByAuthor.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        return booksByAuthor;
    }

    @PatchMapping(value = "/borrowBook")
    @ResponseBody
    public void borrowBook(@RequestParam String isbn) {
        library.borrowBook(isbn);
    }

    @PatchMapping(value = "/returnBook")
    @ResponseBody
    public void returnBook(@RequestParam String isbn) {
        library.returnBook(isbn);
    }

    @DeleteMapping(value = "/removeBook")
    @ResponseBody
    public void removeBook(@RequestParam String isbn) {
        library.removeBook(isbn);
    }
}
