package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.service.dto.NotificationDTO;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "patronAccount", source = "patronAccount", qualifiedByName = "patronAccountId")
    NotificationDTO toDto(Notification s);

    @Named("patronAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PatronAccountDTO toDtoPatronAccountId(PatronAccount patronAccount);
}
