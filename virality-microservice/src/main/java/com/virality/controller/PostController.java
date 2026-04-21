package com.virality.controller;

import com.virality.dto.AddCommentRequest;
import com.virality.dto.CreatePostRequest;
import com.virality.dto.LikeRequest;
import com.virality.entity.Comment;
import com.virality.entity.Post;
import com.virality.service.CommentService;
import com.virality.service.LikeService;
import com.virality.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        Post post = postService.createPost(request.getAuthorId(), request.getContent());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable("postId") Long postId,
            @RequestBody AddCommentRequest request) {
        Comment comment = commentService.addComment(postId, request.getAuthorId(), request.getContent(),
                request.getTargetCommentId());
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable("postId") Long postId,
            @RequestBody LikeRequest request) {
        likeService.likePost(postId, request.getAuthorId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
