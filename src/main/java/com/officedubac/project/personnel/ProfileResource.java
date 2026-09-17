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
        Personnel personnel = user.getPersonnel();
        personnel.setPhone(dto.getPhone());
        personnel.setEmail(dto.getEmail());
        personnel.setCivilite(dto.getCivilite());
        personnel.setBank(dto.getBank());
        personnel.setCode_bank(dto.getCode_bank());
        personnel.setCode_agc(dto.getCode_agc());
        personnel.setNum_compte(dto.getNum_compte());
        personnel.setKey_rib(dto.getKey_rib());
        personnel.setVoiture(dto.getVoiture());
        return ResponseEntity.ok(userRepository.save(user));
    }

    private User currentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
