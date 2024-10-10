import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CarrelloService {

  private baseUrl = 'http://localhost:8080/carrello'; // Assicurati che l'URL sia corretto

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  aggiungiAlCarrello(idProdotto: number, quantita: number): Observable<any> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Poiché il backend si aspetta parametri tramite @RequestParam, li inviamo come query params
    const params = new HttpParams()
      .set('idProdotto', idProdotto.toString())
      .set('quantita', quantita.toString());

    return this.httpClient.post(`${this.baseUrl}/aggiungi`, null, { headers, params, responseType: 'text' });
  }
}
