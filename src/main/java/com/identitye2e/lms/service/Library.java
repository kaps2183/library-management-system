package com.identitye2e.lms.service;

import com.identitye2e.lms.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Service
public class Library implements ILibrary {
    private static final Logger logger = LoggerFactory.getLogger(Library.class);
    // TODO - extract to repo class - time permitting
    // isbn - is unique for title hence using it as key
    private final ConcurrentHashMap<String, Book> libraryDb;

    @Autowired
    public Library(ConcurrentHashMap<String, Book> libraryDb) {
        this.libraryDb = libraryDb;
    }

    @Override
    public void addBook(Book book) {
        libraryDb.put(book.getIsbn(), book);
        logger.info("added Book {}", book);
    }

    @Override
    public synchronized void removeBook(String isbn) {

        if (libraryDb.containsKey(isbn)) {
            Book removedBook = libraryDb.remove(isbn);
            logger.info("removed book title {}, isbn {}", removedBook.getTitle(), removedBook.getIsbn());
        }else{
            logger.info("isbn {} doesn't exists", isbn);
        }

    }

    @Override
    @Cacheable(value = "books", key = "#isbn", condition = "#result")
    public Optional<Book> findBookByISBN(String isbn) {
        Optional<Book> optionalBook = ofNullable(libraryDb.get(isbn));
        optionalBook.ifPresentOrElse(book -> logger.info("Found book {} for isbn {}", book.getTitle(), isbn), () -> logger.info("Value not Found for isbn {}", isbn));
        return optionalBook;
    }

    @Override
    @Cacheable(value = "booksByAuthor", key = "#author", condition = "#result")
    public List<Optional<Book>> findBooksByAuthor(String author) {
        List<Optional<Book>> optionalBooksByAuthor = libraryDb.values().stream().
                filter(book -> book.getAuthor().equals(author)).map(book -> of(book)).collect(Collectors.toList());
        logger.info("Found {} books by author {}", optionalBooksByAuthor.size(), author);
        return optionalBooksByAuthor;
    }

    @Override
    public void borrowBook(String isbn) {
        Optional<Book> optionalBook = ofNullable(libraryDb.get(isbn));
        optionalBook.ifPresentOrElse(book -> {
            int availableCopies = book.getAvailableCopies().decrementAndGet();
            logger.info("New number of available copies-> {} for isbn {}", availableCopies, isbn);
        }, () -> logger.info("No value for isbn {}", isbn));
    }

    @Override
    public void returnBook(String isbn) {
        Optional<Book> optionalBook = ofNullable(libraryDb.get(isbn));
        optionalBook.ifPresentOrElse(book -> {
            int availableCopies = book.getAvailableCopies().incrementAndGet();
            logger.info("New number of available copies-> {} for isbn {}", availableCopies, isbn);
        }, () -> logger.info("No value for isbn {}", isbn));

    }
}
