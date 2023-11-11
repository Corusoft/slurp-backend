package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.Credential;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CredentialRepository extends ListCrudRepository<Credential, Long> {
    /**
     * Comprueba si existe un usuario por su nombre de usuario, ignorando maýusculas o minúsculas.
     *
     * @param nickname Nombre del usuario
     * @return True si existe un usuario con el nombre recibido.
     */
    boolean existsByNicknameIgnoreCase(String nickname);

    Optional<Credential> findByNicknameIgnoreCase(String nickname);


}
