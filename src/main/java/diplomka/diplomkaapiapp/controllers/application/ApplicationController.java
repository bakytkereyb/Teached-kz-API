package diplomka.diplomkaapiapp.controllers.application;

import diplomka.diplomkaapiapp.entities.application.Application;
import diplomka.diplomkaapiapp.entities.application.ApplicationStatus;
import diplomka.diplomkaapiapp.entities.course.Course;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.request.ListPagination.ListPagination;
import diplomka.diplomkaapiapp.services.application.ApplicationService;
import diplomka.diplomkaapiapp.services.file.FileService;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Application", description = "APIs for operation on Application")
public class ApplicationController {
    private final UserService userService;
    private final JwtService jwtService;
    private final FileService fileService;
    private final ApplicationService applicationService;

    @PostMapping("/save")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Application.class))})
    })
    public ResponseEntity saveApplication(@RequestHeader(value="Authorization") String token,
                                          @RequestParam("title") String title,
                                          @RequestParam("body") String body) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }
            Application application = new Application(user, title, body);
            application = applicationService.saveApplication(application);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListPagination.class))})
    })
    public ResponseEntity getAllApplications(@RequestParam("page") Integer page,
                                             @RequestParam("limit") Integer limit) {
        try {
            List<Application> applications = applicationService.getAllApplications(page, limit);
            List<Application> nextApplications = applicationService.getAllApplications(page + 1, limit);

            ListPagination<Application> listPagination = new ListPagination<>(applications, !nextApplications.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Application.class))})
    })
    public ResponseEntity getApplicationById(@PathVariable UUID id) {
        try {
            Application application = applicationService.getApplicationById(id);

            if (application == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("application not found");
            }

            return ResponseEntity.ok(application);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Application.class))})
    })
    public ResponseEntity changeApplicationStatus(@PathVariable UUID id,
                                                  @RequestParam("status") ApplicationStatus status) {
        try {
            Application application = applicationService.getApplicationById(id);

            if (application == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("application not found");
            }

            application.setStatus(status);

            application = applicationService.saveApplication(application);

            return ResponseEntity.ok(application);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/my")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListPagination.class))})
    })
    public ResponseEntity getAllMyApplications(@RequestParam("page") Integer page,
                                               @RequestParam("limit") Integer limit,
                                               @RequestHeader(value="Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
            }

            List<Application> applications = applicationService.getAllApplicationsByUser(user, page, limit);
            List<Application> nextApplications = applicationService.getAllApplicationsByUser(user, page + 1, limit);

            ListPagination<Application> listPagination = new ListPagination<>(applications, !nextApplications.isEmpty());

            return ResponseEntity.ok(listPagination);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
