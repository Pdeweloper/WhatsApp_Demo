package com.virality.service;

import com.virality.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final ViralityEngineService viralityEngineService;
    private final PostService postService;
    private final NotificationService notificationService;

    @Transactional
    public void likePost(Long postId, Long authorId) {
        Post post = postService.getPost(postId);

        viralityEngineService.incrementScore(postId, ViralityEngineService.InteractionType.HUMAN_LIKE);
        notificationService.pushNotification(post.getAuthorId(), "User " + authorId + " liked your post " + postId);
    }
}
