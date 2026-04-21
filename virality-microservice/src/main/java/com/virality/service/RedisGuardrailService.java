package com.virality.service;

import com.virality.exception.RateLimitExceededException;
import com.virality.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGuardrailService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Vertical Cap: Depth level must be <= 20
     */
    public void validateVerticalCap(int depthLevel) {
        if (depthLevel > 20) {
            throw new ValidationException("Comment depth level cannot exceed 20");
        }
    }

    /**
     * Horizontal Cap: Only 100 bot comments allowed per post. (Atomic increment)
     */
    public void validateAndIncrementBotHorizontalCap(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount != null && currentCount > 100) {
            // Revert increment since it's rejected
            redisTemplate.opsForValue().decrement(key);
            throw new RateLimitExceededException("Horizontal Cap: Bot limit (100) reached for post " + postId);
        }
    }

    /**
     * Cooldown Cap: If bot interacts with a human, it can't interact again for 10 minutes.
     * Uses SETNX (setIfAbsent) combined with a TTL.
     */
    public void validateAndApplyCooldown(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofMinutes(10));
        
        if (Boolean.FALSE.equals(isSet)) {
            throw new RateLimitExceededException("Cooldown Cap: Bot " + botId + " is on cooldown for user " + humanId);
        }
    }
}
