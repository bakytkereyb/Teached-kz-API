package diplomka.diplomkaapiapp.repositories.postCourse;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourse;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostCourseRepository extends JpaRepository<PostCourse, UUID>, PagingAndSortingRepository<PostCourse, UUID> {
    List<PostCourse> findAllByStudentsContaining(User student, Pageable pageable);
    List<PostCourse> findAllByStatus(CourseStatus status, Pageable pageable);
}
