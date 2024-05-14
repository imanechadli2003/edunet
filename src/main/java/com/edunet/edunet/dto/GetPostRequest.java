package com.edunet.edunet.dto;

import java.time.LocalDateTime;

public record GetPostRequest(
        int id,
        String topicName,
        String author,
        String content,
        LocalDateTime createdOn,
        int upVotes,
        int downVotes,
        int comments
) {
}
