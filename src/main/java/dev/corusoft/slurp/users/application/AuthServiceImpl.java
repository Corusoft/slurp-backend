package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.ContactInfo;
import dev.corusoft.slurp.users.domain.Credential;
import dev.corusoft.slurp.users.domain.User;
import dev.corusoft.slurp.users.domain.UserRoles;
import dev.corusoft.slurp.users.domain.exceptions.IncorrectLoginException;
import dev.corusoft.slurp.users.domain.exceptions.IncorrectPasswordException;
import dev.corusoft.slurp.users.domain.exceptions.UserAlreadyExistsException;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.ContactInfoRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.CredentialRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
                .build();
        user = userRepo.save(user);
        createCredentialForUser(paramsDTO, user);
        createContactInfoForUser(paramsDTO, user);

        // Asignar datos por defecto
        user.setRegisteredAt(LocalDateTime.now());
        authUtils.assignRoleToUser(user, UserRoles.BASIC);
        user = userRepo.save(user);

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
        }

        // Comprobar si credenciales coinciden
        Credential userCredentials = user.getCredential();
        if (!authUtils.doPasswordsMatch(rawPassword, userCredentials.getPasswordEncrypted())) {
            throw new IncorrectLoginException();
        }

        return user;
    }

    @Override
    public void changePassword(UUID userID, String oldPassword, String newPassword)
            throws UserNotFoundException, IncorrectPasswordException {
        // Obtener al usurio y sus credenciales
        Credential credential = authUtils.findUserCredential(userID);

        // Comprobar que contraseñas coinciden
        String currentPassword = credential.getPasswordEncrypted();
        if (!authUtils.doPasswordsMatch(currentPassword, oldPassword)) {
            throw new IncorrectPasswordException();
        }

        // Cifrar y actualizar contraseña
        String encodedNewPassword = authUtils.encryptPassword(newPassword);
        credential.setPasswordEncrypted(encodedNewPassword);
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
        Credential credentials = Credential.builder()
                .nickname(paramsDTO.getNickname())
                .passwordEncrypted(authUtils.encryptPassword(paramsDTO.getRawPassword()))
                .user(user)
                .build();
        credentialRepo.save(credentials);
        user.attachCredential(credentials);

        return user;
    }

}
