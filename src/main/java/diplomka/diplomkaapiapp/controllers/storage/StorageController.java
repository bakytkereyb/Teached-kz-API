package diplomka.diplomkaapiapp.controllers.storage;

import diplomka.diplomkaapiapp.services.storage.FilesStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Storage", description = "Storage controller")
public class StorageController {
    private final FilesStorageService filesStorageService;

    @PostMapping(value = "/upload", consumes = {
            "multipart/form-data"
    })
    public ResponseEntity uploadFileToStorage(@RequestParam("file") MultipartFile file) {
        try {
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            UUID id = UUID.randomUUID();
            String fileName = id + fileExtension;
            filesStorageService.save(file, fileName);
            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/get/{filename:.+}")
    @ResponseBody
    public ResponseEntity getFile(@PathVariable String filename) {
        try {
            Resource file = filesStorageService.load(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename());
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);

//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
