package diplomka.diplomkaapiapp.repositories.course;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID>, PagingAndSortingRepository<CourseSection, UUID> {
}
