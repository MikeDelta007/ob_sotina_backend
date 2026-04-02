package com.officedubac.project.services;

import com.officedubac.project.models.RegleMatiere;
import com.officedubac.project.models.RegleMatiereCGS;
import com.officedubac.project.models.SpecialiteCGS;
import com.officedubac.project.repository.RegleMatiereCGSRepository;
import com.officedubac.project.repository.RegleMatiereRepository;
import com.officedubac.project.repository.SpecialiteCGSRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegleMatiereCGSService
{

    private final RegleMatiereCGSRepository repository;
    private final SpecialiteCGSRepository repository2;

    public RegleMatiereCGSService(RegleMatiereCGSRepository repository, SpecialiteCGSRepository repository2) {
        this.repository = repository;
        this.repository2 = repository2;
    }

    // 🔹 Créer une règle
    public RegleMatiereCGS create(RegleMatiereCGS regle) {
        return repository.save(regle);
    }

    // 🔹 Lire toutes les règles
    public List<RegleMatiereCGS> findAll() {
        return repository.findAll();
    }

    // 🔹 Lire par id
    public Optional<RegleMatiereCGS> findById(String id) {
        return repository.findById(id);
    }

    // 🔹 Mettre à jour
    public RegleMatiereCGS update(String id, RegleMatiereCGS updated) {
        return repository.findById(id)
                .map(r -> {
                    r.setValeur(updated.getValeur());
                    r.setDate(updated.getDate());
                    r.setHeure(updated.getHeure());
                    return repository.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Règle non trouvée"));
    }

    // 🔹 Supprimer
    public void delete(String id) {
        repository.deleteById(id);
    }

    public List<SpecialiteCGS> findByClasse(String level)
    {
        return repository2.findByClasse(level);
    }

}