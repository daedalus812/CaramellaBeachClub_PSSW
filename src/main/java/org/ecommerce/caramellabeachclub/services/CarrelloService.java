package org.ecommerce.caramellabeachclub.services;

import org.ecommerce.caramellabeachclub.entities.*;
import org.ecommerce.caramellabeachclub.repositories.CarrelloProdottoRepository;
import org.ecommerce.caramellabeachclub.repositories.CarrelloRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.ecommerce.caramellabeachclub.resources.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CarrelloService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;

    @Transactional
    public void aggiungiAlCarrello(int idUtente, Prodotto p, int quantita)
            throws UserNotFoundException, InvalidQuantityException {

        //Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Per proseguire devi prima loggarti!");
        }

        //Verifico che esista un carrello per quell'utente, altrimenti se è vuoto lo creo
        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());
        if (carrello == null) {
            carrello = new Carrello();
            carrello.setIdUtente(user);
            carrello = carrelloRepository.save(carrello);
        }

        //Verifico la disponibilità del prodotto
        if (p.getDisp() < quantita) {
            throw new InvalidQuantityException
                    ("Impossibile aggiungere al carrello: il prodotto non è disponibile per la quantità desiderata");
        }

        //Creo l'aggiunta e la collego al carrello dell'utente
        CarrelloProdotto aggiunta = new CarrelloProdotto();
        aggiunta.setCarrello(carrello);
        aggiunta.setProdotto(p);
        aggiunta.setQuantita(quantita);
        carrelloProdottoRepository.save(aggiunta);

    }//aggiungi al carrello

    public void rimuoviDalCarrello(int idUtente, CarrelloProdotto cp) throws IllegalAccessException {

        //Verifico che l'utente esista
        Utente user = utenteRepository.getUtenteById(idUtente);
        if (user == null) { throw new UserNotFoundException("Per proseguire devi prima loggarti!"); }

        Carrello carrello = carrelloRepository.findByIdUtente(user.getId());

        //Ulteriori verifiche
        if (carrello == null) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }
        if (!(cp.getCarrello().equals(carrello))) {
            throw new InvalidOperationException("Impossibile proseguire con l'operazione"); }

        carrelloProdottoRepository.delete(cp);
        carrelloRepository.save(carrello);
        

    }//rimuovi dal carrello


}
