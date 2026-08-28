package com.officedubac.project.expressionBesoin;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ExpressionBesoinRepository extends MongoRepository<ExpressionBesoin, String> {
    List<ExpressionBesoin> findByCreeParOrderByDateCreationDesc(String creePar);
    List<ExpressionBesoin> findByStatutOrderByDateCreationDesc(ExpressionBesoin.Statut statut);
    List<ExpressionBesoin> findByStatutAndUtiliseePourMandatementFalseOrderByDateCreationDesc(ExpressionBesoin.Statut statut);
}
