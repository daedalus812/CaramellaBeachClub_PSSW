package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "reso")
public class Reso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reso", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine idOrdine;

    @Column(name = "motivo", nullable = false, length = 100)
    private String motivo;

    @Column(name = "stato_reso", nullable = false, length = 50)
    private String statoReso;

}