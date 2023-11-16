package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.ContactInfo;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.dto.input.UpdateContactInfoParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.ContactInfoRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Transactional
@Service
public class UserServiceImpl implements UserService {
    /* DEPENDENCIES */
    private final UserRepository userRepo;
    private final ContactInfoRepository contactInfoRepo;
    private final AuthUtils authUtils;

    public UserServiceImpl(UserRepository userRepo, ContactInfoRepository contactInfoRepo, AuthUtils authUtils) {
        this.userRepo = userRepo;
        this.contactInfoRepo = contactInfoRepo;
        this.authUtils = authUtils;
    }

    /* USE CASES */

    @Override
    public User updateContactInfo(UUID userID, UpdateContactInfoParamsDTO paramsDTO) throws UserNotFoundException {
        // Obtener al usuario y sus datos de contacto
        User user = authUtils.fetchUserByID(userID);
        ContactInfo contactInfo = user.getContactInfo();

        boolean hasChangedContact = false;
        // Comprobar si los campos han cambiado para actualizarlos
        if (hasChanged(contactInfo.getEmail(), paramsDTO.getNewEmail())) {
            // TODO Validar y confirmar email
            contactInfo.setEmail(paramsDTO.getNewEmail());
            hasChangedContact = true;
        }
        if (hasChanged(contactInfo.getPhoneNumber(), paramsDTO.getNewMobilePhone())) {
            // TODO validar y confirmar teléfono
            contactInfo.setPhoneNumber(paramsDTO.getNewMobilePhone());
            hasChangedContact = true;
        }

        if (hasChangedContact) {
            log.info("User {} has updated his contact info", userID);
            contactInfoRepo.save(contactInfo);
        }

        return user;
    }

    /* HELPER FUNCTIONS */
    private <T extends Object> boolean hasChanged(T expected, T received) {
        return !expected.equals(received);
    }
}
