package com.edunet.edunet.dto;

public record UserSearchQuery(
        String firstName,
        String lastName,
        String branch,
        String title
) {}
