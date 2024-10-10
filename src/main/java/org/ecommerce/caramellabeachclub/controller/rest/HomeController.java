package org.ecommerce.caramellabeachclub.controller.rest;


import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.jwt.CustomJwt;
import org.ecommerce.caramellabeachclub.repositories.ProdottoRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RequestMapping("")
public class HomeController {
    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_fullstack-developer')")
    public ResponseEntity<String> home(){
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        Utente loggato = utenteRepository.findByEmail(jwt.getName());
        String email = jwt.getName();
        if(loggato == null){
            Utente save = new Utente();
            save.setEmail(email);
            save.setNome(jwt.getFirstname());
            save.setCognome(jwt.getLastname());
            utenteRepository.save(save);
        }
        return ResponseEntity.ok("Utente loggato correttamente e presente nel database");
    }

    @GetMapping("/prodotti")
    public ResponseEntity<List<Prodotto>> getAllProdotti() {
        List<Prodotto> prodotti = prodottoRepository.findAll();
        return ResponseEntity.ok(prodotti);
    }
}
