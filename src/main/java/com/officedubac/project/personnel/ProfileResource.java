package com.officedubac.project.personnel;

import com.officedubac.project.models.User;
import com.officedubac.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileResource {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(currentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMe(@RequestBody ProfileUpdateDTO dto) {
        User user = currentUser();
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setCivilite(dto.getCivilite());
        user.setBank(dto.getBank());
        user.setCode_bank(dto.getCode_bank());
        user.setCode_agc(dto.getCode_agc());
        user.setNum_compte(dto.getNum_compte());
        user.setKey_rib(dto.getKey_rib());
        user.setMatricule_voiture(dto.getMatricule_voiture());
        return ResponseEntity.ok(userRepository.save(user));
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
