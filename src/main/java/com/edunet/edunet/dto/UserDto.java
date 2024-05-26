    package com.edunet.edunet.dto;

import java.time.LocalDate;

public record UserDto(
        long id,
        String firstName,
        String lastName,
        String handle,
        String email,
        String country,
        String gender,
        String Branch,
        String title,
        LocalDate createdOn
) {}
