package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.QuestionnaireBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.QuestionnaireBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionnaireBankService {
    private final QuestionnaireBankRepository questionnaireBankRepository;

    public QuestionnaireBank saveQuestionnaireBank(QuestionnaireBank questionnaireBank) {
        return questionnaireBankRepository.save(questionnaireBank);
    }
}
