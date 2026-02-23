package com.officedubac.project.dto;

import com.officedubac.project.models.HoraireItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "horaire_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HoraireRequest
{
    @Id
    private String id;
    private Map<String, HoraireItem> horaires;

    public HoraireRequest(Map<String, HoraireItem> horaires)
    {

    }
}