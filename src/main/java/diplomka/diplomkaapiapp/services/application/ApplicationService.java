package diplomka.diplomkaapiapp.services.application;

import diplomka.diplomkaapiapp.entities.application.Application;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.application.ApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public Application getApplicationById(UUID id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public List<Application> getAllApplications(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return applicationRepository.findAll(pageable).getContent();
    }

    public List<Application> getAllApplicationsByUser(User user, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        return applicationRepository.findAllByUser(user, pageable);
    }
}
