package diplomka.diplomkaapiapp.repositories.postCourse;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PostCourseSectionRepository extends JpaRepository<PostCourseSection, UUID>, PagingAndSortingRepository<PostCourseSection, UUID> {
}
