package org.ecommerce.caramellabeachclub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class ProdottoOrdinatoDTO {
    private Integer idProdotto;
    private String nome;
    private String immagineUrl;
    private BigDecimal prezzo;
    private Integer quantita;


}