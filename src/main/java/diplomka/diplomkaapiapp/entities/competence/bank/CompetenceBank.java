package diplomka.diplomkaapiapp.entities.competence.bank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "competence_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetenceBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    private List<ComponentBank> componentBankList;

    public CompetenceBank(String name) {
        this.name = name;
    }

    public void addComponent(ComponentBank componentBank) {
        componentBankList.add(componentBank);
    }
}
