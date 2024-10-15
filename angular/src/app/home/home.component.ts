import { Component, OnInit } from '@angular/core';
import { ProdottoService, Prodotto } from '../services/prodotto.service';
import { CarrelloService } from '../services/carrello.service';
import { CommonModule } from '@angular/common';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [CommonModule],
  standalone: true,
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  products: Prodotto[] = [];
  quantities: number[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private prodottoService: ProdottoService, private carrelloService: CarrelloService, private oauthService: OAuthService) { }

  ngOnInit(): void {
    if (this.oauthService.hasValidAccessToken()) {
      this.loadProducts();
    } else {

      this.oauthService.events.subscribe(event => {
        if (event.type === 'token_received') {
          this.loadProducts();
        }
      });
    }
  }

  loadProducts(): void {
    this.prodottoService.getProdottiDisponibili().subscribe(
      prodotti => {
        this.products = prodotti;
        this.quantities = this.products.map(() => 1);
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento dei prodotti:', error);
        this.errorMessage = 'Errore nel caricamento dei prodotti.';
        this.isLoading = false;
      }
    );
  }

  onQuantityChange(event: any, index: number): void {
    const value = event.target.value;
    this.quantities[index] = value > 0 ? value : 1;
  }

  addToCart(productId: number, quantity: number): void {
    this.carrelloService.aggiungiAlCarrello(productId, quantity).subscribe(
      response => {
        console.log('Successo:', response);
        alert('Prodotto aggiunto al carrello con successo.');
      },
      error => {
        console.error('Errore:', error);
        alert('Errore durante l\'aggiunta al carrello.');
      }
    );
}
}