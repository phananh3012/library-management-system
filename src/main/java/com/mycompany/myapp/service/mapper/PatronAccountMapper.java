package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PatronAccount} and its DTO {@link PatronAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface PatronAccountMapper extends EntityMapper<PatronAccountDTO, PatronAccount> {
    @Mapping(target = "books", source = "books", qualifiedByName = "bookIdSet")
    PatronAccountDTO toDto(PatronAccount s);

    @Mapping(target = "books", ignore = true)
    @Mapping(target = "removeBook", ignore = true)
    PatronAccount toEntity(PatronAccountDTO patronAccountDTO);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);

    @Named("bookIdSet")
    default Set<BookDTO> toDtoBookIdSet(Set<Book> book) {
        return book.stream().map(this::toDtoBookId).collect(Collectors.toSet());
    }
}
