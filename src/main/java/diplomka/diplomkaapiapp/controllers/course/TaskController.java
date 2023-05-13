package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.Task;
import diplomka.diplomkaapiapp.entities.course.TaskFiles;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.TaskList;
import diplomka.diplomkaapiapp.services.course.CourseSectionService;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.course.DeleteCourseService;
import diplomka.diplomkaapiapp.services.course.TaskFilesService;
import diplomka.diplomkaapiapp.services.file.FileService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/task")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Task", description = "APIs for operation on Task")
public class TaskController {
    private final CourseService courseService;
    private final TaskFilesService taskFilesService;
    private final CourseSectionService courseSectionService;
    private final DeleteCourseService deleteCourseService;
    private final UserService userService;
    private final JwtService jwtService;
    private final FileService fileService;

    @GetMapping("/get")
    @Operation(
            summary = "Get Tasks",
            description = "Get Tasks")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class))})
    })
    public ResponseEntity getAllTasksByUser(@RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            List<Course> courses = courseService.getAllCoursesByStudent(user, 0, Integer.MAX_VALUE);
            List<TaskList> tasksList = new ArrayList<>();
            for (Course course : courses) {
                for (CourseSection section : course.getSections()) {
                    for (Task task : section.getTasks()) {
                        TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task, user);
                        task.setTaskFiles(taskFiles == null ? new TaskFiles() : taskFiles);
                        
                        TaskList taskList = new TaskList(task, course, section);
                        tasksList.add(taskList);
                    }
                }
            }

            return ResponseEntity.ok(tasksList);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
