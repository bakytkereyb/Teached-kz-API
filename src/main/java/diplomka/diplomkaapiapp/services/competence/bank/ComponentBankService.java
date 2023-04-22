package diplomka.diplomkaapiapp.services.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.ComponentBank;
import diplomka.diplomkaapiapp.repositories.competence.bank.ComponentBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ComponentBankService {
    private final ComponentBankRepository componentBankRepository;

    public ComponentBank saveComponentBank(ComponentBank componentBank) {
        return componentBankRepository.save(componentBank);
    }

    public ComponentBank getComponentById(UUID id) {
        return componentBankRepository.findById(id).orElse(null);
    }
}
