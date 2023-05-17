package diplomka.diplomkaapiapp.repositories.user;

import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID>, JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
//    List<User> findAllByRolesContaining(Role role, Pageable pageable);
    List<User> findAllByRolesContaining(Role role);
    List<User> findAllByFirstNameContainingIgnoreCaseOrSecondNameContainingIgnoreCaseOrMiddleNameContainsIgnoreCase(String firstName, String secondName, String middleName, Pageable pageable);

    long countByRolesContaining(Role role);
//    List<User> findAll(Pageable pageable);
}
