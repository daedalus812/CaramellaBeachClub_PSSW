package org.ecommerce.caramellabeachclub.controller.rest;

import org.ecommerce.caramellabeachclub.repositories.ProdottoRepository;
import org.ecommerce.caramellabeachclub.services.ProdottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RestController
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;
    @Autowired
    private ProdottoRepository prodottoRepository;

    @PreAuthorize("hasAuthority('ROLE_fullstack-developer')")
    @GetMapping( "/products/{id}/availability")
    public ResponseEntity<Integer> getProductAvailability(@PathVariable Integer id) {
        int availability = prodottoRepository.findDisponibilitaById(id);
        return new ResponseEntity<>(availability, HttpStatus.OK);
    }

}
