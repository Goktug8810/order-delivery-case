package com.goktug.order_delivery.service;

import com.goktug.order_delivery.dto.DeliveredOrder;
import com.goktug.order_delivery.dto.event.OrderDeliveryStatistics;
import com.goktug.order_delivery.entity.OrderEntity;
import com.goktug.order_delivery.exception.OrderNotFoundException;
import com.goktug.order_delivery.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDeliveryProducer orderDeliveryProducer;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderService(OrderRepository orderRepository, OrderDeliveryProducer orderDeliveryProducer) {
        this.orderRepository = orderRepository;
        this.orderDeliveryProducer = orderDeliveryProducer;
    }


    //Teslim edilmiş siparişleri işleme-DTO'ya çevirme-Kafkaya event publish

    public List<DeliveredOrder> processDeliveredOrders(LocalDate date) {
        List<OrderEntity> deliveredOrders = orderRepository.findDeliveredOrdersByDate(date);

        if (deliveredOrders.isEmpty()) {
            throw new OrderNotFoundException("Belirtilen tarihte teslim edilmiş sipariş bulunamadı: " + date);
        }

        List<DeliveredOrder> convertedOrders = deliveredOrders.stream()
                .map(this::convertToDeliveredOrder)
                .collect(Collectors.toList());

        convertedOrders.forEach(this::publishOrderDeliveryEvent);

        return convertedOrders;
    }

    //OrderEntity -> DeliveredOrder dönüşümü
    private DeliveredOrder convertToDeliveredOrder(OrderEntity entity) {
        Integer collectionDuration = computeDuration(entity.getCollectionStartedAt(), entity.getCollectedAt());
        Integer deliveryDuration = computeDuration(entity.getDeliveryStartedAt(), entity.getDeliveredAt());
        Integer leadTime = computeDuration(entity.getCreatedAt(), entity.getDeliveredAt());

        boolean orderInTime = isOrderInTime(entity.getEta(), leadTime);

        return DeliveredOrder.builder()
                .id(entity.getId())
                .createdAt(formatDateTime(entity.getCreatedAt()))
                .lastUpdatedAt(formatDateTime(entity.getLastUpdatedAt()))
                .collectionDuration(collectionDuration)
                .deliveryDuration(deliveryDuration)
                .eta(entity.getEta())
                .leadTime(leadTime)
                .orderInTime(orderInTime)
                .build();
    }


    //Kafka'ya `OrderDeliveryStatistics` event gönderimi
    private void publishOrderDeliveryEvent(DeliveredOrder order) {
        OrderDeliveryStatistics event = OrderDeliveryStatistics.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .lastUpdatedAt(order.getLastUpdatedAt())
                .collectionDuration(order.getCollectionDuration())
                .deliveryDuration(order.getDeliveryDuration())
                .eta(order.getEta())
                .leadTime(order.getLeadTime())
                .orderInTime(order.getOrderInTime())
                .eventTimestamp(LocalDateTime.now().format(formatter))
                .build();

        orderDeliveryProducer.sendOrderDeliveryEvent(event);
    }


    //İki zaman damgası arasındaki süreyi dakika cinsinden hesaplama.
    private Integer computeDuration(LocalDateTime start, LocalDateTime end) {
        return (start == null || end == null) ? null : (int) Duration.between(start, end).toMinutes();
    }


    //LocalDateTime nesnesini "yyyy-MM-dd HH:mm:ss" formatına çevirme işlemi.
    private String formatDateTime(LocalDateTime dt) {
        return (dt == null) ? null : dt.format(formatter);
    }


    //Sipariş ETA içerisinde teslim edilmiş mi kontrolü (eta >= lead time)
    private boolean isOrderInTime(Integer eta, Integer leadTime) {
        if (leadTime == null || eta == null || leadTime < 0 || eta < 0) {
            return false;
        }
        return leadTime <= eta;
    }

}
