package org.example.bookshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NonNull
    private String title;
    @Column(nullable = false)
    @NonNull
    private String author;
    @Column(unique = true, nullable = false)
    @NonNull
    private String isbn;
    @Column(nullable = false)
    @NonNull
    private BigDecimal price;
    private String description;
    private String coverImage;

    public Book() {

    }
}
