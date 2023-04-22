package diplomka.diplomkaapiapp.repositories.competence.bank;

import diplomka.diplomkaapiapp.entities.competence.bank.QuestionBank;
import diplomka.diplomkaapiapp.entities.competence.bank.SectionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SectionBankRepository extends JpaRepository<SectionBank, UUID>, PagingAndSortingRepository<SectionBank, UUID> {
}
