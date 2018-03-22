import {Price} from "./price";

export class Stock {
    constructor(name: string, currentPrice: Price, id?: number, lastUpdate?: Date) {
        this.name = name;
        this.currentPrice = currentPrice;
        this.id = id;
        this.lastUpdate = lastUpdate;
    }

    name: string;
    currentPrice: Price;
    id: number;
    lastUpdate: Date;
}