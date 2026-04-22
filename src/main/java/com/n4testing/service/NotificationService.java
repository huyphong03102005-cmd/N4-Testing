package com.n4testing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi tín hiệu thông báo cho frontend rằng dữ liệu dashboard cần được cập nhật.
     */
    public void broadcastUpdate() {
        messagingTemplate.convertAndSend("/topic/dashboard-update", "update");
    }
}
