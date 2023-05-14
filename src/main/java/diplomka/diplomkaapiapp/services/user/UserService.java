package diplomka.diplomkaapiapp.services.user;

import diplomka.diplomkaapiapp.constants.EnvironmentProperties;
import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.repositories.user.RoleRepository;
import diplomka.diplomkaapiapp.repositories.user.UserRepository;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
//public class UserService {
    private final String baseURL = EnvironmentProperties.SERVER_URL;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User saveUser(User user) throws Exception {
        User existingUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        if (existingUser == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        throw new Exception("User already exists");
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findAll(pageable).getContent();
    }

    public List<User> getAllUsersByName(String name, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository
                .findAllByFirstNameContainingIgnoreCaseOrSecondNameContainingIgnoreCaseOrMiddleNameContainsIgnoreCase
                        (name, name, name, pageable);
    }

    public void sendEmailConfirmationMessage(User user) throws MessagingException {
        String jwtToken = baseURL + "/api/user/confirm/" + jwtService.generateToken(user);
        sendMessageToEmail(user.getEmail(), "Email confirmation", jwtToken);
    }

    private void sendMessageToEmail(String toEmail,
                                    String subject,
                                    String body) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setText(body, true); // Use this or above line.
        helper.setTo(toEmail);
        helper.setSubject(subject);

        mailSender.send(mimeMessage);
    }

//    public List<User> getAllUsersByRole(int skip, int limit, Role role) {
//        Pageable pageable = PageRequest.of(skip, limit);
//        return userRepository.findAllByRolesContaining(role, pageable);
//    }

    public List<User> getAllUsersByRoleWithoutPage(Role role) {
        return userRepository.findAllByRolesContaining(role);
    }
}
