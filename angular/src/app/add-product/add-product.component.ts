import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { OAuthService } from 'angular-oauth2-oidc';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css']
})
export class AddProductComponent {
  prodotto = {
    nome: '',
    descrizione: '',
    prezzo: 0,
    immagineUrl: '',
    disp: 0,
    disponibilita: false
  };

  successMessage: string = '';
  errorMessage: string = '';

  constructor(private httpClient: HttpClient, private oauthService: OAuthService) { }

  aggiungiProdotto() {
    const token = this.oauthService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    this.httpClient.post<{ message: string }>('http://localhost:8080/aggiungi', this.prodotto, { headers })
      .subscribe(
        response => {
          this.successMessage = response.message;
          this.errorMessage = '';
          this.prodotto = { 
            nome: '', 
            descrizione: '', 
            prezzo: 0, 
            immagineUrl: '', 
            disp: 0, 
            disponibilita: false 
          }; 
        },
        error => {
          console.error('Errore nell\'aggiunta del prodotto:', error);
          this.errorMessage = error.error.error || 'Errore nell\'aggiunta del prodotto.';
          this.successMessage = '';
        }
      );
  }
}