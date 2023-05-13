package diplomka.diplomkaapiapp.controllers.postCourse;

import diplomka.diplomkaapiapp.entities.course.CourseSection;
import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.entities.postCourse.PostCourseSection;
import diplomka.diplomkaapiapp.services.file.FileService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.postCourse.DeletePostCourseService;
import diplomka.diplomkaapiapp.services.postCourse.PostCourseSectionService;
import diplomka.diplomkaapiapp.services.postCourse.PostCourseService;
import diplomka.diplomkaapiapp.services.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post-course/section")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Post Course section", description = "APIs for operation on Post Course section")
public class PostCourseSectionController {
    private final UserService userService;
    private final JwtService jwtService;
    private final FileService fileService;
    private final PostCourseService postCourseService;
    private final PostCourseSectionService postCourseSectionService;
    private final DeletePostCourseService deletePostCourseService;

    @PostMapping("/{id}/add/file")
    public ResponseEntity addFileToSection(@PathVariable UUID id,
                                           @RequestParam("label") String label,
                                           @RequestParam("fileName") String fileName) {
        try {
            PostCourseSection postCourseSection = postCourseSectionService.getPostCourseSectionById(id);
            if (postCourseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post-Course Section not found");
            }
            File file = new File(fileName, label);
            file = fileService.saveFile(file);
            postCourseSection.addFile(file);
            postCourseSection = postCourseSectionService.savePostCourseSection(postCourseSection);
            return ResponseEntity.ok(new String("file added"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/remove/file/{fileId}")
    public ResponseEntity addFileToSection(@PathVariable UUID id,
                                           @PathVariable UUID fileId) {
        try {
            PostCourseSection postCourseSection = postCourseSectionService.getPostCourseSectionById(id);
            if (postCourseSection == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post-Course Section not found");
            }
            File file = fileService.getFileById(fileId);
            if (file == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
            }
            postCourseSection.setFiles(
                    postCourseSection.getFiles().stream()
                            .filter(sectionFile -> !sectionFile.getId().equals(file.getId()))
                            .collect(Collectors.toList())
            );
            postCourseSection = postCourseSectionService.savePostCourseSection(postCourseSection);
            return ResponseEntity.ok(new String("file removed"));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
