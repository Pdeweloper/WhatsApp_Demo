package com.virality.service;

import com.virality.entity.Comment;
import com.virality.entity.Post;
import com.virality.repository.BotRepository;
import com.virality.repository.CommentRepository;
import com.virality.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final RedisGuardrailService redisGuardrailService;
    private final ViralityEngineService viralityEngineService;
    private final BotRepository botRepository;
    private final NotificationService notificationService;

    @Transactional
    public Comment addComment(Long postId, Long authorId, String content, Long targetCommentId) {
        Post post = postService.getPost(postId);

        int depthLevel = 0;
        Long targetAuthorId = post.getAuthorId();

        if (targetCommentId != null) {
            Comment targetComment = commentRepository.findById(targetCommentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Target comment not found"));
            depthLevel = targetComment.getDepthLevel() + 1;
            targetAuthorId = targetComment.getAuthorId();
        }

        // 1. Guardrail checks
        redisGuardrailService.validateVerticalCap(depthLevel);

        boolean isBot = botRepository.existsById(authorId);

        if (isBot) {
            // Check Caps
            redisGuardrailService.validateAndIncrementBotHorizontalCap(postId);
            // It makes sense to guardrail the interaction with targetAuthor if it's a bot
            // interacting with human
            // Assuming targetAuthorId refers to human interactions
            redisGuardrailService.validateAndApplyCooldown(authorId, targetAuthorId);

            // Increment virality
            viralityEngineService.incrementScore(postId, ViralityEngineService.InteractionType.BOT_REPLY);
            notificationService.pushNotification(targetAuthorId, "Bot replied to your content on post " + postId);
        } else {
            viralityEngineService.incrementScore(postId, ViralityEngineService.InteractionType.HUMAN_COMMENT);
            notificationService.pushNotification(targetAuthorId, "Human replied to your content on post " + postId);
        }

        // 2. Final Persistence
        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .content(content)
                .depthLevel(depthLevel)
                .build();

        return commentRepository.save(comment);
    }
}
