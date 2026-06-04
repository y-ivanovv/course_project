package ru.edu.project.control.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BookCreateRequest {

    @NotBlank(message = "Название книги не должно быть пустым")
    private String title;

    @NotBlank(message = "Автор не должен быть пустым")
    private String author;

    @NotBlank(message = "ISBN не должен быть пустым")
    @Pattern(regexp = "^[0-9]{5}$", message = "ISBN должен состоять ровно из 5 цифр")
    private String isbn;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NotBlank(message = "Жанр не должен быть пустым")
    private String genre;

    // Конструкторы
    public BookCreateRequest() {}

    public BookCreateRequest(String title, String author, String isbn, String description, String genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.genre = genre;
    }

    // Геттеры и сеттеры
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}