package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.course.DeleteCourseService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Course", description = "APIs for operation on Course")
public class CourseController {

    private final CourseService courseService;
    private final DeleteCourseService deleteCourseService;
    private final UserService userService;

    @GetMapping("/get/{id}")
    public ResponseEntity getCourseById(@PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status/public")
    public ResponseEntity changeCourseStatusToPublic(@PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            course.setStatus(CourseStatus.PUBLIC);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status/private")
    public ResponseEntity changeCourseStatusToPrivate(@PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            course.setStatus(CourseStatus.PRIVATE);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity getAllCourses(@RequestParam("page") Integer page,
                                        @RequestParam("limit") Integer limit,
                                        @RequestParam(value = "status", required = false) CourseStatus status) {
        try {
            if (status != null) {
                if (status != CourseStatus.PRIVATE && status != CourseStatus.PUBLIC) {
                    return ResponseEntity.badRequest().body("invalid course status");
                }
                List<Course> courses = courseService.getAllCoursesByStatus(page, limit, status);
                List<Course> nextCourses = courseService.getAllCoursesByStatus(page + 1, limit, status);

                ListPagination<Course> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

                return ResponseEntity.ok(listPagination);
            }
            List<Course> courses = courseService.getAllCourses(page, limit);
            List<Course> nextCourses = courseService.getAllCourses(page + 1, limit);

            ListPagination<Course> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteCourseById(@PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            if (course.getStatus() == CourseStatus.PUBLIC) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("cannot delete public course");
            }
            deleteCourseService.deleteCourse(course);
            return ResponseEntity.ok(new String("course deleted"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity saveCourse(@RequestParam("name") String name,
                                     @RequestParam("nameKz") String nameKz,
                                     @RequestParam("nameRu") String nameRu,
                                     @RequestParam("description") String description,
                                     @RequestParam("descriptionKz") String descriptionKz,
                                     @RequestParam("descriptionRu") String descriptionRu,
                                     @RequestParam("trainerUsername") String trainerUsername) {
        try {
            User trainer = userService.getUserByUsername(trainerUsername);
            if (trainer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("trainer not found");
            }
            if (!trainer.isTrainer()) {
                return ResponseEntity.badRequest().body("user is not trainer");
            }
            Course course = new Course(name, description, nameKz, descriptionKz, nameRu, descriptionRu, trainer);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
