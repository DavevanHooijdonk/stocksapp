package com.brocoding.stocks.backend.controller;

import com.brocoding.stocks.backend.domain.EnhancedResources;
import com.brocoding.stocks.backend.domain.Stock;
import com.brocoding.stocks.backend.service.StockService;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Dave van Hooijdonk on 12-3-2018.
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/stocks", produces = APPLICATION_JSON_VALUE)
public class StockController {

    private static final ResponseEntity<Resource<Stock>> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * @param id the Identifier of the requested stock
     * @return 200 with the requested {@link Stock} if present else 404, "Not Found"
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource<Stock>> getStock(@PathVariable final Integer id) {
        return stockService.fetchStock(id)
                .map(stock -> new Resource<>(stock, linkTo(methodOn(StockController.class).getStock(id)).withSelfRel()))
                .map(resource -> new ResponseEntity<>(resource, HttpStatus.OK))
                .orElse(NOT_FOUND);
    }

    /**
     * @param page     number of the page requested
     * @param pageSize maximum amount of stocks per page
     * @return a list of {@link Stock} currently stored for the page else 404, "Not Found"
     */
    @GetMapping
    public ResponseEntity getStocks(@RequestParam final int page,
                                    @RequestParam final int pageSize) {
        final List<Stock> allStocks = stockService.fetchStocks();
        final List<Stock> stocks = allStocks.stream()
                .skip(max(0L, page - 1L) * pageSize)
                .limit(pageSize)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
        if (stocks.isEmpty()) {
            return NOT_FOUND;
        } else {
            final Resources<Stock> resources = formResources(stocks, page, pageSize, allStocks.size());
            return new ResponseEntity<>(resources, HttpStatus.OK);
        }
    }

    /**
     * @param id           of the stock to mergeFromReference
     * @param stockBuilder a builder for a new stock to be stored
     * @return 200 with the requested {@link Stock} if present else 404, "Not Found"
     */
    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<Stock>> putStock(@PathVariable final Integer id,
                                                    @RequestBody final Stock.Builder stockBuilder) {
        return stockService.updateStock(id, stockBuilder)
                .map(stock -> new Resource<>(stock, linkTo(methodOn(StockController.class).getStock(id)).withSelfRel()))
                .map(resource -> new ResponseEntity<>(resource, HttpStatus.OK))
                .orElse(NOT_FOUND);
    }

    /**
     * @param stockBuilder a builder for a new stock to be stored
     * @return the requested {@link Stock} by Id if present
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<Stock>> postStock(@RequestBody final Stock.Builder stockBuilder, final UriComponentsBuilder ucBuilder) {
        final Stock stock = stockService.createStock(stockBuilder);
        final Integer id = stock.getId();
        final Link self = linkTo(methodOn(StockController.class).getStock(id)).withSelfRel();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/stocks/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<>(new Resource<>(stock, self), headers, HttpStatus.CREATED);
    }

    private Resources<Stock> formResources(final List<Stock> stocks, final int page, final int pageSize, final int max) {
        final List<Link> links = new ArrayList<>();
        final int maxPages = max / pageSize + Math.min(max % pageSize, 1);
        links.add(linkTo(methodOn(StockController.class).getStocks(page, pageSize)).withSelfRel());
        links.add(linkTo(methodOn(StockController.class).getStocks(1, pageSize)).withRel("first"));
        links.add(linkTo(methodOn(StockController.class).getStocks(maxPages, pageSize)).withRel("last"));
        if (page > 1) {
            links.add(linkTo(methodOn(StockController.class).getStocks(page - 1, pageSize)).withRel("prev"));
        }
        if (page < maxPages) {
            links.add(linkTo(methodOn(StockController.class).getStocks(page + 1, pageSize)).withRel("next"));
        }
        return new EnhancedResources<>(stocks, unmodifiableList(links), maxPages, stocks.size(), max);
    }
}
