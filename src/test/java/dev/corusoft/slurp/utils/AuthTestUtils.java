package dev.corusoft.slurp.utils;

import dev.corusoft.slurp.users.application.utils.AuthUtils;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.dto.output.AuthenticatedUserDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static dev.corusoft.slurp.TestConstants.*;
import static dev.corusoft.slurp.users.infrastructure.dto.conversors.UserConversor.toAuthenticatedUserDTO;

@Component
public class AuthTestUtils {

    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AuthUtils authUtils;


    public AuthTestUtils(BCryptPasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    /* ************************* MÉTODOS AUXILIARES ************************* */
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User generateValidUser() {
        return this.generateValidUser(DEFAULT_NAME);
    }

    public User generateValidUser(String name) {
        String finalName = (name != null) ? name : DEFAULT_NAME;

        User user = User.builder()
                .name(finalName)
                .surname(DEFAULT_SURNAME)
                .gender(Gender.MALE)
                .birthDate(DEFAULT_BIRTHDATE)
                .registeredAt(LocalDateTime.now())
                .build();

        Credential credential = generateValidCredentialForUser();
        user.attachCredential(credential);
        ContactInfo contactInfo = generateValidContactInfoForUser(user);
        user.attachContactInfo(contactInfo);


        return user;
    }

    public Credential generateValidCredentialForUser() {
        return generateValidCredentialForUser(DEFAULT_NICKNAME, DEFAULT_PASSWORD);
    }

    public Credential generateValidCredentialForUser(String nickname, String password) {
        String finalNickname = (nickname != null) ? nickname : DEFAULT_NICKNAME;
        String finalPassword = (password != null) ? password : DEFAULT_PASSWORD;
        String encryptedPassword = passwordEncoder.encode(finalPassword);

        Credential credential = Credential.builder()
                .nickname(finalNickname)
                .passwordEncrypted(encryptedPassword)
                .build();

        return credential;
    }

    public ContactInfo generateValidContactInfoForUser(User user) {
        Credential credential = user.getCredential();

        ContactInfo contact = ContactInfo.builder()
                .email(credential.getNickname() + '@' + DEFAULT_EMAIL_DOMAIN)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .build();

        return contact;
    }

    public User registerValidUser() {
        return registerValidUser(DEFAULT_NICKNAME, DEFAULT_PASSWORD);
    }

    public User registerValidUser(String nickname, String password) {
        User user = generateValidUser();
        Credential credential = generateValidCredentialForUser(nickname, password);
        user.attachCredential(credential);
        ContactInfo contactInfo = generateValidContactInfoForUser(user);
        user.attachContactInfo(contactInfo);
        userRepo.save(user);
        authUtils.assignRoleToUser(user, UserRoles.BASIC);

        return userRepo.save(user);
    }

    public void removeRegisteredUser(User user) {
        userRepo.delete(user);
    }

    public AuthenticatedUserDTO generateAuthenticatedUser(User user) {
        String serviceToken = authUtils.generateJWTFromUser(user);

        return toAuthenticatedUserDTO(serviceToken, user);
    }

    public RegisterUserParamsDTO generateRegisterParamsDtoFromUser(User user) {
        Credential credential = user.getCredential();
        ContactInfo contact = user.getContactInfo();

        RegisterUserParamsDTO dto = RegisterUserParamsDTO.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .nickname(credential.getNickname())
                .rawPassword(DEFAULT_PASSWORD)
                .email(contact.getEmail())
                .phoneNumber(contact.getPhoneNumber())
                .build();

        return dto;
    }
}
