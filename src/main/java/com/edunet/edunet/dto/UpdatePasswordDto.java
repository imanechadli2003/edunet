package com.edunet.edunet.dto;

public record UpdatePasswordDto(
        String oldPassword,
        String newPassword,
        String confirmPassword
) { }
