package dev.corusoft.slurp.users.application.utils;

import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.repositories.CredentialRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.RoleRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AuthUtils {
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final CredentialRepository credentialRepo;
    private final UserRepository userRepo;


    public AuthUtils(RoleRepository roleRepo,
                     UserRoleRepository userRoleRepo,
                     BCryptPasswordEncoder passwordEncoder,
                     CredentialRepository credentialRepo,
                     UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
        this.credentialRepo = credentialRepo;
        this.userRepo = userRepo;
    }


    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean doPasswordsMatch(String actualPassword, String expectedPassword) {
        return passwordEncoder.matches(actualPassword, expectedPassword);
    }


    public UserRole assignRoleToUser(User user, UserRoles roleName) {
        Role role = roleRepo.findByName(roleName).get();

        // Asignar rol a usuario
        UserRole userRole = UserRole.builder()
                .id(new UserRoleID(user.getUserID(), role.getRoleID()))
                .assignedAt(LocalDateTime.now())
                .build();
        userRole.setUser(user);
        user.getUserRoles().add(userRole);
        userRole.setRole(role);
        role.getUserRoles().add(userRole);

        return userRoleRepo.save(userRole);
    }

    public User fetchUserByNickname(String nickname) throws UserNotFoundException {
        // Comprobar si existen credenciales para el nickname recibido
        Optional<Credential> optionalCredential = credentialRepo.findByNicknameIgnoreCase(nickname);
        Credential credential = optionalCredential.orElseThrow(() -> new UserNotFoundException(nickname));

        return credential.getUser();
    }

    public boolean checkUserExists(UUID userID) {
        return userRepo.existsById(userID);
    }

    public User fetchUserByID(UUID userID) throws UserNotFoundException {
        // Comprobar si existen credenciales para el nickname recibido
        Optional<User> optionalUser = userRepo.findById(userID);

        return optionalUser
                .orElseThrow(() -> new UserNotFoundException(userID));
    }

    public Credential findUserCredential(UUID userID) throws UserNotFoundException {
        return credentialRepo.findCredentialByUserID(userID)
                .orElseThrow(() -> new UserNotFoundException(userID));
    }

    public boolean doUsersMatch(UUID requestorUserID, UUID targetUserID) {
        return requestorUserID.equals(targetUserID);
    }
}
