package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ATTENZIONE, SOLO PER TEST!!

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    public boolean verificaCredenziali(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email);

        if (utente != null && utente.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

}