package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class AuthorRepositoryWithBagRelationshipsImpl implements AuthorRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String AUTHORS_PARAMETER = "authors";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Author> fetchBagRelationships(Optional<Author> author) {
        return author.map(this::fetchBooks);
    }

    @Override
    public Page<Author> fetchBagRelationships(Page<Author> authors) {
        return new PageImpl<>(fetchBagRelationships(authors.getContent()), authors.getPageable(), authors.getTotalElements());
    }

    @Override
    public List<Author> fetchBagRelationships(List<Author> authors) {
        return Optional.of(authors).map(this::fetchBooks).orElse(Collections.emptyList());
    }

    Author fetchBooks(Author result) {
        return entityManager
            .createQuery("select author from Author author left join fetch author.books where author.id = :id", Author.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Author> fetchBooks(List<Author> authors) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, authors.size()).forEach(index -> order.put(authors.get(index).getId(), index));
        List<Author> result = entityManager
            .createQuery("select author from Author author left join fetch author.books where author in :authors", Author.class)
            .setParameter(AUTHORS_PARAMETER, authors)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
