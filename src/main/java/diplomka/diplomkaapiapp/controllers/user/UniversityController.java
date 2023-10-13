package diplomka.diplomkaapiapp.controllers.user;

import diplomka.diplomkaapiapp.entities.user.University;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.user.UniversityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/university")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "University", description = "APIs for operation on University")
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/get")
    public ResponseEntity getAllUniversities() {
        try {
            List<University> universities = universityService.getAllUniversities();
            return ResponseEntity.ok(universities);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getUniversityById(@PathVariable UUID id) {
        try {
            University university = universityService.getUniversityById(id);
            if (university == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("university not found");
            }
            return ResponseEntity.ok(university);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/save/multiple/file", consumes = {
            "multipart/form-data"
    })
    public ResponseEntity createMultipleUniversityFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            HashSet<String> records = new HashSet<>();
            InputStream is = file.getInputStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    records.add(line);
                }
            }

            for (String line : records) {

                University university = new University(line);
                university = universityService.saveUniversity(university);
                if (university != null) {
                    log.info(line + " : university created");
                } else {
                    log.error(line + " : university error");
                }
            }

            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
}
