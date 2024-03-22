package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.domain.exceptions.*;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Log4j2
@Transactional
@Service
public class AuthServiceImpl implements AuthService {
    /* DEPENDENCIES */
    private final UserRepository userRepo;
    private final CredentialRepository credentialRepo;
    private final ContactInfoRepository contactInfoRepo;
    private final AuthUtils authUtils;

    public AuthServiceImpl(UserRepository userRepo,
                           CredentialRepository credentialRepo,
                           ContactInfoRepository contactInfoRepo,
                           AuthUtils authUtils) {
        this.userRepo = userRepo;
        this.credentialRepo = credentialRepo;
        this.contactInfoRepo = contactInfoRepo;
        this.authUtils = authUtils;
    }


    /* USE CASES */
    @Override
    public User register(RegisterUserParamsDTO paramsDTO) throws UserAlreadyExistsException {
        // Comprobar si existe un usuario con el mismo nickname
        if (credentialRepo.existsByNicknameIgnoreCase(paramsDTO.getNickname())) {
            throw new UserAlreadyExistsException(paramsDTO.getNickname());
        }

        // Crear usuario
        User user = User.builder()
                .name(paramsDTO.getName())
                .surname(paramsDTO.getSurname())
                .gender(paramsDTO.getGender())
                .birthDate(paramsDTO.getBirthDate())
                .isActive(true)
                .build();
        user = userRepo.save(user);
        createCredentialForUser(paramsDTO, user);
        createContactInfoForUser(paramsDTO, user);

        // Asignar datos por defecto
        user.setRegisteredAt(LocalDateTime.now());
        authUtils.assignRoleToUser(user, UserRoles.BASIC);
        user = userRepo.save(user);

        log.info("Registered new user {}", paramsDTO.getNickname());
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public User login(String nickname, String rawPassword) throws IncorrectLoginException {
        // Comprobar si existe el usuario
        User user;
        try {
            user = authUtils.fetchUserByNickname(nickname);
        } catch (UserNotFoundException e) {
            throw new IncorrectLoginException();
        } catch (UserIsDeactivatedException e) {
            // Si usuario estaba desactivado, lo marca como activo
            user = credentialRepo.findByNicknameIgnoreCase(nickname)
                    .get()
                    .getUser();
            user = reactivateUser(user);
        }

        // Comprobar si credenciales coinciden
        Credential userCredentials = user.getCredential();
        if (!authUtils.doPasswordsMatch(rawPassword, userCredentials.getPasswordEncrypted())) {
            throw new IncorrectLoginException();
        }
        log.info("User {} authenticated using credentials", user.getUserID());


        return user;
    }

    @Override
    public User loginViaJWT(UUID userID) throws UserNotFoundException {
        User user = null;
        try {
        // Buscar al usuario
            user = authUtils.fetchUserByID(userID);
        } catch (UserIsDeactivatedException e) {
            // Si usuario estaba desactivado, lo marca como activo
            user = authUtils.findUserByID(userID);
            user = reactivateUser(user);
        }

        log.info("User {} authenticated using Json Web Token", userID);
        return user;
    }

    @Override
    public void changePassword(UUID userID, String oldPassword, String newPassword)
            throws UserNotFoundException, PasswordsDoNotMatchException {
        // Obtener al usurio y sus credenciales
        Credential credential = authUtils.findUserCredential(userID);

        // Comprobar que contraseñas coinciden
        String currentPassword = credential.getPasswordEncrypted();
        if (!authUtils.doPasswordsMatch(oldPassword, currentPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        // Cifrar y actualizar contraseña
        String encodedNewPassword = authUtils.encryptPassword(newPassword);
        credential.setPasswordEncrypted(encodedNewPassword);

        log.info("User {} has changed his password", credential.getNickname());
        credentialRepo.save(credential);
    }


    /* HELPER FUNCTIONS */
    private User createContactInfoForUser(RegisterUserParamsDTO paramsDTO, User user) {
        ContactInfo contactInfo = ContactInfo.builder()
                .email(paramsDTO.getEmail().toLowerCase())
                .phoneNumber(paramsDTO.getPhoneNumber())
                .user(user)
                .build();
        contactInfoRepo.save(contactInfo);
        user.attachContactInfo(contactInfo);

        return user;
    }

    private User createCredentialForUser(RegisterUserParamsDTO paramsDTO, User user) {
        String encryptedPassword = authUtils.encryptPassword(paramsDTO.getRawPassword());
        Credential credentials = Credential.builder()
                .nickname(paramsDTO.getNickname())
                .passwordEncrypted(encryptedPassword)
                .user(user)
                .build();
        credentialRepo.save(credentials);
        user.attachCredential(credentials);

        return user;
    }

    private User reactivateUser(User user) {
        if (user.getIsActive()) return user;

        // Si usuario estaba desactivado, lo marca como activo
        user.markAsActive();
        log.info("User with ID {} was deactivated, but now is active again after login", user.getUserID());

        return userRepo.save(user);
    }
}
