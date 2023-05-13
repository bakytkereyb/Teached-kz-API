package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.Task;
import diplomka.diplomkaapiapp.entities.course.TaskFiles;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.course.TaskFilesService;
import diplomka.diplomkaapiapp.services.course.TaskService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/task-files")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Task Files", description = "APIs for operation on Task Files")
public class TaskFilesController {
    private final TaskFilesService taskFilesService;
    private final TaskService taskService;
    private final UserService userService;

    @Operation(
            summary = "Get Task Files",
            description = "Get Task Files")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskFiles.class))})
    })
    @GetMapping("/get/task/{taskId}/student/{studentId}")
    public ResponseEntity getTaskFilesByTaskAndStudent(@PathVariable UUID taskId,
                                                       @PathVariable UUID studentId) {
        try {
            User student = userService.getUserById(studentId);
            if (student == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
            Task task = taskService.getTaskById(taskId);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
            TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task, student);
            if (taskFiles == null) {
                return ResponseEntity.ok(new TaskFiles());
            }
            return ResponseEntity.ok(taskFiles);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
