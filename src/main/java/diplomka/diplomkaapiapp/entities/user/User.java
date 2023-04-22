package diplomka.diplomkaapiapp.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import diplomka.diplomkaapiapp.entities.competence.bank.CompetenceBank;
import diplomka.diplomkaapiapp.request.UserPut;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String username;

    @JsonIgnore
    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String password;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String firstName;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String secondName;

    @Column(columnDefinition = "TEXT")
    private String middleName;

    private LocalDate birthDay;

    @Column(columnDefinition = "TEXT")
    private String specializationName;

    private LocalDate admissionDate;
    private Integer graduationYear;

    @Column(columnDefinition = "TEXT")
    private String degreeAwarded;

    @Column(columnDefinition = "TEXT")
    private String universityName;

    @Column(columnDefinition = "TEXT")
    private String position;

    @Column(columnDefinition = "TEXT")
    private String degree;

    @Column(columnDefinition = "TEXT")
    private String rank;

    @Column(columnDefinition = "TEXT")
    private String disciplineNames;

    @NotNull
    @NotEmpty
    @Email
    @Column(columnDefinition = "TEXT")
    private String email;

    private Boolean isConfirmed;

//    @Transient
//    private CompetenceBank competenceBank;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @JsonIgnore
    public void setUserPut(UserPut userPut) {
        this.firstName = userPut.getFirstName();
        this.secondName = userPut.getSecondName();
        this.middleName = userPut.getMiddleName();
        this.birthDay = userPut.getBirthDay();
        this.specializationName = userPut.getSpecializationName();
        this.admissionDate = userPut.getAdmissionDate();
        this.graduationYear = userPut.getGraduationYear();
        this.degreeAwarded = userPut.getDegreeAwarded();
        this.universityName = userPut.getUniversityName();
        this.position = userPut.getPosition();
        this.degree = userPut.getDegree();
        this.rank = userPut.getRank();
        this.disciplineNames = userPut.getDisciplineNames();
    }

    public boolean isFullFilled() {
        return
                !StringUtils.isEmpty(firstName) &&
                !StringUtils.isEmpty(secondName) &&
                !StringUtils.isEmpty(middleName) &&
                birthDay != null &&
                !StringUtils.isEmpty(specializationName) &&
                admissionDate != null &&
                graduationYear != null &&
                !StringUtils.isEmpty(degreeAwarded) &&
                !StringUtils.isEmpty(universityName) &&
                !StringUtils.isEmpty(position) &&
                !StringUtils.isEmpty(degree) &&
                !StringUtils.isEmpty(rank) &&
                !StringUtils.isEmpty(disciplineNames);
    }

    public User(String username, String password, String firstName, String secondName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
    }

//    @JsonIgnore
    public boolean isTrainer() {
        for (Role role : roles) {
            if (role.getRoleName().equals("trainer")) {
                return true;
            }
        }
        return false;
    }

//    @JsonIgnore
    public boolean isAdmin() {
        for (Role role : roles) {
            if (role.getRoleName().equals("admin")) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getRoleName()));});
        return authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
