package com.officedubac.project.dto;

import com.officedubac.project.models.HoraireItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraireRequest
{
    private Map<String, HoraireItem> horaires;
}