package ru.edu.project.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "books")
@Setting(settingPath = "es-config.json") // Подключаем наш JSON с настройками анализатора
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "russian_analyzer", searchAnalyzer = "russian_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "russian_analyzer", searchAnalyzer = "russian_analyzer")
    private String author;

    @Field(type = FieldType.Keyword)
    private String isbn;

    @Field(type = FieldType.Text, analyzer = "russian_analyzer", searchAnalyzer = "russian_analyzer")
    private String description; // Теперь полнотекстовый поиск по-русски будет работать и тут

    @Field(type = FieldType.Text, analyzer = "russian_analyzer", searchAnalyzer = "russian_analyzer")
    private String genre;

    @Field(type = FieldType.Keyword)
    private String status;

    // Конструкторы, бизнес-методы, геттеры и сеттеры остаются БЕЗ ИЗМЕНЕНИЙ
    public Book() {}

    public Book(String title, String author, String isbn, String description, String genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.genre = genre;
        this.status = "AVAILABLE";
    }

    public boolean isAvailable() { return "AVAILABLE".equals(this.status); }
    public void borrowBook() {
        if (!isAvailable()) throw new IllegalStateException("Книга '" + title + "' уже выдана!");
        this.status = "BORROWED";
    }
    public void returnBook() { this.status = "AVAILABLE"; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}