package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.course.CourseStatus;
import diplomka.diplomkaapiapp.entities.course.CourseUserStatus;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.services.course.CourseSectionService;
import diplomka.diplomkaapiapp.services.course.CourseService;
import diplomka.diplomkaapiapp.services.course.DeleteCourseService;
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
    private final CourseSectionService courseSectionService;
    private final DeleteCourseService deleteCourseService;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/get/{id}")
    @Operation(
            summary = "Get Course by id",
            description = "Get Course by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Course.class))})
    })
    public ResponseEntity getCourseById(@PathVariable UUID id,
                                        @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }

            if (course.getStudents().stream().anyMatch(student -> student.getId().equals(user.getId()))) {
                if (course.getClosedStudents().stream().anyMatch(closedStudent -> closedStudent.getId().equals(user.getId()))) {
                    course.setUserStatus(CourseUserStatus.FINISHED);
                } else {
                    course.setUserStatus(CourseUserStatus.IN_PROGRESS);
                }
                int[] progress = {0};
                course.getSections().forEach(courseSection -> {
                    courseSection.setClosed(
                            courseSection.getClosedStudents().stream()
                                    .anyMatch(closedStudent -> closedStudent.getId().equals(user.getId()))
                    );
                    if (courseSection.isClosed()) {
                        progress[0]++;
                    }
                });
                course.setProgress(progress[0]);

                if (course.getProgress().equals(course.maxProgress())) {
                    course.getCertificateSection().setClosed(true);
                }
            } else {
                course.setUserStatus(CourseUserStatus.FORBIDDEN);
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

    @PostMapping("/{id}/add/section")
    public ResponseEntity addSection(@RequestParam("name") String name,
                                     @PathVariable UUID id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            CourseSection section = new CourseSection(name);
            section = courseSectionService.saveCourseSection(section);
            course.addSection(section);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete/section/{sectionId}")
    public ResponseEntity deleteSection(@PathVariable UUID id, @PathVariable UUID sectionId) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            course.removeSectionById(sectionId);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
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

    @PostMapping("/{id}/register")
    public ResponseEntity registerUserToCourse(@PathVariable UUID id,
                                               @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }

            course.addStudent(user);
            course = courseService.saveCourse(course);

            return ResponseEntity.ok(new String("user is registered to course"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping ("/get/my")
    public ResponseEntity getAllMyCourses(@RequestHeader(value="Authorization") String token,
                                          @RequestParam("page") Integer page,
                                          @RequestParam("limit") Integer limit
                                          ) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            List<Course> courses = courseService.getAllCoursesByStudent(user, page, limit);
            List<Course> nextCourses = courseService.getAllCoursesByStudent(user, page + 1, limit);

            ListPagination<Course> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

            listPagination.getList().forEach(course -> {
                if (course.getClosedStudents().stream().anyMatch(closedStudent -> closedStudent.getId().equals(user.getId()))) {
                    course.setUserStatus(CourseUserStatus.FINISHED);
                } else {
                    course.setUserStatus(CourseUserStatus.IN_PROGRESS);
                }
                int[] progress = {0};
                course.getSections().forEach(courseSection -> {
                    courseSection.setClosed(
                            courseSection.getClosedStudents().stream()
                                    .anyMatch(closedStudent -> closedStudent.getId().equals(user.getId()))
                    );
                    if (courseSection.isClosed()) {
                        progress[0]++;
                    }
                });
                course.setProgress(progress[0]);

                if (course.getProgress().equals(course.maxProgress())) {
                    course.getCertificateSection().setClosed(true);
                }
            });

            return ResponseEntity.ok(listPagination);
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
            CourseSection courseSection = new CourseSection("Certificate section");
            courseSection = courseSectionService.saveCourseSection(courseSection);
            Course course = new Course(name, description, nameKz, descriptionKz, nameRu, descriptionRu, trainer, courseSection);
            course = courseService.saveCourse(course);
            return ResponseEntity.ok(course);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
