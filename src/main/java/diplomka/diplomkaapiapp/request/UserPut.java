package diplomka.diplomkaapiapp.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserPut {
    private String username;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDate birthDay;
    private String universityName;
    private String specializationName;
    private LocalDate admissionDate;
    private Integer graduationYear;
    private String degreeAwarded;
    private String universityJobName;
    private String position;
    private String degree;
    private String rank;
    private String disciplineNames;
}
