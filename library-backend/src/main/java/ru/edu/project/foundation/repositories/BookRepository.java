package ru.edu.project.foundation.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import ru.edu.project.entity.Book;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {
    
    Optional<Book> findByIsbn(String isbn);

    /**
     * Честный полнотекстовый поиск (multi_match) по двум полям с использованием анализатора
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"description\"], \"fuzziness\": \"AUTO\"}}")
    List<Book> searchByQuery(String query);
}