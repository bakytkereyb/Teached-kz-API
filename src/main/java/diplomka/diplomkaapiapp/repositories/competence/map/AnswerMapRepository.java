package diplomka.diplomkaapiapp.repositories.competence.map;

import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerMapRepository extends JpaRepository<AnswerMap, UUID>, PagingAndSortingRepository<AnswerMap, UUID> {
}
