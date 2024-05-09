package com.edunet.edunet.dto;

import java.time.LocalDate;

public record GetTopicRequest(
        String name,
        String description,
        String privacy,
        String owner,
        LocalDate createdOn
) {}
