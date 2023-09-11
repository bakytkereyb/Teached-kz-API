package diplomka.diplomkaapiapp.entities.competence.bank;

import com.fasterxml.jackson.annotation.JsonGetter;
import diplomka.diplomkaapiapp.request.anketaCreate.SectionCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "section_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String name;

    @JsonGetter
    public Double maxPoint() {
        Double result = 0.0;
        for (QuestionBank questionBank : questionBankList) {
            if (questionBank.getMaxPoint() == null) {
                continue;
            }
            result += questionBank.getMaxPoint();
        }
        return result;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<QuestionBank> questionBankList = new ArrayList<>();

    public SectionBank(SectionCreate sectionCreate) {
        this.name = sectionCreate.getName();
    }

    @Transient
    private Double point;
}
