package dev.corusoft.slurp.users.infrastructure.dto.conversors;

import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.infrastructure.dto.output.*;
import lombok.experimental.UtilityClass;

import static dev.corusoft.slurp.users.infrastructure.dto.conversors.ContactInfoConversor.toContactInfoDTO;

@UtilityClass
public class UserConversor {
    /* ******************** Convertir a DTO ******************** */
    public UserDTO toUserDTO(User entity) {
        ContactInfoDTO contactInfoDTO = toContactInfoDTO(entity.getContactInfo());

        return UserDTO.builder()
                .userID(entity.getUserID())
                .name(entity.getName())
                .surname(entity.getSurname())
                .gender(entity.getGender())
                .nickname(entity.getCredential().getNickname())
                .birthDate(entity.getBirthDate())
                .registeredAt(entity.getRegisteredAt())
                .roles(entity.getAttachedRoles())
                .contactInfo(contactInfoDTO)
                .build();
    }

    public AuthenticatedUserDTO toAuthenticatedUserDTO(String token, User entity) {
        UserDTO userDTO = toUserDTO(entity);

        return new AuthenticatedUserDTO(token, userDTO);
    }

    /* ******************** Convertir a conjunto de DTO ******************** */


    /* ******************** Convertir a Entidad ******************** */


    /* ******************** Convertir a conjunto de Entidad ******************** */

}
