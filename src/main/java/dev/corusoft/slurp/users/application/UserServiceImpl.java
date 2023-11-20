package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.ContactInfo;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.exceptions.UserIsDeactivatedException;
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
    private final ContactInfoRepository contactInfoRepo;
    private final AuthUtils authUtils;
    private final UserRepository userRepo;

    public UserServiceImpl(ContactInfoRepository contactInfoRepo,
                           AuthUtils authUtils,
                           UserRepository userRepo) {
        this.contactInfoRepo = contactInfoRepo;
        this.authUtils = authUtils;
        this.userRepo = userRepo;
    }

    /* USE CASES */

    @Override
    public User findUserByID(UUID userID) throws UserNotFoundException, UserIsDeactivatedException {
        return authUtils.fetchUserByID(userID);
    }

    @Override
    public User updateContactInfo(UUID userID, UpdateContactInfoParamsDTO paramsDTO) throws UserNotFoundException, UserIsDeactivatedException {
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

    @Override
    public void deactivateUser(UUID userID) throws UserNotFoundException {
        // Obtener al usuario
        User user;
        try {
            user = authUtils.fetchUserByID(userID);
        } catch (UserIsDeactivatedException e) {
            // Si usuario ya estaba desactivado, no hace nada
            return;
        }

        // Marca usuario como desactivado
        user.markAsUnactive();
        log.info("User with ID {} was deactivated", userID);
        userRepo.save(user);
    }

    @Override
    public User reactivateUser(UUID userID) throws UserNotFoundException {
        // Obtener al usuario
        User user = userRepo.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
        if (user.getIsActive()) return user;

        // Marca usuario como activo
        user.markAsActive();
        log.info("User with ID {} was reactivated", userID);

        return userRepo.save(user);
    }


    /* HELPER FUNCTIONS */
    private <T extends Object> boolean hasChanged(T expected, T received) {
        return !expected.equals(received);
    }
}
