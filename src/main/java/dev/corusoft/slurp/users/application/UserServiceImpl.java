package dev.corusoft.slurp.users.application;

import dev.corusoft.slurp.common.domain.exceptions.EntityAlreadyExistsException;
import dev.corusoft.slurp.users.application.utils.UserUtils;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.domain.User.UserBuilder;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final String USER_CLASSNAME = User.class.getSimpleName();

    /* DEPENDENCIES */
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CredentialRepository credentialRepo;
    @Autowired
    private ContactInfoRepository contactInfoRepo;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    /* USE CASES */
    @Override
    public User signUp(RegisterUserParamsDTO paramsDTO) throws EntityAlreadyExistsException {
        // Comprobar si existe un usuario con el mismo nickname
        if (credentialRepo.existsByNicknameIgnoreCase(paramsDTO.getNickname())) {
            throw new EntityAlreadyExistsException(USER_CLASSNAME, paramsDTO.getNickname());
        }

        // Crear usuario
        User user;
        UserBuilder userBuilder = User.builder()
                .name(paramsDTO.getName())
                .surname(paramsDTO.getSurname())
                .gender(paramsDTO.getGender())
                .birthDate(paramsDTO.getBirthDate());

        Credential credentials = Credential.builder()
                .nickname(paramsDTO.getNickname())
                .passwordEncrypted(passwordEncoder.encode(paramsDTO.getRawPassword()))
                .build();
        credentials = credentialRepo.save(credentials);

        ContactInfo contactInfo = ContactInfo.builder()
                .email(paramsDTO.getEmail())
                .phoneNumber(paramsDTO.getPhoneNumber())
                .build();
        contactInfo = contactInfoRepo.save(contactInfo);


        // Asignar datos por defecto
        user = userBuilder.registeredAt(LocalDateTime.now()).build();
        user = userUtils.assignRoleToUser(user, UserRoles.BASIC);
        user.attachCredential(credentials);
        user.attachContactInfo(contactInfo);

        return userRepo.save(user);
    }


    /* HELPER FUNCTIONS */

}
