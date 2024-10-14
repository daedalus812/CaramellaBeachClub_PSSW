import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';

export interface Prodotto {
  id: number;
  nome: string;
  prezzo: number;
  disponibilita: boolean;
  immagineUrl: string;
}


@Injectable({
  providedIn: 'root'
})
export class ProdottoService {

  private baseUrl = 'http://localhost:8080/prodotti';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  getProdottiDisponibili(): Observable<Prodotto[]> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  
    return this.httpClient.get<Prodotto[]>(`${this.baseUrl}/disponibili`, { headers });
  }}