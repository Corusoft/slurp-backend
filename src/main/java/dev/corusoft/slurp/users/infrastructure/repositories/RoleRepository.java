package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.Role;
import dev.corusoft.slurp.users.domain.UserRoles;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface RoleRepository extends ListCrudRepository<Role, Long> {
    Optional<Role> findByName(UserRoles name);
}
