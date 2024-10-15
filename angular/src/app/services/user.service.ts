import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';

export interface Utente {
  id: number;
  nome: string;
  cognome: string;
  email: string;
  telefono: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:8080/utenti';

  constructor(private http: HttpClient, private oauthService: OAuthService) { }

  getCurrentUser(): Observable<Utente> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const url = `${this.baseUrl}/me`;
    return this.http.get<Utente>(url, { headers });
  }

  updateUser(user: Utente): Observable<Utente> {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json' 
    });
    const url = `${this.baseUrl}/me`;
    return this.http.put<Utente>(url, user, { headers });
  }
}