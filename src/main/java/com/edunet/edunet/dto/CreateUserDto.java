package com.edunet.edunet.dto;

public record CreateUserDto(
        String firstName,
        String lastName,
        String handle,
        int gender,
        String branch,
        String title,
        String email,
        String password,
        String country
) {}
