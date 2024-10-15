package org.ecommerce.caramellabeachclub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdineDTO {
    private Integer idOrdine;
    private LocalDateTime data;
    private LocalTime ora;
    private String stato;
    private List<ProdottoOrdinatoDTO> prodotti;

}