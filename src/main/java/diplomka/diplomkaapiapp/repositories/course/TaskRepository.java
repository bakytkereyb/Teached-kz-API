package diplomka.diplomkaapiapp.repositories.course;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, PagingAndSortingRepository<Task, UUID> {
}
