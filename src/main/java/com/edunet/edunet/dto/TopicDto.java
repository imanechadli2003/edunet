package com.edunet.edunet.dto;

import java.time.LocalDate;

public record TopicDto(
        int id,
        String name,
        String description,
        String privacy,
        String owner,
        LocalDate createdOn,
        String membership
) {}
