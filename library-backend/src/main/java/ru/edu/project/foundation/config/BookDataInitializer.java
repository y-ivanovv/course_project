package ru.edu.project.foundation.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.edu.project.entity.Book;
import ru.edu.project.foundation.repositories.BookRepository;

/**
 * Наполняет индекс книг в Elasticsearch демонстрационными данными из books-seed.json,
 * если индекс пуст. Идемпотентно: при повторных запусках ничего не дублирует.
 * Управляется свойством app.books.seed-enabled.
 */
@Component
@Order(2)
public class BookDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BookDataInitializer.class);
    private static final String SEED_FILE = "books-seed.json";

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.books.seed-enabled:true}")
    private boolean enabled;

    public BookDataInitializer(BookRepository bookRepository, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        long existing = bookRepository.count();
        if (existing > 0) {
            log.info("В индексе уже {} книг — загрузка демо-каталога пропущена", existing);
            return;
        }

        try (InputStream is = new ClassPathResource(SEED_FILE).getInputStream()) {
            JsonNode array = objectMapper.readTree(is);
            List<Book> books = new ArrayList<>();
            for (JsonNode node : array) {
                // Конструктор сам выставляет статус AVAILABLE
                books.add(new Book(
                        node.get("title").asText(),
                        node.get("author").asText(),
                        node.get("isbn").asText(),
                        node.get("description").asText(),
                        node.get("genre").asText()));
            }
            bookRepository.saveAll(books);
            log.info("Загружено {} книг в Elasticsearch из {}", books.size(), SEED_FILE);
        } catch (Exception e) {
            log.error("Не удалось загрузить демо-каталог книг из {}", SEED_FILE, e);
        }
    }
}
