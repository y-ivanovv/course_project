package ru.edu.project.control.dto;

import jakarta.validation.constraints.NotBlank;

public class BookSearchRequest {
    
    @NotBlank(message = "Поисковый запрос не должен быть пустым")
    private String query;

    public BookSearchRequest() {}

    public BookSearchRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}