package diplomka.diplomkaapiapp.services.user;

import diplomka.diplomkaapiapp.entities.user.University;
import diplomka.diplomkaapiapp.repositories.user.UniversityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UniversityService {
    private final UniversityRepository universityRepository;

    public University saveUniversity(University university) {
        if (getUniversityByName(university.getName()) == null) {
            return universityRepository.save(university);
        }
        return null;
    }

    public University getUniversityById(UUID id) {
        return universityRepository.findById(id).orElse(null);
    }

    public University getUniversityByName(String name) {
        return universityRepository.findByName(name).orElse(null);
    }

    public List<University> getAllUniversities() {
        return universityRepository.findAll(Sort.by("name").ascending());
    }
}
