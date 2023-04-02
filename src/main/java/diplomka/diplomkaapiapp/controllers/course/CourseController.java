package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/get/{id}")
    public ResponseEntity getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.badRequest().body("course not found");
            }
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
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
                return ResponseEntity.badRequest().body("trainer not found");
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
