package dev.corusoft.slurp.utils;

import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.dto.input.RegisterUserParamsDTO;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static dev.corusoft.slurp.TestConstants.*;

@Component
public class AuthTestUtils {

    /* ************************* DEPENDENCIAS ************************* */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;


    public AuthTestUtils(BCryptPasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    /* ************************* MÉTODOS AUXILIARES ************************* */

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

        Credential credential = generateValidCredentialForUser(user);
        user.attachCredential(credential);
        ContactInfo contactInfo = generateValidContactInfoForUser(user);
        user.attachContactInfo(contactInfo);

        return user;
    }

    public Credential generateValidCredentialForUser(User user) {
        String encryptedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        Credential credential = Credential.builder()
                .nickname("%s_%s".formatted(user.getName(), user.getSurname()))
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
        User user = generateValidUser();
        Credential credential = generateValidCredentialForUser(user);
        user.attachCredential(credential);
        ContactInfo contactInfo = generateValidContactInfoForUser(user);
        user.attachContactInfo(contactInfo);

        return userRepo.save(user);
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
