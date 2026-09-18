package com.officedubac.project.banque;

import com.officedubac.project.banque.dto.BanqueAudit;
import com.officedubac.project.banque.dto.BanqueRequest;
import com.officedubac.project.banque.dto.BanqueResponse;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface BanqueMapper {
    BanqueResponse entiteToResponse(Banque banque);

    @Mapping(source = "auteurName", target = "auteur")
    @Mapping(source = "modifName", target = "modificateur")
    BanqueAudit toEntiteAudit(Banque banque, Long auteurName, Long modifName);

    Banque requestToEntity(BanqueRequest request);

    Banque requestToEntiteAdd(BanqueRequest banqueRequest/*, Utilisateur user*/);   // ici on n'a pa la classe Utilisateur

    //@Mapping(source = "user", target = "utiModifie")
    Banque requestToEntiteUp(@MappingTarget Banque entity, BanqueRequest request/*, Utilisateur user*/);

}
