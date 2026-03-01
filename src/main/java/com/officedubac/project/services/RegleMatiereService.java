package com.officedubac.project.services;

import com.officedubac.project.models.RegleMatiere;
import com.officedubac.project.repository.RegleMatiereRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RegleMatiereService
{

    private final RegleMatiereRepository repository;

    public RegleMatiereService(RegleMatiereRepository repository) {
        this.repository = repository;
    }

    // 🔹 Créer une règle
    public RegleMatiere create(RegleMatiere regle) {
        return repository.save(regle);
    }

    // 🔹 Lire toutes les règles
    public List<RegleMatiere> findAll() {
        return repository.findAll();
    }

    // 🔹 Lire par id
    public Optional<RegleMatiere> findById(String id) {
        return repository.findById(id);
    }

    // 🔹 Mettre à jour
    public RegleMatiere update(String id, RegleMatiere updated) {
        return repository.findById(id)
                .map(r -> {
                    r.setCode(updated.getCode());
                    r.setType(updated.getType());
                    r.setChamp(updated.getChamp());
                    r.setValeur(updated.getValeur());
                    r.setGroupe(updated.getGroupe());
                    r.setSeries(updated.getSeries());
                    r.setHeure1(updated.getHeure1());
                    r.setHeure2(updated.getHeure2());
                    r.setDate1(updated.getDate1());
                    r.setDate2(updated.getDate2());
                    return repository.save(r);
                })
                .orElseThrow(() -> new RuntimeException("Règle non trouvée"));
    }

    // 🔹 Supprimer
    public void delete(String id) {
        repository.deleteById(id);
    }
}