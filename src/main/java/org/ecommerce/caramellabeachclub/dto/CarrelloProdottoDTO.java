package org.ecommerce.caramellabeachclub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CarrelloProdottoDTO {
    private int idProdotto;
    private String nomeProdotto;
    private BigDecimal prezzo;
    private String imageUrl;
    private int quantita;
}