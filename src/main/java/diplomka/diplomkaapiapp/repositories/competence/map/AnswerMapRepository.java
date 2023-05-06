package diplomka.diplomkaapiapp.repositories.competence.map;

import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerMapRepository extends JpaRepository<AnswerMap, UUID>, PagingAndSortingRepository<AnswerMap, UUID> {
    List<AnswerMap> findAllByQuestionBankAndUser(QuestionBank questionBank, User user);
    List<AnswerMap> findAllByQuestionBank(QuestionBank questionBank);
    List<AnswerMap> findAllByAnswerBank(AnswerBank answerBank);
}
