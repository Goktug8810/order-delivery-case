package com.goktug.order_delivery.controller;

import com.goktug.order_delivery.dto.DeliveredOrder;
import com.goktug.order_delivery.exception.InvalidOrderDateException;
import com.goktug.order_delivery.service.OrderDeliveryProducer;
import com.goktug.order_delivery.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderDeliveryProducer orderDeliveryProducer;


    public OrderController(OrderService orderService,
                           OrderDeliveryProducer orderDeliveryProducer) {
        this.orderService = orderService;
        this.orderDeliveryProducer = orderDeliveryProducer;
    }

    @GetMapping("/process/{date}")
    public List<DeliveredOrder> processOrders(@PathVariable("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<DeliveredOrder> deliveredOrders = orderService.processDeliveredOrders(date);
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new InvalidOrderDateException("Invalid date format. Please use 'yyyy-MM-dd'.");
        }
        return deliveredOrders;
    }
}

