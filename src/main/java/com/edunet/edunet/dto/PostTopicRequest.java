package com.edunet.edunet.dto;

public record PostTopicRequest(
        String name,
        String description,
        int privacy
) {}
