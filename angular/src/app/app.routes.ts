import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: "carrello",
        loadComponent: () => import('./carrello/carrello.component').then((c) => c.CarrelloComponent)
    },
    {
        path: "",
        loadComponent: () => import('./home/home.component').then((c) => c.HomeComponent)
    }
];
