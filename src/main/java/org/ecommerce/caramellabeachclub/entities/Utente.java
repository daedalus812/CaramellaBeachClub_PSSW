package org.ecommerce.caramellabeachclub.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
@Entity
@Table(name = "utente")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente", nullable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "telefono")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?\\d{10}$", message = "Numero di telefono non valido")
    private String telefono;

}