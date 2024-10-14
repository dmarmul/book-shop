package org.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
