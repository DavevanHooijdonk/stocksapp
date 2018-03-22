import {Component} from '@angular/core';
import {AuthenticationService} from "../service";

@Component({
    selector: 'app-login',
    templateUrl: 'stocks-login.component.html',
    styleUrls: ['./stocks-login.component.css']
})
export class StocksLoginComponent {

    credentials = {username: '', password: ''};

    constructor(private authenticationService: AuthenticationService) {
    }

    onSubmit() {
        this.authenticationService.login(this.credentials.username, this.credentials.password);
    }
}