import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {StocksDisplayComponent} from "./display/stocks-display.component";
import {StocksLoginComponent} from "./login/stocks-login.component";
import {AuthGuard} from "./service";

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'login'},
    {path: 'stocks', canActivate: [AuthGuard], component: StocksDisplayComponent},
    {path: 'login', component: StocksLoginComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    providers: [AuthGuard],
    exports: [RouterModule]
})
export class AppRoutingModule {
}