package diplomka.diplomkaapiapp.services.postCourse;

import diplomka.diplomkaapiapp.entities.postCourse.PostCourseSection;
import diplomka.diplomkaapiapp.repositories.postCourse.PostCourseSectionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostCourseSectionService {
    private final PostCourseSectionRepository postCourseSectionRepository;

    public PostCourseSection savePostCourseSection(PostCourseSection postCourseSection) {
        return postCourseSectionRepository.save(postCourseSection);
    }

    public PostCourseSection getPostCourseSectionById(UUID id) {
        return postCourseSectionRepository.findById(id).orElse(null);
    }
}
