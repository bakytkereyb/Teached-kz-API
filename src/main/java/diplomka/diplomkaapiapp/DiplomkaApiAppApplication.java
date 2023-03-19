package diplomka.diplomkaapiapp;

import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.user.RoleService;
import diplomka.diplomkaapiapp.services.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DiplomkaApiAppApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(DiplomkaApiAppApplication.class, args);
        RoleService roleService = configurableApplicationContext.getBean(RoleService.class);
        UserService userService = configurableApplicationContext.getBean(UserService.class);

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        Role trainerRole = new Role("trainer");
        userRole = roleService.saveRole(userRole);
        adminRole = roleService.saveRole(adminRole);
        trainerRole = roleService.saveRole(trainerRole);
//
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        User user = new User(
//                "bati",
//                passwordEncoder.encode("123"),
//                "B_f",
//                "B_s",
//                "a@a.ru");
//        user.addRole(userRole);
//        try {
//            userService.saveUser(user);
//        } catch (Exception e) {
//
//        }
    }
}
