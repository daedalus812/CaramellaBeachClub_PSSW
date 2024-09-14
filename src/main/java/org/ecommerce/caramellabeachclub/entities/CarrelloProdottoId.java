package org.ecommerce.caramellabeachclub.entities;

import java.io.Serializable;
import java.util.Objects;

public class CarrelloProdottoId implements Serializable {

    private Integer carrelloId;
    private Integer prodottoId;

    // Costruttore vuoto
    public CarrelloProdottoId() {
    }

    // Costruttore con parametri
    public CarrelloProdottoId(Integer carrelloId, Integer prodottoId) {
        this.carrelloId = carrelloId;
        this.prodottoId = prodottoId;
    }

    // Getter e setter
    public Integer getCarrelloId() {
        return carrelloId;
    }

    public void setCarrelloId(Integer carrelloId) {
        this.carrelloId = carrelloId;
    }

    public Integer getProdottoId() {
        return prodottoId;
    }

    public void setProdottoId(Integer prodottoId) {
        this.prodottoId = prodottoId;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrelloProdottoId that = (CarrelloProdottoId) o;
        return Objects.equals(carrelloId, that.carrelloId) && Objects.equals(prodottoId, that.prodottoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carrelloId, prodottoId);
    }
}
