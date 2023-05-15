package diplomka.diplomkaapiapp.repositories.application;

import diplomka.diplomkaapiapp.entities.application.Application;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID>, PagingAndSortingRepository<Application, UUID> {
    List<Application> findAllByUser(User user, Pageable pageable);
}
