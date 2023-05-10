package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.Task;
import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.services.course.CourseSectionService;
import diplomka.diplomkaapiapp.services.course.TaskFilesService;
import diplomka.diplomkaapiapp.services.course.TaskService;
import diplomka.diplomkaapiapp.services.file.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course/section")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Course section", description = "APIs for operation on Course section")
public class CourseSectionController {
    private final CourseSectionService courseSectionService;
    private final TaskService taskService;
    private final TaskFilesService taskFilesService;
    private final FileService fileService;

    @PostMapping("/{id}/add/file")
    public ResponseEntity addFileToSection(@PathVariable UUID id,
                                           @RequestParam("label") String label,
                                           @RequestParam("fileName") String fileName) {
        try {
            CourseSection courseSection = courseSectionService.getCourseSectionById(id);
            if (courseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Section not found");
            }
            File file = new File(fileName, label);
            file = fileService.saveFile(file);
            courseSection.addFile(file);
            courseSection = courseSectionService.saveCourseSection(courseSection);
            return ResponseEntity.ok(new String("file added"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/add/task")
    public ResponseEntity addTaskToSection(@PathVariable UUID id,
                                           @RequestParam("name") String name,
                                           @RequestParam("description") String description,
                                           @RequestParam("deadline") String deadline) {
        try {
            CourseSection courseSection = courseSectionService.getCourseSectionById(id);
            if (courseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Section not found");
            }
            Instant instant = Instant.parse( deadline );
            Task task = new Task(
                    LocalDateTime.ofInstant(instant, ZoneOffset.UTC),
                    name,
                    description);

            task = taskService.saveTask(task);
            log.warn(task.toString());
            courseSection.addTask(task);
            courseSection = courseSectionService.saveCourseSection(courseSection);
            return ResponseEntity.ok(new String("task added"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/remove/task/{taskId}")
    public ResponseEntity addTaskToSection(@PathVariable UUID id,
                                           @PathVariable UUID taskId) {
        try {
            CourseSection courseSection = courseSectionService.getCourseSectionById(id);
            if (courseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Section not found");
            }

            Task task = taskService.getTaskById(taskId);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }

            courseSection.setTasks(
                    courseSection.getTasks().stream()
                            .filter(sectionTask -> !sectionTask.getId().equals(task.getId()))
                            .collect(Collectors.toList())
            );
            courseSection = courseSectionService.saveCourseSection(courseSection);
            return ResponseEntity.ok(new String("task removed"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/remove/file/{fileId}")
    public ResponseEntity addFileToSection(@PathVariable UUID id,
                                           @PathVariable UUID fileId) {
        try {
            CourseSection courseSection = courseSectionService.getCourseSectionById(id);
            if (courseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Section not found");
            }
            File file = fileService.getFileById(fileId);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }
            courseSection.setFiles(
                    courseSection.getFiles().stream()
                            .filter(sectionFile -> !sectionFile.getId().equals(file.getId()))
                            .collect(Collectors.toList())
            );
            courseSection = courseSectionService.saveCourseSection(courseSection);
            return ResponseEntity.ok(new String("file removed"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
