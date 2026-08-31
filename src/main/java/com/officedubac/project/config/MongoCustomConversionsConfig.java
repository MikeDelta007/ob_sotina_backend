package com.officedubac.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

/**
 * Configuration séparée de MongoConfig pour éviter tout cycle de dépendances :
 * ce bean doit être disponible avant la construction de MongoTemplate /
 * MappingMongoConverter, alors que MongoConfig dépend lui-même de MongoTemplate.
 */
@Configuration
public class MongoCustomConversionsConfig {

    // Tolère les rôles legacy/inconnus stockés en base (voir StringToRoleConverter)
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new StringToRoleConverter()));
    }
}
