package org.ecommerce.retroemporium.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "descrizione", length = 50)
    private String descrizione;

    @OneToMany(mappedBy = "idCategoria")
    private Set<Prodotto> prodottos = new LinkedHashSet<>();

}