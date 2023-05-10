package diplomka.diplomkaapiapp.repositories.course;

import diplomka.diplomkaapiapp.entities.course.TaskFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TaskFilesRepository extends JpaRepository<TaskFiles, UUID>, PagingAndSortingRepository<TaskFiles, UUID> {
}
