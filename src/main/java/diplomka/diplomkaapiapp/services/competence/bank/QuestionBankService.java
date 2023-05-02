package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.QuestionBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionBankService {
    private final QuestionBankRepository questionBankRepository;

    public QuestionBank saveQuestionBank(QuestionBank questionBank) {
        return questionBankRepository.save(questionBank);
    }
    public QuestionBank findQuestionById(UUID id) {
        return questionBankRepository.findById(id).orElse(null);
    }
}
