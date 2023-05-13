package diplomka.diplomkaapiapp.services.course;

import diplomka.diplomkaapiapp.entities.course.Task;
import diplomka.diplomkaapiapp.entities.course.TaskFiles;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.course.TaskFilesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskFilesService {
    private final TaskFilesRepository taskFilesRepository;

    public TaskFiles saveTaskFiles(TaskFiles taskFiles) {
        return taskFilesRepository.save(taskFiles);
    }
    public TaskFiles getTaskFilesById(UUID id) {
        return taskFilesRepository.findById(id).orElse(null);
    }
    public TaskFiles getTaskFilesByTaskAndStudent(Task task, User student) {
        return taskFilesRepository.findByTaskAndStudent(task, student);
    }
}
