package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.service.dto.BookCopyDTO;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.dto.PublisherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BookCopy} and its DTO {@link BookCopyDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookCopyMapper extends EntityMapper<BookCopyDTO, BookCopy> {
    @Mapping(target = "publisher", source = "publisher", qualifiedByName = "publisherId")
    @Mapping(target = "book", source = "book", qualifiedByName = "bookId")
    BookCopyDTO toDto(BookCopy s);

    @Named("publisherId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublisherDTO toDtoPublisherId(Publisher publisher);

    @Named("bookId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookDTO toDtoBookId(Book book);
}
