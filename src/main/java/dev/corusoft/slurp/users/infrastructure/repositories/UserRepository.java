package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface UserRepository extends ListCrudRepository<User, UUID> {
}
