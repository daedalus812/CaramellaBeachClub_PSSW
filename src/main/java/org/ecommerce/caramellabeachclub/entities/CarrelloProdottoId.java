package org.ecommerce.caramellabeachclub.entities;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
public class CarrelloProdottoId implements Serializable {

    private Integer carrelloId;
    private Integer prodottoId;
}