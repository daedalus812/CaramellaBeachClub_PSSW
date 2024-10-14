import { Component, OnInit } from '@angular/core';
import { CarrelloService, CarrelloProdottoDTO } from '../services/carrello.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-carrello',
  templateUrl: './carrello.component.html',
  styleUrls: ['./carrello.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class CarrelloComponent implements OnInit {

  cartItems: CarrelloProdottoDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  totalAmount: number = 0;

  // Variabili per l'ordine
  metodoPagamento: number = 1; // Puoi impostare un valore di default
  indirizzoSpedizione: string = '';

  constructor(private carrelloService: CarrelloService) { }

  ngOnInit(): void {
    this.loadCartItems();
  }

  loadCartItems(): void {
    this.carrelloService.getCartItems().subscribe(
      items => {
        this.cartItems = items;
        this.calculateTotal();
        this.isLoading = false;
      },
      error => {
        console.error(error);
        this.errorMessage = 'Errore nel caricamento degli articoli del carrello.';
        this.isLoading = false;
      }
    );
  }

  calculateTotal(): void {
    this.totalAmount = this.cartItems.reduce((acc, item) => acc + (item.prezzo * item.quantita), 0);
  }

  increaseQuantity(idProdotto: number): void {
    this.carrelloService.plusAdding(idProdotto).subscribe(
      () => {
        const item = this.cartItems.find(item => item.idProdotto === idProdotto);
        if (item) {
          item.quantita += 1;
          this.calculateTotal();
        }
      },
      error => {
        console.error(error);
        alert('Errore durante l\'aumento della quantità.');
      }
    );
  }

  decreaseQuantity(idProdotto: number): void {
    this.carrelloService.minusRemoving(idProdotto).subscribe(
      () => {
        const item = this.cartItems.find(item => item.idProdotto === idProdotto);
        if (item) {
          item.quantita -= 1;
          if (item.quantita <= 0) {
            this.cartItems = this.cartItems.filter(item => item.idProdotto !== idProdotto);
          }
          this.calculateTotal();
        }
      },
      error => {
        console.error(error);
        alert('Errore durante la diminuzione della quantità.');
      }
    );
  }

  removeItem(idProdotto: number): void {
    this.carrelloService.rimuoviDalCarrello(idProdotto).subscribe(
      () => {
        this.cartItems = this.cartItems.filter(item => item.idProdotto !== idProdotto);
        this.calculateTotal();
      },
      error => {
        console.error(error);
        alert('Errore durante la rimozione del prodotto dal carrello.');
      }
    );
  }

  emptyCart(): void {
    this.carrelloService.svuotaCarrello().subscribe(
      () => {
        this.cartItems = [];
        this.totalAmount = 0;
      },
      error => {
        console.error(error);
        alert('Errore durante lo svuotamento del carrello.');
      }
    );
  }

  placeOrder(): void {
    if (!this.indirizzoSpedizione) {
      alert('Per favore, inserisci un indirizzo di spedizione.');
      return;
    }

    this.carrelloService.ordina(this.metodoPagamento, this.indirizzoSpedizione).subscribe(
      response => {
        alert('Ordine effettuato con successo.');
        this.cartItems = [];
        this.totalAmount = 0;
        this.indirizzoSpedizione = '';
      },
      error => {
        console.error(error);
        alert('Errore durante l\'effettuazione dell\'ordine.');
      }
    );
  }
}
