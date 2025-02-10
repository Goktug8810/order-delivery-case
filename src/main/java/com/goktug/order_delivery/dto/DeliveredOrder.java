package com.goktug.order_delivery.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeliveredOrder {
    private final Long id;
    private final String createdAt;
    private final String lastUpdatedAt;
    private final Integer collectionDuration;
    private final Integer deliveryDuration;
    private final Integer eta;
    private final Integer leadTime;
    private final Boolean orderInTime;
}

