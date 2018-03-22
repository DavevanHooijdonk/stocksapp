import {Component, OnInit} from '@angular/core';
import {AuthenticationService, StocksService} from "../service";
import {StockPage} from "../service/domain/stockPage";
import {HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {BsModalRef} from "ngx-bootstrap/modal/bs-modal-ref.service";
import {ModalComponent} from "../modal/modal.component";
import {BsModalService} from "ngx-bootstrap/modal";
import {Stock} from "../service/domain/stock";

@Component({
    selector: 'app-stocks-display',
    templateUrl: './stocks-display.component.html',
    styleUrls: ['./stocks-display.component.css'],
    providers: [StocksService]
})
export class StocksDisplayComponent implements OnInit {

    private baseUrl: string = environment.api.concat('/stocks?');

    admin: boolean;
    stockPage: StockPage;
    page: number = 1;
    pageSize: number = 20;
    pageNumbers: Array<number>;
    bsModalRef: BsModalRef;


    constructor(private stocksService: StocksService,
                private modalService: BsModalService,
                private authenticationService: AuthenticationService) {
    }

    ngOnInit() {
        this.admin = this.authenticationService.isAdmin();
        this.retrieveStocks({});
        setInterval(() => {
            this.retrieveStocks({});
        }, 10000);
    }

    retrieveStocks({href = this.generateHref()}: { href?: string }) {
        this.stocksService.getStocks(href)
            .subscribe(
                data => {
                    this.stockPage = data;
                    this.pageNumbers = Array(data.totalPages).fill(0).map((x, i) => i + 1)
                },
                error => console.log(error)
            )
    }

    setPageSize(pageSize: number) {
        this.pageSize = pageSize;
        this.page = 1;
        this.retrieveStocks({})
    }

    setPage(page: number) {
        this.page = page;
        this.retrieveStocks({})
    }

    firstPage() {
        this.page = 1;
        this.retrieveStocks({href: this.stockPage._links.first.href})
    }

    prevPage() {
        this.page++;
        this.retrieveStocks({href: this.stockPage._links.prev.href});
    }

    nextPage() {
        this.page++;
        this.retrieveStocks({href: this.stockPage._links.next.href});
    }

    lastPage() {
        this.page = this.stockPage.totalPages;
        this.retrieveStocks({href: this.stockPage._links.last.href});
    }

    private generateHref(): string {
        return this.baseUrl.concat(new HttpParams()
            .append('page', this.page.toString())
            .append('pageSize', this.pageSize.toString()).toString())
    }

    edit(stock: Stock) {
        const initialState = {
            title: 'Edit Stock: '.concat(stock.id.toString()),
            stock: stock,
            execute: _stock => this.stocksService.editStock(_stock).subscribe(() => this.retrieveStocks({}),
                error => console.log(error))
        };
        this.bsModalRef = this.modalService.show(ModalComponent, {initialState});
    }

    add() {
        const initialState = {
            title: 'Create new Stock',
            execute: stock => this.stocksService.createStock(stock).subscribe(() => this.lastPage(),
                error => console.log(error))
        };
        this.bsModalRef = this.modalService.show(ModalComponent, {initialState});
    }

    logout() {
        this.authenticationService.logout()
    }
}