package com.edunet.edunet.dto;

import com.edunet.edunet.models.User;

public record CreateUserRequest(
        String firstName,
        String lastName,
        int gender,
        String branch,
        String title,
        String email,
        String password,
        String country
) {}
