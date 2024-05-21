package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Hold;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.service.dto.BookCopyDTO;
import com.mycompany.myapp.service.dto.HoldDTO;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Hold} and its DTO {@link HoldDTO}.
 */
@Mapper(componentModel = "spring")
public interface HoldMapper extends EntityMapper<HoldDTO, Hold> {
    @Mapping(target = "bookCopy", source = "bookCopy", qualifiedByName = "bookCopyId")
    @Mapping(target = "patronAccount", source = "patronAccount", qualifiedByName = "patronAccountId")
    HoldDTO toDto(Hold s);

    @Named("bookCopyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BookCopyDTO toDtoBookCopyId(BookCopy bookCopy);

    @Named("patronAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatronAccountDTO toDtoPatronAccountId(PatronAccount patronAccount);
}
