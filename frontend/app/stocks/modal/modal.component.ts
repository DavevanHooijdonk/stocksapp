import {Component, OnChanges, OnInit} from '@angular/core';
import {BsModalRef} from "ngx-bootstrap";
import {Stock} from "../service/domain/stock";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Price} from "../service/domain/price";
import {Subscription} from "rxjs/src/Subscription";

@Component({
    selector: 'app-modal-content',
    templateUrl: 'modal.component.html'
})

export class ModalComponent implements OnInit, OnChanges {
    title: string;
    stock: Stock;
    stockForm: FormGroup;
    execute: (Stock) => Subscription;

    constructor(public bsModalRef: BsModalRef,
                private fb: FormBuilder) {
    }

    ngOnInit() {
        this.createForm()
    }

    ngOnChanges() {
        this.rebuildForm();
    }

    createForm() {
        if (this.stock) {
            this.stockForm = this.fb.group({
                name: this.stock.name,
                amount: this.stock.currentPrice.amount,
                currency: this.stock.currentPrice.currency
            })
        } else {
            this.stockForm = this.fb.group(
                {
                    name: '',
                    amount: 0,
                    currency: ''
                }
            )
        }
    }

    rebuildForm() {
        this.stockForm.reset({
            name: this.stock.name,
            amount: this.stock.currentPrice.amount,
            currency: this.stock.currentPrice.currency
        });
    }

    onSubmit() {
        let editedStock = this.prepareSaveStock();
        this.execute(editedStock);
        this.bsModalRef.hide()
    }

    prepareSaveStock(): Stock {
        const formModel = this.stockForm.value;
        return {
            id: this.stock ? this.stock.id : undefined,
            name: formModel.name as string,
            currentPrice: new Price(
                formModel.amount as number,
                formModel.currency as string
            ),
            lastUpdate: undefined
        };
    }

    revert() {
        this.rebuildForm();
    }

}