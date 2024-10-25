package com.identitye2e.lms.service;

import com.identitye2e.lms.entity.Book;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class LibraryTest {

    private Library library;
    private ConcurrentHashMap<String, Book> spyLibraryDb;
    private Book mockBook;
    private AtomicInteger spyAvlQty;

    @BeforeMethod
    public void setUp() {
        mockBook = mock(Book.class);
        when (mockBook.getIsbn()).thenReturn("isbn-123");
        when(mockBook.getAuthor()).thenReturn("J K Rowling");
        when(mockBook.getTitle()).thenReturn("Harry Potter");
        spyAvlQty = spy(new AtomicInteger(200));
        when(mockBook.getAvailableCopies()).thenReturn(spyAvlQty);
        ConcurrentHashMap<String, Book> libraryDb = new ConcurrentHashMap<>();
        libraryDb.put("isbn-123", mockBook);
        spyLibraryDb = spy(libraryDb);
        library = new Library(spyLibraryDb);
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testAddBook() {
        Book mockBook = mock(Book.class);
        when(mockBook.getIsbn()).thenReturn("isbn-0010");
        when(mockBook.toString()).thenReturn("Test Book");
        library.addBook(mockBook);
        verify(mockBook).getIsbn();
        verify(spyLibraryDb).put("isbn-0010", mockBook);
        assertEquals(spyLibraryDb.size(), 2);
    }

    @Test
    public void shouldLogErrorOnRemovingNonExistentIsbn() {
        library.removeBook("isbn-0010");
        verify(spyLibraryDb).containsKey("isbn-0010");
    }

    @Test
    public void shouldRemoveBook() {
        library.removeBook("isbn-123");
        verify(spyLibraryDb).containsKey("isbn-123");
        verify(spyLibraryDb).remove("isbn-123");
    }

    @Test
    public void shouldFindBookByISBN() {
        when (mockBook.getIsbn()).thenReturn("isbn-123");
        Optional<Book> bookByISBN = library.findBookByISBN("isbn-123");
        verify(spyLibraryDb).get("isbn-123");
        assertEquals(bookByISBN.get().getIsbn(), "isbn-123");
    }

    @Test
    public void testFindBooksByAuthor() {

        List<Optional<Book>> optionalBookByAuthor = library.findBooksByAuthor("J K Rowling");
        Book book = optionalBookByAuthor.stream().map(Optional::get).findAny().get();
        assertEquals(book.getIsbn(),"isbn-123");
        assertEquals(book.getAuthor(),"J K Rowling");
    }

    @Test
    public void testBorrowBook() {
        library.borrowBook("isbn-123");
        verify(mockBook).getAvailableCopies();
        verify(spyAvlQty).decrementAndGet();
        assertEquals(spyAvlQty.get(), 199);
    }

    @Test
    public void testReturnBook() {
        library.returnBook("isbn-123");
        verify(mockBook).getAvailableCopies();
        verify(spyAvlQty).incrementAndGet();
        assertEquals(spyAvlQty.get(), 201);
    }

}