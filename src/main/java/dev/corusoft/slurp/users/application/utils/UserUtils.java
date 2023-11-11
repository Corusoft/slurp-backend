package dev.corusoft.slurp.users.application.utils;

import dev.corusoft.slurp.users.domain.*;
import dev.corusoft.slurp.users.infrastructure.repositories.RoleRepository;
import dev.corusoft.slurp.users.infrastructure.repositories.UserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Transactional(readOnly = true)
public class UserUtils {
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserUtils(RoleRepository roleRepo,
                     UserRoleRepository userRoleRepo,
                     BCryptPasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
    }


    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
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

}
