package com.virality.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViralityEngineService {
    private final StringRedisTemplate redisTemplate;

    private static final String VIRALITY_SCORE_KEY_PREFIX = "post:";
    private static final String VIRALITY_SCORE_KEY_SUFFIX = ":virality_score";

    public void incrementScore(Long postId, InteractionType type) {
        String key = VIRALITY_SCORE_KEY_PREFIX + postId + VIRALITY_SCORE_KEY_SUFFIX;
        long incrementValue = type.getScore();
        
        Long newScore = redisTemplate.opsForValue().increment(key, incrementValue);
        log.info("Incremented virality score for post {} by {}. New score: {}", postId, incrementValue, newScore);
    }

    public enum InteractionType {
        BOT_REPLY(1),
        HUMAN_LIKE(20),
        HUMAN_COMMENT(50);

        private final int score;

        InteractionType(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
