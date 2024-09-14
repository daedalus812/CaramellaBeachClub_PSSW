package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
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

    @OneToMany(mappedBy = "categoria")
    private Set<Prodotto> prodottos = new LinkedHashSet<>();

}