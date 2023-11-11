package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.UserRole;
import dev.corusoft.slurp.users.domain.UserRoleID;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, UserRoleID> {
}
