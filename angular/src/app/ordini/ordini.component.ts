import { Component, OnInit } from '@angular/core';
import { OrdineService, Ordine, ProdottoOrdinato } from '../services/ordine.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ordini',
  templateUrl: './ordini.component.html',
  styleUrls: ['./ordini.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class OrdiniComponent implements OnInit {

  ordini: Ordine[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private ordineService: OrdineService) { }

  ngOnInit(): void {
    this.loadOrdini();
  }

  loadOrdini(): void {
    this.ordineService.getMieiOrdini().subscribe(
      ordini => {
        this.ordini = ordini;
        this.isLoading = false;
      },
      error => {
        console.error('Errore nel caricamento degli ordini:', error);
        this.errorMessage = 'Errore nel caricamento degli ordini.';
        this.isLoading = false;
      }
    );
  }

  calcolaTotaleOrdine(prodotti: ProdottoOrdinato[]): number {
    if (!prodotti || prodotti.length === 0) {
      return 0;
    }
    return prodotti.reduce((totale, prodotto) => {
      return totale + (prodotto.prezzo * prodotto.quantita);
    }, 0);
  }

  confermaAnnullamentoOrdine(ordine: Ordine): void {
    const motivo = prompt('Inserisci il motivo dell\'annullamento:');
    if (motivo) {
      const conferma = confirm('Sei sicuro di voler annullare l\'ordine?');
      if (conferma) {
        this.ordineService.annullaOrdine(ordine.idOrdine, motivo).subscribe(
          response => {
            alert('Ordine annullato con successo.');
            ordine.stato = 'Annullato. Motivo: ' + motivo;
          },
          error => {
            alert('Errore durante l\'annullamento dell\'ordine.');
          }
        );
      }
    }
  }

  confermaResoOrdine(ordine: Ordine): void {
    const motivo = prompt('Inserisci il motivo del reso:');
    if (motivo) {
      const conferma = confirm('Sei sicuro di voler richiedere un reso per questo ordine?');
      if (conferma) {
        this.ordineService.effettuaReso(ordine.idOrdine, motivo).subscribe(
          response => {
            alert('Reso richiesto con successo.');
            ordine.stato = 'Reso';
          },
          error => {
            alert('Errore durante la richiesta di reso.');
          }
        );
      }
    }
  }

}