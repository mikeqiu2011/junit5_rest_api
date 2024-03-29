package com.rest.app;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/book")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBookRecords() {
        return bookRepository.findAll();
    }

    @GetMapping(value = "{bookId}")
    public ResponseEntity<Book> getBookById(@PathVariable(value = "bookId") Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(optionalBook.get());
    }

    @PostMapping
    public Book createBookRecord(@RequestBody @Valid Book bookRecord) {
        return bookRepository.save(bookRecord);
    }

    @PutMapping
    public Book updateBookRecord(@RequestBody @Valid Book bookRecord) throws NotFoundException {
        if (bookRecord == null || bookRecord.getBookId() == null) {
            throw new NotFoundException("BookRecord or ID must not be null!");
        }

        Optional<Book> optionalBook = bookRepository.findById(bookRecord.getBookId());
        if (!optionalBook.isPresent()) {
            throw new NotFoundException("Book with ID: " + bookRecord.getBookId() + " does not exist");
        }

        Book existingBookRecord = optionalBook.get();
        existingBookRecord.setName(bookRecord.getName());
        existingBookRecord.setSummary(bookRecord.getSummary());
        existingBookRecord.setRating(bookRecord.getRating());

        return bookRepository.save(existingBookRecord);
    }

    @DeleteMapping(value = "{bookId}")
    public void deleteBookById(@PathVariable(value = "bookId") Long bookId) throws NotFoundException {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            throw new NotFoundException("bookId " + bookId + " not found");
        }

        bookRepository.deleteById(bookId);
    }
}
