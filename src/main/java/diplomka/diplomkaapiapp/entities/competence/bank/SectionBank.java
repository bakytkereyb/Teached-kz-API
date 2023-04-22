package diplomka.diplomkaapiapp.entities.competence.bank;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Double maxPoint;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<QuestionBank> questionBankList;

    @Transient
    private Double point;
}
