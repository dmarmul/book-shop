package org.example.bookshop;

import java.math.BigDecimal;
import org.example.bookshop.model.Book;
import org.example.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookShopApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Harry Potter");
            book.setAuthor("Joanne Rowling");
            book.setIsbn("7655899867");
            book.setPrice(BigDecimal.valueOf(500));

            bookService.save(book);

            System.out.println(bookService.findAll());
        };
    }
}
