package dev.corusoft.slurp.users.application.utils;

import dev.corusoft.slurp.common.security.jwt.application.JwtGenerator;
import dev.corusoft.slurp.common.security.jwt.domain.JwtData;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.domain.exceptions.UserIsDeactivatedException;
import dev.corusoft.slurp.users.domain.exceptions.UserNotFoundException;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AuthUtils {
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final CredentialRepository credentialRepo;
    private final UserRepository userRepo;


    public AuthUtils(RoleRepository roleRepo,
                     UserRoleRepository userRoleRepo,
                     BCryptPasswordEncoder passwordEncoder,
                     JwtGenerator jwtGenerator,
                     CredentialRepository credentialRepo,
                     UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.credentialRepo = credentialRepo;
        this.userRepo = userRepo;
    }

    public String generateJWTFromUser(User user) {
        String nickname = user.getCredential().getNickname();
        List<String> roles = user.getAttachedRoles()
                .stream()
                .map(Enum::name)
                .toList();

        JwtData jwtData = JwtData.builder()
                .userID(user.getUserID())
                .nickname(nickname)
                .roles(roles)
                .build();

        return jwtGenerator.generateJWT(jwtData);
    }

    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean doPasswordsMatch(String rawPassword, String expectedPassword) {
        return passwordEncoder.matches(rawPassword, expectedPassword);
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

    public User fetchUserByNickname(String nickname) throws UserNotFoundException, UserIsDeactivatedException {
        // Comprobar si existen credenciales para el nickname recibido
        User user = credentialRepo.findByNicknameIgnoreCase(nickname)
                .orElseThrow(() -> new UserNotFoundException(nickname))
                .getUser();

        // Comprobar si usuario está activo
        if (!user.getIsActive()) {
            throw new UserIsDeactivatedException();
        }

        return user;
    }

    public User fetchUserByID(UUID userID) throws UserNotFoundException, UserIsDeactivatedException {
        // Comprobar si existe el usuario
        User user = userRepo.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID));

        // Comprobar si usuario está activo
        if (!user.getIsActive()) {
            throw new UserIsDeactivatedException();
        }

        return user;
    }

    public Credential findUserCredential(UUID userID) throws UserNotFoundException {
        return credentialRepo.findCredentialByUserID(userID)
                .orElseThrow(() -> new UserNotFoundException(userID));
    }

    public boolean doUsersMatch(UUID requestorUserID, UUID targetUserID) {
        return requestorUserID.equals(targetUserID);
    }
}
