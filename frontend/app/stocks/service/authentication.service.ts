import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import 'rxjs/add/operator/map'
import {environment} from "../../environments/environment";
import {Principal} from "./domain/principal";
import {Router} from "@angular/router";

@Injectable()
export class AuthenticationService {

    constructor(private http: HttpClient,
                private router: Router) {
    }

    private headers: HttpHeaders;
    private authenticationUrl: string = environment.api.concat('/authenticate');
    private authenticated: boolean;
    private admin: boolean;

    login(username: string, password: string) {
        let basicAuthentication = "Basic " + btoa(username.concat(':', password));
        let headers = new HttpHeaders()
            .append("Authorization", basicAuthentication);
        return this.http.get<Principal>(this.authenticationUrl,
            {headers: headers})
            .subscribe(
                data => {
                    this.authenticated = true;
                    this.headers = headers;
                    this.admin = data.authorities.filter(auth => auth.authority == 'ROLE_ADMIN').length > 0;
                    this.router.navigateByUrl('/stocks');
                },
                error => {
                    console.log(error);
                    this.authenticated = false;
                }
            );
    }

    isAuthenticated(): boolean {
        return this.authenticated
    }

    getSecurityHeader(): HttpHeaders {
        return this.headers;
    }

    isAdmin(): boolean {
        return this.admin;
    }

    logout() {
        this.authenticated = false;
        this.admin = false;
        this.router.navigate(['/login']);
    }
}