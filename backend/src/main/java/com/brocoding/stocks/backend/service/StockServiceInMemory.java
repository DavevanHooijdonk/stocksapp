package com.brocoding.stocks.backend.service;

import com.brocoding.stocks.backend.domain.Stock;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Created by Dave van Hooijdonk on 13-3-2018.
 */
@Service
public class StockServiceInMemory implements StockService {

    private final AtomicInteger indexCounter = new AtomicInteger();
    private final ConcurrentMap<Integer, Stock> idsToStocks = new ConcurrentHashMap<>();

    @Override
    public List<Stock> fetchStocks() {
        return idsToStocks.values().stream()
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public Optional<Stock> fetchStock(final Integer id) {
        return ofNullable(idsToStocks.get(id));
    }

    @Override
    public Optional<Stock> updateStock(final Integer id, final Stock.Builder stockBuilder) {
        return ofNullable(idsToStocks.computeIfPresent(id, (key, value) -> stockBuilder.mergeFromReference(value).build()));
    }

    @Override
    public Stock createStock(final Stock.Builder stockBuilder) {
        final Integer index = indexCounter.addAndGet(1);
        final Stock stock = stockBuilder.setId(index).build();
        idsToStocks.putIfAbsent(index, stock);
        return stock;
    }
}
