package diplomka.diplomkaapiapp.controllers.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import diplomka.diplomkaapiapp.entities.user.User;
import diplomka.diplomkaapiapp.services.jwt.JwtService;
import diplomka.diplomkaapiapp.services.user.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity login(@RequestParam("username") String username,
                                @RequestParam("password") String password) {
        try {
            log.info("username:" +  username);
            log.info("password:" +  password);
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            username,
//                            password
//                    )
//            );
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("user not found");
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getRoleName()));});
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);

            var jwtToken = jwtService.generateToken(userDetails);
                Token token = Token.builder()
                        .access_token(jwtToken)
                        .build();
                return ResponseEntity.ok(token);
            }
            return ResponseEntity.badRequest().body("password is incorrect");
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body("bad request");
        }
    }


//    @GetMapping("/get/{token}")
//    public ResponseEntity getUserByToken(@PathVariable String token) {
//        try {
//            if (token == null || token.isEmpty()) {
//                return ResponseEntity.badRequest().body("token is empty or null");
//            }
//            String username = getUsernameFromToken(token);
//            if (username.equals("admin")) {
//                return ResponseEntity.ok(true);
//            }
//            return ResponseEntity.badRequest().body("user not found");
//        } catch (Exception e) {
//            log.error(e.toString());
//            return ResponseEntity.badRequest().body("bad request");
//        }
//    }


}

@Data
@AllArgsConstructor
@Builder
class Token {
    private String access_token;
}
