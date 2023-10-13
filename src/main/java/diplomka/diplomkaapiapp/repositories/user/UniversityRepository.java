package diplomka.diplomkaapiapp.repositories.user;

import diplomka.diplomkaapiapp.entities.user.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UniversityRepository extends PagingAndSortingRepository<University, UUID>, JpaRepository<University, UUID> {
    Optional<University> findByName(String name);
}
