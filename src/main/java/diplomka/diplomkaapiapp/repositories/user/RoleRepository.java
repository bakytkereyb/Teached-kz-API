package diplomka.diplomkaapiapp.repositories.user;

import diplomka.diplomkaapiapp.entities.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, UUID>, JpaRepository<Role, UUID> {
    Role findByRoleName(String roleName);
}
