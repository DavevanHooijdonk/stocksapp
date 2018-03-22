export class Price {
    constructor(amount: number, currency: string) {
        this.amount = amount;
        this.currency = currency;
    }

    amount: number;
    currency: string;
}