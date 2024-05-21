package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.Category;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.CategoryDTO;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Book} and its DTO {@link BookDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Mapping(target = "patronAccounts", source = "patronAccounts", qualifiedByName = "patronAccountIdSet")
    @Mapping(target = "authors", source = "authors", qualifiedByName = "authorIdSet")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    BookDTO toDto(Book s);

    @Mapping(target = "removePatronAccount", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "removeAuthor", ignore = true)
    Book toEntity(BookDTO bookDTO);

    @Named("patronAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatronAccountDTO toDtoPatronAccountId(PatronAccount patronAccount);

    @Named("patronAccountIdSet")
    default Set<PatronAccountDTO> toDtoPatronAccountIdSet(Set<PatronAccount> patronAccount) {
        return patronAccount.stream().map(this::toDtoPatronAccountId).collect(Collectors.toSet());
    }

    @Named("authorId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AuthorDTO toDtoAuthorId(Author author);

    @Named("authorIdSet")
    default Set<AuthorDTO> toDtoAuthorIdSet(Set<Author> author) {
        return author.stream().map(this::toDtoAuthorId).collect(Collectors.toSet());
    }

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);
}
