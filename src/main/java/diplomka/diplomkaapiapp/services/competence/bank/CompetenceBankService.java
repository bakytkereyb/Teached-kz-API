package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.CompetenceBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CompetenceBankService {
    private final CompetenceBankRepository competenceBankRepository;

    public CompetenceBank saveCompetenceBank(CompetenceBank competenceBank) {
        return competenceBankRepository.save(competenceBank);
    }

    public CompetenceBank initCompetenceBank(CompetenceBank competenceBank) {
        if (competenceBankRepository.findAll().get(0) != null) {
            return null;
        }
        return competenceBankRepository.save(competenceBank);
    }

    public CompetenceBank getCompetenceBank() {
        return competenceBankRepository.findAll().get(0);
    }
}
