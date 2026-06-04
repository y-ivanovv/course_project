package ru.edu.project.foundation.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import ru.edu.project.entity.Book;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);

    /**
     * Полнотекстовый поиск по всем полям книги.
     * - title, description, author, genre — нечёткий полнотекстовый поиск (multi_match, fuzziness AUTO);
     * - isbn — точное совпадение (term), без нечёткости, чтобы не цеплять соседние номера.
     * Пагинация (from/size) применяется через переданный Pageable.
     */
    @Query("{\"bool\": {\"should\": ["
            + "{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"description\", \"author\", \"genre\"], \"fuzziness\": \"AUTO\"}},"
            + "{\"term\": {\"isbn\": \"?0\"}}"
            + "], \"minimum_should_match\": 1}}")
    Page<Book> searchByQuery(String query, Pageable pageable);
}
