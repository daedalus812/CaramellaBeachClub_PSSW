import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { HttpClient } from '@angular/common/http';
import { filter } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Caramella Beach Club | Shop';
  benvenutoText = 'Benvenuto';
  isOwner: boolean = false;

  constructor(
    private oauthService: OAuthService,
    private httpClient: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.oauthService.events
      .pipe(filter(e => e.type === 'token_received'))
      .subscribe(() => {
        console.log('Token ricevuto, facendo partire la chiamata...');
        this.userInit();
        this.benvenuto();

      });

    if (this.oauthService.hasValidAccessToken()) {
      console.log('Token già presente, facendo partire la chiamata...');
      this.benvenuto();
      this.userInit();

    }
  }

  benvenuto() {
    const claims: any = this.oauthService.getIdentityClaims();
    if (claims) {
      this.benvenutoText = `Benvenuto, ${claims.given_name || 'Utente'}!`;
    } else {
      this.benvenutoText = 'Benvenuto!';
    }
  }

  userInit() {
    const token = this.oauthService.getAccessToken();
    this.httpClient.get<{ message: string }>('http://localhost:8080/home', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }).subscribe(response => {
      console.log(response);
    });
  }

  logout() {
    this.oauthService.logOut();
  }

  goToShop() { }

  goToMyOrders() { }

  goToProfile() { }

  goToCart() {
    this.router.navigate(['/carrello']);
  }

  goToHome() { }

}