package com.brocoding.stocks.backend.service;

import com.brocoding.stocks.backend.domain.Stock;

import java.util.List;
import java.util.Optional;

/**
 * Created by Dave van Hooijdonk on 12-3-2018.
 */
public interface StockService {

    /**
     * @return List of {@link Stock}
     */
    List<Stock> fetchStocks();

    /**
     * @param id of the stock to be fetched
     * @return requested {@link Stock} if present, else an empty optional
     */
    Optional<Stock> fetchStock(final Integer id);

    /**
     * @param id           of the stock to be updated
     * @param stockBuilder containing variables to mergeFromReference
     * @return requested {@link Stock} if present, otherwise an empty optional
     */
    Optional<Stock> updateStock(final Integer id, final Stock.Builder stockBuilder);

    /**
     * @param stockBuilder containing stock variables to create a stock
     * @return created {@link Stock} if present, otherwise an empty optional
     */
    Stock createStock(final Stock.Builder stockBuilder);

}
