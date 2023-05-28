package diplomka.diplomkaapiapp.controllers.course;

import diplomka.diplomkaapiapp.entities.course.*;
import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final TaskFilesService taskFilesService;
    private final CourseSectionService courseSectionService;
    private final DeleteCourseService deleteCourseService;
    private final UserService userService;
    private final JwtService jwtService;
    private final FileService fileService;

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
                    if (courseSection.isClosed() || courseSection.getTasks().isEmpty()) {
                        progress[0]++;
                    }
                    courseSection.getTasks().forEach(task -> {
                        TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task, user);
                        task.setTaskFiles(taskFiles);
                    });
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

    @GetMapping("/{id}/section/{sectionId}/task/{taskId}")
    public ResponseEntity getTaskOfCourse(@PathVariable UUID id,
                                          @PathVariable UUID sectionId,
                                          @PathVariable UUID taskId,
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
            final Task[] task = {null};
            course.getSections().forEach(section -> {
                if (section.getId().equals(sectionId)) {
                    section.getTasks().forEach(sectionTask -> {
                        if (sectionTask.getId().equals(taskId)) {
                            task[0] = sectionTask;
                        }
                    });
                }
            });
            if (task[0] == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
            }
            TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task[0], user);
            task[0].setTaskFiles(taskFiles == null ? new TaskFiles() : taskFiles);
            return ResponseEntity.ok(task[0]);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/section/{sectionId}/task/{taskId}/submit")
    public ResponseEntity submitTaskFiles(@PathVariable UUID id,
                                          @PathVariable UUID sectionId,
                                          @PathVariable UUID taskId,
                                          @RequestHeader(value="Authorization") String token,
                                          @RequestParam("fileNames") String[] fileNames) {
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
            if (!course.getStudents().stream().anyMatch(student -> student.getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("student not found in course");
            }

            final Task[] task = {null};
            course.getSections().forEach(section -> {
                if (section.getId().equals(sectionId)) {
                    section.getTasks().forEach(sectionTask -> {
                        if (sectionTask.getId().equals(taskId)) {
                            task[0] = sectionTask;
                        }
                    });
                }
            });

            if (task[0] == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
            }
            TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task[0], user);
            if (taskFiles == null) {
                taskFiles = new TaskFiles();
            }

            taskFiles.setStudent(user);
            taskFiles.setTask(task[0]);
            List<File> files = new ArrayList<>();
            for (String fileName : fileNames) {
                File file = new File(fileName, fileName);
                file = fileService.saveFile(file);
                files.add(file);
            }
            taskFiles.setFiles(files);
            taskFiles.setSubmittedAt(LocalDateTime.now());

            taskFiles = taskFilesService.saveTaskFiles(taskFiles);

            return ResponseEntity.ok(taskFiles);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/section/{sectionId}/task/{taskId}/grade")
    public ResponseEntity gradeTaskByStudent(@PathVariable UUID id,
                                             @PathVariable UUID sectionId,
                                             @PathVariable UUID taskId,
                                             @RequestParam("grade") Double grade,
                                             @RequestParam(value = "comment", required = false) String comment,
                                             @RequestParam("studentId") UUID studentId) {
        try {
            User user = userService.getUserById(studentId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            if (!course.getStudents().stream().anyMatch(student -> student.getId().equals(user.getId()))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("student not found in course");
            }

            final Task[] task = {null};
            final CourseSection[] courseSection = {null};
            course.getSections().forEach(section -> {
                if (section.getId().equals(sectionId)) {
                    section.getTasks().forEach(sectionTask -> {
                        if (sectionTask.getId().equals(taskId)) {
                            task[0] = sectionTask;
                            courseSection[0] = section;
                        }
                    });
                }
            });

            if (task[0] == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
            }
            TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task[0], user);
            if (taskFiles == null) {
                taskFiles = new TaskFiles();
            }

            taskFiles.setStudent(user);
            taskFiles.setTask(task[0]);
            taskFiles.setGrade(grade);
            taskFiles.setComment(comment);

            taskFiles = taskFilesService.saveTaskFiles(taskFiles);
            closeCourseSectionByStudent(courseSection[0].getId(), user);
            closeCourseByStudent(course.getId(), user);
            return ResponseEntity.ok(taskFiles);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private void closeCourseByStudent(UUID courseId, User student) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return;
        }
        boolean isClosed = true;
        for (CourseSection section : course.getSections()) {
             if (
                     section.getClosedStudents().stream()
                             .noneMatch(closedStudent -> closedStudent.getId().equals(student.getId()))
             ) {
                 isClosed = false;
             }
        }
        if (isClosed) {
            course.addClosedStudent(student);
            courseService.saveCourse(course);
        }
    }

    private void closeCourseSectionByStudent(UUID sectionId, User student) {
        CourseSection section = courseSectionService.getCourseSectionById(sectionId);
        if (section == null) {
            return;
        }
        boolean isClosed = true;
        for (Task task : section.getTasks()) {
            TaskFiles taskFiles = taskFilesService.getTaskFilesByTaskAndStudent(task, student);
            if (taskFiles == null) {
                isClosed = false;
                continue;
            }
            if (taskFiles.status() != TaskStatus.GRADED) {
                isClosed = false;
            }
        }
        if (isClosed) {
            section.addClosedStudent(student);
            courseSectionService.saveCourseSection(section);
        }
    }

    @GetMapping("/{id}/get/students")
    public ResponseEntity getStudentOfCourse(@PathVariable UUID id,
                                             @RequestParam("page") Integer page,
                                             @RequestParam("limit") Integer limit) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("course not found");
            }
            Pageable pageable = PageRequest.of(page, limit);
            int start = (int)pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), course.getStudents().size());
            if (start <= end) {
                List<User> students = course.getStudents().subList(start, end);

                pageable = PageRequest.of(page + 1, limit);
                start = (int)pageable.getOffset();
                end = Math.min((start + pageable.getPageSize()), course.getStudents().size());

                List<User> nextStudents = new ArrayList<>();
                if (start <= end) {
                    nextStudents = course.getStudents().subList(start, end);
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

    @GetMapping("/get/withoutPage")
    public ResponseEntity getAllCoursesWithoutPage(@RequestParam(value = "status", required = false) CourseStatus status) {
        try {
            if (status != null) {
                List<Course> courses = courseService.getAllCoursesByStatusWithoutPage(status);

                return ResponseEntity.ok(courses);
            }
            List<Course> courses = courseService.getAllCourseWithoutPageable();

            return ResponseEntity.ok(courses);
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
                    if (courseSection.isClosed() || courseSection.getTasks().isEmpty()) {
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

    @GetMapping ("/get/training")
    public ResponseEntity getAllTrainingCourses(@RequestHeader(value="Authorization") String token,
                                                @RequestParam("page") Integer page,
                                                @RequestParam("limit") Integer limit
    ) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            List<Course> courses = courseService.getAllCoursesByTrainer(user, page, limit);
            List<Course> nextCourses = courseService.getAllCoursesByTrainer(user, page + 1, limit);

            ListPagination<Course> listPagination = new ListPagination<>(courses, !nextCourses.isEmpty());

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
