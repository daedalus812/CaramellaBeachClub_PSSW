package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Table(name = "prodotto")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prodotto", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "descrizione", length = 50)
    private String descrizione;

    @Column(name = "immagine_url", length = 200)
    private String immagineUrl;

    @Column(name = "prezzo", nullable = false, precision = 10, scale = 2)
    // precision= 10, scale= 2  | per rappresentare correttamente i valori monetari, es. 49.99€
    private BigDecimal prezzo;

    @OneToMany(mappedBy = "prodotto")
    private Set<CarrelloProdotto> carrelloProdottos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProdotto")
    private Set<Recensione> recensiones = new LinkedHashSet<>();

    @Column(name = "disp")
    private Integer disp;
}