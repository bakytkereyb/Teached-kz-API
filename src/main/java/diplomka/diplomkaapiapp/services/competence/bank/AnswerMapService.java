package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.competence.map.AnswerMapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnswerMapService {
    private final AnswerMapRepository answerMapRepository;

    public List<AnswerMap> getAllAnswersByQuestionAndUser(QuestionBank questionBank, User user) {
        return answerMapRepository.findAllByQuestionBankAndUser(questionBank, user);
    }
    public AnswerMap saveAnswerMap(AnswerMap answerMap) {
        return answerMapRepository.save(answerMap);
    }
}
