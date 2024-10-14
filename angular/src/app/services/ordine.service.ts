import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc'; // Importa OAuthService

export interface ProdottoOrdinato {
  idProdotto: number;
  nome: string;
  immagineUrl: string;
  prezzo: number;
  quantita: number;
}

export interface Ordine {
  idOrdine: number;
  data: string;
  stato: string;
  prodotti: ProdottoOrdinato[];
}

@Injectable({
  providedIn: 'root'
})
export class OrdineService {

  private baseUrl = 'http://localhost:8080/ordini';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  getMieiOrdini(): Observable<Ordine[]> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + this.oauthService.getAccessToken()
    });

    return this.httpClient.get<Ordine[]>(`${this.baseUrl}/miei-ordini`, { headers });
  }

  annullaOrdine(idOrdine: number, motivo: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + this.oauthService.getAccessToken()
    });

    const params = new HttpParams().set('motivo', motivo);

    const url = `${this.baseUrl}/${idOrdine}/annulla`;

    return this.httpClient.post(url, null, { headers, params, responseType: 'text' });
}

  effettuaReso(idOrdine: number, motivo: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + this.oauthService.getAccessToken()
    });

    
    const params = new HttpParams().set('motivo', motivo);
    const url = `${this.baseUrl}/${idOrdine}/reso`;
    return this.httpClient.post(url, motivo, { headers, params, responseType: 'text' });
  }

  annullaReso(idOrdine: number): Observable<any> {
    
    const headers = new HttpHeaders({
  
      'Authorization': 'Bearer ' + this.oauthService.getAccessToken()
    });

    const url = `${this.baseUrl}/${idOrdine}/reso/annulla`;
    return this.httpClient.post(url, null, { headers, responseType: 'text' });
  }

}
