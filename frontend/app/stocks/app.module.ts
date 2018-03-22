import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {BsDropdownModule, ModalModule} from 'ngx-bootstrap';

import {AppComponent} from './app.component';
import {StocksDisplayComponent} from "./display/stocks-display.component";
import {StocksLoginComponent} from "./login/stocks-login.component";
import {AppRoutingModule} from "./app.routing.module";
import {ModalComponent} from "./modal/modal.component";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AuthenticationService} from "./service";

@NgModule({
    declarations: [
        AppComponent,
        StocksDisplayComponent,
        StocksLoginComponent,
        ModalComponent
    ],
    imports: [
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        BrowserModule,
        HttpClientModule,
        BsDropdownModule.forRoot(),
        ModalModule.forRoot()
    ],
    providers: [AuthenticationService],
    bootstrap: [AppComponent],
    entryComponents: [
        ModalComponent
    ]
})
export class AppModule {
}
