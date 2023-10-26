package dev.corusoft.slurp.users.application.utils;

import dev.corusoft.slurp.common.domain.exceptions.EntityNotFoundException;
import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class UserUtils {
    private static final String USER_CLASSNAME = User.class.getSimpleName();
    private static final String PASSWORD_SALT = BCrypt.gensalt();

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CredentialRepository credentialRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    public User assignRoleToUser(User user, UserRoles userRole) {
        Role targetRole;
        Optional<Role> optionalRole = roleRepo.findByName(userRole);
        if (optionalRole.isPresent()) {
            targetRole = optionalRole.get();
        } else {
            targetRole = new Role();
            targetRole.setName(userRole);
        }

        user = user.attachUserRole(targetRole);

        return user;
    }

}
