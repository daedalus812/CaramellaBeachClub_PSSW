import { Component, OnInit } from '@angular/core';
import { Product } from '../../product/product';
import { CarrelloService } from '../services/carrello.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  imports: [CommonModule],
  standalone: true,
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  products: Product[] = [];
  quantities: number[] = [];

  constructor(private carrelloService: CarrelloService) { }

  ngOnInit(): void {
    this.products = [
      {
        id: 9,
        name: 'Felpa',
        price: 49.99,
        availability: true,
        imageUrl: 'https://i.ibb.co/QYKqbsF/felpa.jpg'
      },
      {
        id: 7,
        name: 'T-Shirt',
        price: 29.99,
        availability: true,
        imageUrl: 'https://i.ibb.co/vYmTvh3/image.png'
      },
      {
        id: 10,
        name: 'Tazza',
        price: 14.99,
        availability: true,
        imageUrl: 'https://i.ibb.co/jDqyFBY/image.png'
      }
    ];

    // Inizializza le quantità a 1 per ogni prodotto
    this.quantities = this.products.map(() => 1);
  }

  onQuantityChange(event: any, index: number): void {
    const value = event.target.value;
    this.quantities[index] = value > 0 ? value : 1;
  }

  addToCart(productId: number, quantity: number): void {
    this.carrelloService.aggiungiAlCarrello(productId, quantity).subscribe(
      response => {
        console.log(response);
        alert('Prodotto aggiunto al carrello con successo.');
      },
      error => {
        console.error(error);
        alert('Errore durante l\'aggiunta al carrello.');
      }
    );
  }
}
