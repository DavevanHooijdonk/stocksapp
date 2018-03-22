package com.brocoding.stocks.backend;

import com.brocoding.stocks.backend.data.StocksInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by Dave van Hooijdonk on 12-3-2018.
 */
@SpringBootApplication
public class StocksApplication {

    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(StocksApplication.class);
        context.getBean(StocksInitializer.class).loadStocksFromJson();
    }

}
