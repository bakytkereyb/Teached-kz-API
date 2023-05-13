package diplomka.diplomkaapiapp.controllers.postCourse;

import diplomka.diplomkaapiapp.entities.course.*;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourse;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourseSection;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.services.file.FileService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.postCourse.DeletePostCourseService;
import diplomka.diplomkaapiapp.services.postCourse.PostCourseSectionService;
import diplomka.diplomkaapiapp.services.postCourse.PostCourseService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/post-course")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Post Course", description = "APIs for operation on Post Course")
public class PostCourseController {
    private final UserService userService;
    private final JwtService jwtService;
    private final FileService fileService;
    private final PostCourseService postCourseService;
    private final PostCourseSectionService postCourseSectionService;
    private final DeletePostCourseService deletePostCourseService;

    @PostMapping("/save")
    public ResponseEntity savePostCourse(@RequestParam("name") String name,
                                     @RequestParam("nameKz") String nameKz,
                                     @RequestParam("nameRu") String nameRu,
                                     @RequestParam("description") String description,
                                     @RequestParam("descriptionKz") String descriptionKz,
                                     @RequestParam("descriptionRu") String descriptionRu) {
        try {
            PostCourse postCourse = new PostCourse(name, description, nameKz, descriptionKz, nameRu, descriptionRu);
            postCourse = postCourseService.savePostCourse(postCourse);
            return ResponseEntity.ok(postCourse);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getPostCourseById(@PathVariable UUID id,
                                            @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }

            if (!postCourse.getStudents().stream().anyMatch(student -> student.getId().equals(user.getId()))) {
                postCourse.setUserStatus(CourseUserStatus.FORBIDDEN);
            }

            return ResponseEntity.ok(postCourse);
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
                List<PostCourse> courses = postCourseService.getAllPostCoursesByStatus(page, limit, status);
                List<PostCourse> nextCourses = postCourseService.getAllPostCoursesByStatus(page + 1, limit, status);

                ListPagination<PostCourse> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

                return ResponseEntity.ok(listPagination);
            }

            List<PostCourse> courses = postCourseService.getAllPostCourses(page, limit);
            List<PostCourse> nextCourses = postCourseService.getAllPostCourses(page + 1, limit);

            ListPagination<PostCourse> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

            return ResponseEntity.ok(listPagination);
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
            List<PostCourse> courses = postCourseService.getAllPostCoursesByStudent(user, page, limit);
            List<PostCourse> nextCourses = postCourseService.getAllPostCoursesByStudent(user, page + 1, limit);

            ListPagination<PostCourse> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status/public")
    public ResponseEntity changePostCourseStatusToPublic(@PathVariable UUID id) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            postCourse.setStatus(CourseStatus.PUBLIC);
            postCourse = postCourseService.savePostCourse(postCourse);
            return ResponseEntity.ok(postCourse);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status/private")
    public ResponseEntity changePostCourseStatusToPrivate(@PathVariable UUID id) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            postCourse.setStatus(CourseStatus.PRIVATE);
            postCourse = postCourseService.savePostCourse(postCourse);
            return ResponseEntity.ok(postCourse);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/register")
    public ResponseEntity registerUserToPostCourse(@PathVariable UUID id,
                                                   @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }

            postCourse.addStudent(user);
            postCourse = postCourseService.savePostCourse(postCourse);

            return ResponseEntity.ok(new String("user is registered to post-course"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePostCourseById(@PathVariable UUID id) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            if (postCourse.getStatus() == CourseStatus.PUBLIC) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("cannot delete public post-course");
            }
            deletePostCourseService.deletePostCourse(postCourse);
            return ResponseEntity.ok(new String("post-course deleted"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/get/students")
    public ResponseEntity getStudentOfPostCourse(@PathVariable UUID id,
                                                @RequestParam("page") Integer page,
                                                @RequestParam("limit") Integer limit) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            Pageable pageable = PageRequest.of(page, limit);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), postCourse.getStudents().size());
            if (start <= end) {
                List<User> students = postCourse.getStudents().subList(start, end);

                pageable = PageRequest.of(page + 1, limit);
                start = (int)pageable.getOffset();
                end = Math.min((start + pageable.getPageSize()), postCourse.getStudents().size());

                List<User> nextStudents = new ArrayList<>();
                if (start <= end) {
                    nextStudents = postCourse.getStudents().subList(start, end);
                }


                ListPagination<User> listPagination = new ListPagination<>(students, !nextStudents.isEmpty());
                return ResponseEntity.ok(listPagination);
            }
            return ResponseEntity.ok(new ListPagination<>(new ArrayList<>(), false));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/add/section")
    public ResponseEntity addSection(@RequestParam("name") String name,
                                     @PathVariable UUID id) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            PostCourseSection section = new PostCourseSection(name);
            section = postCourseSectionService.savePostCourseSection(section);
            postCourse.addSection(section);
            postCourse = postCourseService.savePostCourse(postCourse);
            return ResponseEntity.ok(postCourse);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete/section/{sectionId}")
    public ResponseEntity deleteSection(@PathVariable UUID id, @PathVariable UUID sectionId) {
        try {
            PostCourse postCourse = postCourseService.getPostCourseById(id);
            if (postCourse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post-course not found");
            }
            postCourse.removeSectionById(sectionId);
            postCourse = postCourseService.savePostCourse(postCourse);
            return ResponseEntity.ok(postCourse);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
