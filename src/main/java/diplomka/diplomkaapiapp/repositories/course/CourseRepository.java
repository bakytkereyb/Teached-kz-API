package diplomka.diplomkaapiapp.repositories.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
