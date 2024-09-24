package org.ecommerce.caramellabeachclub.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class CarrelloProdottoId implements Serializable {

    private Integer carrelloId;
    private Integer prodottoId;


}