package com.edunet.edunet.dto;

public record CreateTopicDto(
        String name,
        String description,
        int privacy
) {}
