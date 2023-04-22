package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.SectionBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.SectionBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SectionBankService {
    private final SectionBankRepository sectionBankRepository;

    public SectionBank saveSectionBank(SectionBank sectionBank) {
        return sectionBankRepository.save(sectionBank);
    }
}
