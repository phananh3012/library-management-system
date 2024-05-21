package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.dto.BookDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Author} and its DTO {@link AuthorDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapper<AuthorDTO, Author> {
    @Mapping(target = "books", source = "books", qualifiedByName = "bookIdSet")
    AuthorDTO toDto(Author s);

    @Mapping(target = "removeBook", ignore = true)
    Author toEntity(AuthorDTO authorDTO);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);

    @Named("bookIdSet")
    default Set<BookDTO> toDtoBookIdSet(Set<Book> book) {
        return book.stream().map(this::toDtoBookId).collect(Collectors.toSet());
    }
}
