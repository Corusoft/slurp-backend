package dev.corusoft.slurp.users.infrastructure.dto.conversors;

import dev.corusoft.slurp.users.domain.ContactInfo;
import dev.corusoft.slurp.users.infrastructure.dto.output.ContactInfoDTO;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactInfoConversor {
    /* ******************** Convertir a DTO ******************** */
    public ContactInfoDTO toContactInfoDTO(ContactInfo entity) {
        return ContactInfoDTO.builder()
                .contactInfoID(entity.getContactInfoID())
                .email(entity.getEmail())
                .isEmailVerified(entity.getIsEmailVerified())
                .phoneNumber(entity.getPhoneNumber())
                .isPhoneNumberVerified(entity.getIsPhoneNumberVerified())
                .build();
    }

}
