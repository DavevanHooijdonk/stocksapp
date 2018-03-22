import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Stock} from "./domain/stock";
import {Observable} from "rxjs/Observable";
import {StockPage} from "./domain/stockPage";
import {environment} from "../../environments/environment";
import {AuthenticationService} from "./authentication.service";

@Injectable()
export class StocksService {

    private baseUrl: string = environment.api.concat('/stocks');
    private headers;

    constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
    }


    getStocks(href: string): Observable<StockPage> {
        return this.http.get<StockPage>(href, {
            headers: this.getHeaders(),
        })
    }

    createStock(stock: Stock): Observable<Stock> {
        return this.http.post<Stock>(this.baseUrl, stock, {
            headers: this.getHeaders(),
        })
    }

    editStock(stock: Stock): Observable<Stock> {
        return this.http.put<Stock>(this.baseUrl.concat('/', stock.id.toString()), stock, {
            headers: this.getHeaders(),
        })
    }

    private getHeaders(): HttpHeaders {
        if (!this.headers) {
            this.headers = this.authenticationService.getSecurityHeader()
                .append('Accept', 'application/json')
                .append('Content-Type', 'application/json');
        }
        return this.headers
    }
}