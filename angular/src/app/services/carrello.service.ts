import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';

export interface CarrelloProdottoDTO {
  idProdotto: number;
  nomeProdotto: string;
  prezzo: number;
  imageUrl: string;
  quantita: number;
}

@Injectable({
  providedIn: 'root'
})
export class CarrelloService {

  private baseUrl = 'http://localhost:8080/carrello';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  private getHeaders(): HttpHeaders {
    const token = this.oauthService.getAccessToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getCartItems(): Observable<CarrelloProdottoDTO[]> {
    return this.httpClient.get<CarrelloProdottoDTO[]>(`${this.baseUrl}/items`, { headers: this.getHeaders() });
  }

  aggiungiAlCarrello(idProdotto: number, quantita: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString(), quantita: quantita.toString() };
    return this.httpClient.post(`${this.baseUrl}/aggiungi`, null, { headers: this.getHeaders(), params });
  }

  plusAdding(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(`${this.baseUrl}/plus`, null, { headers: this.getHeaders(), params });
  }

  minusRemoving(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.put(`${this.baseUrl}/minus`, null, { headers: this.getHeaders(), params });
  }

  rimuoviDalCarrello(idProdotto: number): Observable<any> {
    const params = { idProdotto: idProdotto.toString() };
    return this.httpClient.delete(`${this.baseUrl}/rimuovi`, { headers: this.getHeaders(), params });
  }

  svuotaCarrello(): Observable<any> {
    return this.httpClient.delete(`${this.baseUrl}/svuota`, { headers: this.getHeaders() });
  }

  ordina(metodoPagamento: number, indirizzoSpedizione: string): Observable<any> {
    const params = { metodoPagamento: metodoPagamento.toString(), indirizzoSpedizione };
    return this.httpClient.post(`${this.baseUrl}/ordina`, null, { headers: this.getHeaders(), params });
  }
}