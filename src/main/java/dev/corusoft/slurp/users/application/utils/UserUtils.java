package dev.corusoft.slurp.users.application.utils;

import dev.corusoft.slurp.common.exception.EntityNotFoundException;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class UserUtils {
    private static final String USER_CLASSNAME = User.class.getSimpleName();

    private final UserRepository userRepo;
    private final CredentialRepository credentialRepo;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserUtils(UserRepository userRepo,
                     CredentialRepository credentialRepo,
                     RoleRepository roleRepo,
                     UserRoleRepository userRoleRepo,
                     BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.credentialRepo = credentialRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
    }


    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Busca un usuario por su nickname.
     *
     * @param nickname Nombre de usuario a buscar
     * @return Usuario encontrado
     * @throws EntityNotFoundException No se encuentra al usuario
     */
    public User fetchUserByNickname(String nickname) throws EntityNotFoundException {
        Optional<User> optionalUser = Optional.empty();

        // Comprobar si existe el usuario con el ID recibido
        boolean hasExistentCredentials = credentialRepo.existsByNicknameIgnoreCase(nickname);
        if (hasExistentCredentials) {
            optionalUser = userRepo.findByNickname(nickname);
        }
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException(USER_CLASSNAME, nickname);
        }

        return optionalUser.get();
    }

    public UserRole assignRoleToUser(User user, UserRoles roleName) {
        // Buscar el rol. Si no existe, crearlo.
        Role role;
        Optional<Role> optionalRole = roleRepo.findByName(roleName);
        if (optionalRole.isPresent()) {
            role = optionalRole.get();
        } else {
            role = new Role();
            role.setName(roleName);
        }

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

}
