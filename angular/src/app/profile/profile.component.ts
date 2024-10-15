import { Component, OnInit } from '@angular/core';
import { UserService, Utente } from '../services/user.service';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class ProfileComponent implements OnInit {

  user: Utente | null = null;
  profileForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private userService: UserService) {
    this.profileForm = new FormGroup({
      telefono: new FormControl('', [
        Validators.pattern('^(\\+\\d{1,3}( )?)?\\d{10}$')
      ])
    });
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    this.userService.getCurrentUser().subscribe(
      (data) => {
        this.user = data;
        this.profileForm.patchValue({
          telefono: this.user.telefono
        });
      },
      (error) => {
        console.error('Errore nel caricamento dei dati utente:', error);
        this.errorMessage = 'Impossibile caricare i dati utente.';
      }
    );
  }

  onSubmit(): void {
    if (this.profileForm.invalid || !this.user) {
      return;
    }

    const updatedUser: Utente = {
      ...this.user,
      telefono: this.profileForm.value.telefono
    };

    this.userService.updateUser(updatedUser).subscribe(
      (data) => {
        this.user = data;
        this.successMessage = 'Numero di telefono aggiornato con successo.';
        this.errorMessage = '';
      },
      (error) => {
        console.error('Errore nell\'aggiornamento dei dati utente:', error);
        this.errorMessage = 'Errore durante l\'aggiornamento del numero di telefono.';
        this.successMessage = '';
      }
    );
  }

  get telefono() {
    return this.profileForm.get('telefono');
  }
}