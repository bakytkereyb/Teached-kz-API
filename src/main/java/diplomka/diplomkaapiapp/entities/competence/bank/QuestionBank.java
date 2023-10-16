package diplomka.diplomkaapiapp.entities.competence.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.competence.QuestionType;
import diplomka.diplomkaapiapp.entities.competence.map.AnswerMap;
import diplomka.diplomkaapiapp.request.anketaCreate.QuestionCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "question_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String questionKz;

    @Column(columnDefinition = "TEXT")
    private String questionRu;

    @NotNull
    @Column(columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private Double maxPoint;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AnswerBank> answerBankList = new ArrayList<>();

    public QuestionBank(QuestionCreate questionCreate) {
        this.question = questionCreate.getQuestion();
        this.questionKz = questionCreate.getQuestionKz();
        this.questionRu = questionCreate.getQuestionRu();
        this.type = questionCreate.getType();
    }

    @Transient
    private List<AnswerMap> answerMapList;
    @Transient
    private Double point;

    @JsonIgnore
    public Double getPointQuestion(QuestionBank questionBank) {
        Double[] point = {0.0};
//        if (questionBank.getType() == QuestionType.OPEN) {
//            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
//                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
//                    .collect(Collectors.toList());
//            if (answerMapList.size() == 0) {
//                return 0.0;
//            }
//            if (answerMapList.get(0).getIsSelected() == null) {
//                return 0.0;
//            }
//            if (answerMapList.get(0).getIsSelected()) {
//                return 1.0;
//            } else {
//                return 0.0;
//            }
//        }
//        if (questionBank.getType() == QuestionType.LIST) {
//            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
//                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
//                    .collect(Collectors.toList());
//            if (answerMapList.size() == 0) {
//                return 0.0;
//            }
//            for (AnswerMap answerMap : answerMapList) {
//                if (answerMap.getIsSelected() != null) {
////                    if (answerMap.getAnswerBank().getIsCorrect()) {
////                        if (answerMap.getIsSelected()) {
////                            point[0] += 1.0;
////                        }
////                    }
//                    point[0] += answerMap.getAnswerBank().getPoint();
//                }
//            }
//            if (point[0] > 0.0) {
//                return 1.0;
//            } else {
//                return 0.0;
//            }
//        }
//        if (questionBank.getType() == QuestionType.MCQ) {
//            List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
//                    .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
//                    .collect(Collectors.toList());
//            if (answerMapList.size() == 0) {
//                return 0.0;
//            }
//            Double divideNum = 1.0;
//            for (AnswerMap answerMap : answerMapList) {
//                if (answerMap.getIsSelected() != null) {
//                    if (answerMap.getAnswerBank().getIsCorrect()) {
//                        if (answerMap.getIsSelected()) {
//                            point[0] += 1.0;
//                        }
//                    } else {
//                        if (answerMap.getIsSelected()) {
//                            divideNum += 1.0;
//                        }
//                    }
//                }
//            }
//
//            point[0] /= divideNum;
//        }

        List<AnswerMap> answerMapList = questionBank.getAnswerMapList().stream()
                .filter(answerMap -> answerMap.getQuestionBank().getId() == questionBank.getId())
                .collect(Collectors.toList());
        if (answerMapList.size() == 0) {
            return 0.0;
        }
        for (AnswerMap answerMap : answerMapList) {
            if (answerMap.getIsSelected() != null) {
                if (answerMap.getIsSelected()) {
//                    if (answerMap.getIsSelected()) {
//                        point[0] += 1.0;
//                    }
                    point[0] += answerMap.getAnswerBank().getPoint();
                }
            }
        }

        System.out.println(point[0]);
        return point[0];
    }
}
