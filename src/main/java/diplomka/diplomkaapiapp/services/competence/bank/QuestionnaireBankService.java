package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.*;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.competence.bank.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionnaireBankService {
    private final QuestionnaireBankRepository questionnaireBankRepository;
    private final ComponentBankRepository componentBankRepository;
    private final SectionBankRepository sectionBankRepository;
    private final QuestionBankRepository questionBankRepository;
    private final AnswerBankRepository answerBankRepository;

    public QuestionnaireBank saveQuestionnaireBank(QuestionnaireBank questionnaireBank) {
        return questionnaireBankRepository.save(questionnaireBank);
    }

    public QuestionnaireBank getQuestionnaireById(UUID id) {
        return questionnaireBankRepository.findById(id).orElse(null);
    }

    public boolean isUserPassedAnketa(QuestionnaireBank questionnaireBank, User user) {
        QuestionnaireBank passedAnketa = questionnaireBankRepository.findByIdAndPassedUsersContaining(
                questionnaireBank.getId(),
                user
        );
        return passedAnketa != null;
    }
}
