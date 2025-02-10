package com.goktug.order_delivery.dto.event;

import lombok.*;

@Getter
@Builder
@ToString
public class OrderDeliveryStatistics {
    private final Long orderId;
    private final String createdAt;
    private final String lastUpdatedAt;
    private final Integer collectionDuration;
    private final Integer deliveryDuration;
    private final Integer eta;
    private final Integer leadTime;
    private final Boolean orderInTime;
    private final String eventTimestamp;
}
