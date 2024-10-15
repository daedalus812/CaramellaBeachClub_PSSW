package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.entities.Prodotto;
import org.ecommerce.caramellabeachclub.entities.Utente;
import org.ecommerce.caramellabeachclub.jwt.CustomJwt;
import org.ecommerce.caramellabeachclub.repositories.ProdottoRepository;
import org.ecommerce.caramellabeachclub.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> home(){
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
        Map<String, String> response = new HashMap<>();
        response.put("message", "Utente loggato correttamente e presente nel database");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prodotti")
    public ResponseEntity<List<Prodotto>> getAllProdotti() {
        List<Prodotto> prodotti = prodottoRepository.findAll();
        return ResponseEntity.ok(prodotti);
    }

    @PostMapping("/aggiungi")
    @PreAuthorize("hasAuthority('ROLE_owner')")
    public ResponseEntity<Map<String, String>> aggiungiProdotto(@RequestBody Prodotto prodotto){
        try {
            prodottoRepository.save(prodotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto aggiunto con successo.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("error", "Errore nell'aggiunta del prodotto.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}