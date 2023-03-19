package diplomka.diplomkaapiapp.services.user;

import diplomka.diplomkaapiapp.entities.user.Role;
import diplomka.diplomkaapiapp.repositories.user.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public Role saveRole(Role role) {
        Role existingRole = roleRepository.findByRoleName(role.getRoleName());
        if (existingRole == null) {
            return roleRepository.save(role);
        }
        return existingRole;
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
