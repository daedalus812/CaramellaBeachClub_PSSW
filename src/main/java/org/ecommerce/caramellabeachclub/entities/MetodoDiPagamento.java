package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "metodo_di_pagamento")
public class MetodoDiPagamento {
    @Id
    @Column(name = "id_metodo", nullable = false)
    private Integer id;

    @Column(name = "selezione", nullable = false, length = Integer.MAX_VALUE)
    private String selezione;

    @OneToMany(mappedBy = "metodoDiPagamento")
    private Set<Transazione> transaziones = new LinkedHashSet<>();

}