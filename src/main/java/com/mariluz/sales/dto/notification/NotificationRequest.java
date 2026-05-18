package com.mariluz.sales.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NotificationRequest {

    private String email;
    private String title;
    private String body;
}
