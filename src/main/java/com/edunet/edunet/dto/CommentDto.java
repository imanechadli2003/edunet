package com.edunet.edunet.dto;

public record CommentDto(int id, int postId, long authorId, String handle, String comment) {
}
