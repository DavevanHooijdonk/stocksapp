import {Stock} from "./stock";
import {Link} from "./link";

export class StockPage {
    numberOfElements: number;
    totalElements: number;
    totalPages: number;
    _embedded: { stockList: Array<Stock> };
    _links: {
        self?: Link,
        first?: Link,
        prev?: Link,
        next?: Link,
        last?: Link,
    }
}