package com.virality.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddCommentRequest {
    private Long authorId;
    private Long targetCommentId; // optional, if null then it's top-level
    private String content;
}
