import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: "carrello",
        loadComponent: () => import('./carrello/carrello.component').then((c) => c.CarrelloComponent)
    },
    {
        path: "",
        loadComponent: () => import('./home/home.component').then((c) => c.HomeComponent)
    },
    {
        path: "ordini",
        loadComponent: () => import('./ordini/ordini.component').then((c) => c.OrdiniComponent)
    },
    {
        path: "profile",
        loadComponent: () => import('./profile/profile.component').then((c) => c.ProfileComponent)
    },
    {
        path: "add-product",
        loadComponent: () => import('./add-product/add-product.component').then((c) => c.AddProductComponent)
    },
    { path: '**', redirectTo: '' },
];