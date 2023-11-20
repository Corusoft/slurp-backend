package dev.corusoft.slurp.users.infrastructure.dto.conversors;

import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.output.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static dev.corusoft.slurp.users.infrastructure.dto.conversors.ContactInfoConversor.toContactInfoDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConversor {
    /* ******************** Convertir a DTO ******************** */
    public static UserDTO toUserDTO(User entity) {
        ContactInfoDTO contactInfoDTO = toContactInfoDTO(entity.getContactInfo());

        return UserDTO.builder()
                .userID(entity.getUserID())
                .name(entity.getName())
                .surname(entity.getSurname())
                .gender(entity.getGender())
                .nickname(entity.getCredential().getNickname())
                .birthDate(entity.getBirthDate())
                .registeredAt(entity.getRegisteredAt())
                .isActive(entity.getIsActive())
                .unactiveSince(entity.getUnactiveSince())
                // Otras entidades
                .roles(entity.getAttachedRoles())
                .contactInfo(contactInfoDTO)
                .build();
    }

    public static AuthenticatedUserDTO toAuthenticatedUserDTO(String token, User entity) {
        UserDTO userDTO = toUserDTO(entity);

        return new AuthenticatedUserDTO(token, userDTO);
    }

    /* ******************** Convertir a conjunto de DTO ******************** */


    /* ******************** Convertir a Entidad ******************** */


    /* ******************** Convertir a conjunto de Entidad ******************** */

}
