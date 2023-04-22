package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
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

    @GetMapping("/get")
    public ResponseEntity getAllCourses(@RequestParam("skip") Integer skip,
                                        @RequestParam("limit") Integer limit) {
        try {
            List<Course> courses = courseService.getAllCourses(skip, limit);
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity saveCourse(@RequestParam("name") String name,
                                     @RequestParam("description") String description,
                                     @RequestParam("trainerUsername") String trainerUsername) {
        try {
            User trainer = userService.getUserByUsername(trainerUsername);
            if (trainer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("trainer not found");
            }
            if (!trainer.isTrainer()) {
                return ResponseEntity.badRequest().body("user is not trainer");
            }
            Course course = new Course(name, description, trainer);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
