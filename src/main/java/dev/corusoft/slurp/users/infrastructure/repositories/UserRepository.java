package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends ListCrudRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE LOWER(u.credential.nickname) = LOWER(:nickname)")
    Optional<User> findByNickname(String nickname);

}
