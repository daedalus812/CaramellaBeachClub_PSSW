package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.CarrelloRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrelloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    public List<Carrello> getCarrello() {
        return carrelloRepository.findAll();
    }
    public Carrello getCarrelloById(int id) {
        return carrelloRepository.findById(id);
    }


}
