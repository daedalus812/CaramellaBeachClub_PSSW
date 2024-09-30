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

    /*

    La relazione Many-to-One tra MetodoDiPagamento e Transazione la considero appropriata,
    dato che un metodo di pagamento può essere utilizzato in molte transazioni.
    Anche se normalmente un ordine potrebbe generare una sola transazione,
    mantenere la relazione Many-to-One mi offre maggiore flessibilità e
    mi consente di gestire più transazioni associate a un metodo di pagamento.

    */
}