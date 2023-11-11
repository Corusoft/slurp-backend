package dev.corusoft.slurp.users.infrastructure.repositories;

import dev.corusoft.slurp.users.domain.ContactInfo;
import org.springframework.data.repository.CrudRepository;

public interface ContactInfoRepository extends CrudRepository<ContactInfo, Long> {
}
