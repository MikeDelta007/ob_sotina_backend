package com.officedubac.project.banque;

import com.officedubac.project.banque.dto.BanqueAudit;
import com.officedubac.project.banque.dto.BanqueRequest;
import com.officedubac.project.banque.dto.BanqueResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// Mapping manuel (pas de MapStruct dans ce projet) entre Banque et ses DTO.
@Component
public class BanqueMapper {

    public BanqueResponse entiteToResponse(Banque banque) {
        if (banque == null) return null;
        return new BanqueResponse(
                banque.getId(),
                banque.getName(),
                banque.getCodeBanque(),
                banque.getNomComplet(),
                banque.getUtiCree(),
                banque.getDateCreation(),
                banque.getUtiModifie(),
                banque.getDateModification()
        );
    }

    public BanqueAudit toEntiteAudit(Banque banque, Long auteurId, Long modifId) {
        if (banque == null) return null;
        BanqueAudit audit = new BanqueAudit();
        audit.setId(banque.getId());
        audit.setName(banque.getName());
        audit.setAuteur(auteurId != null ? auteurId.toString() : null);
        audit.setDateCreation(banque.getDateCreation());
        audit.setModificateur(modifId != null ? modifId.toString() : null);
        audit.setDateModification(banque.getDateModification());
        return audit;
    }

    public Banque requestToEntity(BanqueRequest request) {
        if (request == null) return null;
        return Banque.builder()
                .name(request.getName())
                .codeBanque(request.getCodeBanque())
                .NomComplet(request.getNomComplet())
                .dateCreation(LocalDateTime.now())
                .build();
    }

    public Banque requestToEntiteUp(Banque entity, BanqueRequest request) {
        if (entity == null || request == null) return entity;
        entity.setName(request.getName());
        entity.setCodeBanque(request.getCodeBanque());
        entity.setNomComplet(request.getNomComplet());
        entity.setDateModification(LocalDateTime.now());
        return entity;
    }
}
