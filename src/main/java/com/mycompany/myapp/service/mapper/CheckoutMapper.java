package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Checkout;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.service.dto.BookCopyDTO;
import com.mycompany.myapp.service.dto.CheckoutDTO;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Checkout} and its DTO {@link CheckoutDTO}.
 */
@Mapper(componentModel = "spring")
public interface CheckoutMapper extends EntityMapper<CheckoutDTO, Checkout> {
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyId")
    @Mapping(target = "patronAccount", source = "patronAccount", qualifiedByName = "patronAccountId")
    CheckoutDTO toDto(Checkout s);

    @Named("bookCopyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookCopyDTO toDtoBookCopyId(BookCopy bookCopy);

    @Named("patronAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatronAccountDTO toDtoPatronAccountId(PatronAccount patronAccount);
}
