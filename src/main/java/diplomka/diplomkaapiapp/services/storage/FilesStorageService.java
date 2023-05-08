package diplomka.diplomkaapiapp.services.storage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FilesStorageService {


    private final Path root = Paths.get("../uploads");
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public void save(MultipartFile file, String fileName) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(fileName));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
    public boolean deleteFileByName(String filename) {
        try {
            if (filename.equals("16.jpg")) {
                return true;
            }
            Path filePath = Paths.get("../uploads/" + filename);
            Files.deleteIfExists(filePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
