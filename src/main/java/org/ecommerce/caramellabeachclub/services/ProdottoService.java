package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.repositories.ProdottoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProdottoService {
    private final ProdottoRepository prodottoRepository;


    public ProdottoService(ProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

}