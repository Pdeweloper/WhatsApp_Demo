package com.virality.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Notification Engine Throttler
     * Key: notif:user:{id} -> used for 15-minute cooldown.
     * List: user:{id}:pending_notifs -> stores pending text messages.
     */
    public void pushNotification(Long userId, String message) {
        String cooldownKey = "notif:user:" + userId;
        String pendingListKey = "user:" + userId + ":pending_notifs";

        Boolean isCooldownActive = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(isCooldownActive)) {
            // Push message into pending notifications list
            redisTemplate.opsForList().rightPush(pendingListKey, message);
        } else {
            // No recent notification, sent immediately. Set cooldown.
            log.info("Push Notification Sent to User {}: {}", userId, message);
            redisTemplate.opsForValue().set(cooldownKey, "sent", Duration.ofMinutes(15));
            // Just outputting to log, conceptually actual push goes here
        }
    }

    /**
     * Run every 5 minutes using @Scheduled.
     * Scans keys user:*:pending_notifs, fetches messages, prints summary, clears
     * list.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void processPendingNotifications() {
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String userIdStr = key.split(":")[1];
            List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
            if (messages != null && !messages.isEmpty()) {
                // Determine a simple summary based on messages count (this satisfies simple
                // summary logic)
                log.info("Summarized Push Notification: Bot or users interacted {} times with User {}", messages.size(),
                        userIdStr);
                // Clear list
                redisTemplate.delete(key);
            }
        }
    }
}
