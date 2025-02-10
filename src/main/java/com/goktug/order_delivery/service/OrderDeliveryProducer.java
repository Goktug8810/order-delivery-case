package com.goktug.order_delivery.service;


import com.goktug.order_delivery.dto.event.OrderDeliveryStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class OrderDeliveryProducer {

    private final KafkaTemplate<String, OrderDeliveryStatistics> kafkaTemplate;
    private static final String ORDER_DELIVERY_STATISTICS = "order_delivery_statistics";

    public OrderDeliveryProducer(KafkaTemplate<String, OrderDeliveryStatistics> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderDeliveryEvent(OrderDeliveryStatistics event) {
        CompletableFuture<SendResult<String, OrderDeliveryStatistics>> future =
                kafkaTemplate.send(ORDER_DELIVERY_STATISTICS, event.getOrderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Event gönderilemedi: {}", event.getOrderId(), ex);
            }
        });
    }
}

