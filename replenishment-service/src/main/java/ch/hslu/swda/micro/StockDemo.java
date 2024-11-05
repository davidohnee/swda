package ch.hslu.swda.micro;

import ch.hslu.swda.stock.api.Stock;
import ch.hslu.swda.stock.api.StockFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockDemo {
    public static void main(String[] args) {
        var LOG = LoggerFactory.getLogger(StockDemo.class);

        int artikel = 111222;
        Stock stock = StockFactory.getStock();
        int count = stock.getItemCount(111222);
        LOG.info("Bestand von Artikel '{}': {} Stück", 111222, count);
        int ordered = stock.orderItem(111222, 30);
        LOG.info("Bestellung von Artikel '{}': {} Stück.", 111222, ordered);
        LOG.info("Bestand von Artikel '{}': {} Stück", 111222, stock.getItemCount(111222));
        String ticket = stock.reserveItem(111222, 20);
        LOG.info("Reservation von Artikel '{}': 20 Stück, Ticket: {}", 111222, ticket);
        stock.orderReservation(ticket);
        LOG.info("Bestellung von Reservation '{}'.", ticket);
        LOG.info("Bestand von Artikel '{}': {} Stück", 111222, stock.getItemCount(111222));
        ticket = stock.reserveItem(111222, 10);
        LOG.info("Reservation von Artikel '{}': 10 Stück, Ticket: {}", 111222, ticket);
        LOG.info("Bestand von Artikel '{}': {} Stück", 111222, stock.getItemCount(111222));
        ticket = stock.freeReservation(ticket);
        LOG.info("Reservation '{}' freigeben.", ticket);
        LOG.info("Bestand von Artikel '{}': {} Stück", 111222, stock.getItemCount(111222));
        ordered = stock.orderItem(111222, 100);
        LOG.info("Bestellung von Artikel '{}': {} Stück.", 111222, ordered);
        LOG.info("Lieferdatum von Artikel '{}': {}", 111222, stock.getItemDeliveryDate(111222));
    }
}
