package com.officedubac.project.config;

import com.officedubac.project.models.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Certains comptes en base portent un profil dont le nom a été retiré de l'enum
 * Role au fil des évolutions (ex: AGENT_DE_SAISIE, SCOLARITE...). Sans ce
 * converter, la désérialisation Mongo lève IllegalArgumentException dès qu'un
 * seul document porte une valeur obsolète, ce qui fait planter toute lecture
 * (ex: la liste des comptes). On tolère la valeur inconnue en retournant null
 * plutôt que de faire échouer toute la requête.
 */
@ReadingConverter
public class StringToRoleConverter implements Converter<String, Role> {
    @Override
    public Role convert(String source) {
        try {
            return Role.valueOf(source);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
