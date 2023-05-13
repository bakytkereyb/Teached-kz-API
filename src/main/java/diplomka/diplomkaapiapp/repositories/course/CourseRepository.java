package diplomka.diplomkaapiapp.repositories.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, PagingAndSortingRepository<Course, UUID> {
//    List<Course> findAll(Pageable pageable);
    Optional<Course> findById(UUID id);
    List<Course> findAllByStatus(CourseStatus courseStatus, Pageable pageable);
    List<Course> findAllByStudentsContaining(User student, Pageable pageable);
    List<Course> findAllByTrainerOrderByNameAsc(User trainer, Pageable pageable);
}
