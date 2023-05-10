package diplomka.diplomkaapiapp.services.file;

import diplomka.diplomkaapiapp.entities.file.File;
import diplomka.diplomkaapiapp.repositories.file.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService {
    private final FileRepository fileRepository;

    public File saveFile(File file) {
        return fileRepository.save(file);
    }

    public File getFileById(UUID id) {
        return fileRepository.findById(id).orElse(null);
    }
}
