package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.AnswerBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.AnswerBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AnswerBankService {
    private final AnswerBankRepository answerBankRepository;

    public AnswerBank saveAnswerBank(AnswerBank answerBank) {
        return answerBankRepository.save(answerBank);
    }
}
