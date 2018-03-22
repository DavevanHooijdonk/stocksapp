package com.brocoding.stocks.backend.data;

import com.brocoding.stocks.backend.domain.Stock;
import com.brocoding.stocks.backend.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;
import static org.springframework.util.ResourceUtils.getFile;

/**
 * Created by Dave van Hooijdonk on 14-3-2018.
 */
@Component
public class StocksInitializer {

    private static final Logger logger = LoggerFactory.getLogger(StocksInitializer.class);

    private final String fileName;
    private final StockService stockService;
    private final ObjectReader objectReader;

    public StocksInitializer(@Value("${stockapp.startupstocks.filename}") final String filename,
                             final StockService stockService,
                             final ObjectMapper objectMapper) {
        this.fileName = filename;
        this.stockService = stockService;
        this.objectReader = objectMapper.readerFor(Stock.Builder[].class);
    }

    public void loadStocksFromJson() {
        try {
            Arrays.<Stock.Builder>asList(objectReader.readValue(getFile(CLASSPATH_URL_PREFIX.concat(fileName).concat(".json"))))
                    .forEach(stockService::createStock);
        } catch (final IOException e) {
            logger.error("Unable to initialize initial stocks due to: {}", e.getMessage());
        }
    }
}
