package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.common.exception.EntityAlreadyExistsException;
import dev.corusoft.slurp.users.application.utils.UserUtils;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
public class AuthServiceImpl implements AuthService {
    private final String USER_CLASSNAME = User.class.getSimpleName();

    /* DEPENDENCIES */
    private final UserRepository userRepo;
    private final CredentialRepository credentialRepo;
    private final ContactInfoRepository contactInfoRepo;
    private final UserUtils userUtils;

    public AuthServiceImpl(UserRepository userRepo, CredentialRepository credentialRepo, ContactInfoRepository contactInfoRepo, UserUtils userUtils) {
        this.userRepo = userRepo;
        this.credentialRepo = credentialRepo;
        this.contactInfoRepo = contactInfoRepo;
        this.userUtils = userUtils;
    }


    /* USE CASES */
    @Override
    public User register(RegisterUserParamsDTO paramsDTO) throws EntityAlreadyExistsException {
        // Comprobar si existe un usuario con el mismo nickname
        if (credentialRepo.existsByNicknameIgnoreCase(paramsDTO.getNickname())) {
            throw new EntityAlreadyExistsException(USER_CLASSNAME, paramsDTO.getNickname());
        }

        // Crear usuario
        User user = User.builder()
                //.userID(UUID.randomUUID())
                .name(paramsDTO.getName())
                .surname(paramsDTO.getSurname())
                .gender(paramsDTO.getGender())
                .birthDate(paramsDTO.getBirthDate())
                .build();
        user = userRepo.save(user);

        Credential credentials = Credential.builder()
                .nickname(paramsDTO.getNickname())
                .passwordEncrypted(userUtils.encryptPassword(paramsDTO.getRawPassword()))
                .user(user)
                .build();
        credentialRepo.save(credentials);
        user.attachCredential(credentials);

        ContactInfo contactInfo = ContactInfo.builder()
                .email(paramsDTO.getEmail().toLowerCase())
                .phoneNumber(paramsDTO.getPhoneNumber())
                .user(user)
                .build();
        contactInfoRepo.save(contactInfo);
        user.attachContactInfo(contactInfo);

        // Asignar datos por defecto
        user.setRegisteredAt(LocalDateTime.now());
        userUtils.assignRoleToUser(user, UserRoles.BASIC);
        user = userRepo.save(user);

        return user;
    }


    /* HELPER FUNCTIONS */

}
