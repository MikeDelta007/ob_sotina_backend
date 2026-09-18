package com.officedubac.project.banque;

import com.officedubac.project.banque.dto.BanqueAudit;
import com.officedubac.project.banque.dto.BanqueRequest;
import com.officedubac.project.banque.dto.BanqueResponse;
import com.officedubac.project.exception.BusinessResourceException;

import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

;

public interface BanqueService {
    public List<BanqueResponse> all() throws BusinessResourceException;

//    public List<BanqueResponse> FonctionEncours() throws BusinessResourceException;
public Optional<BanqueAudit> auditOneById(String id) throws NumberFormatException, BusinessResourceException;

    public Optional<BanqueResponse> oneById(String id) throws NumberFormatException, BusinessResourceException;

    public BanqueResponse add(BanqueRequest req) throws BusinessResourceException;

    public BanqueResponse maj(BanqueRequest req, String id) throws NumberFormatException, NoSuchElementException, BusinessResourceException;

    public String del(String id) throws NumberFormatException, BusinessResourceException;

    void verifyBanqueUnique(String libelleLong)throws  BusinessResourceException;
    Optional<Banque> findByLibelle(String name) throws BusinessResourceException;
    public List<Banque> importExcel(InputStream inputStream) throws Exception;

}
