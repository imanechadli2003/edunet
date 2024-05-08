package com.edunet.edunet.dto;

public record UpdatePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmPassword
) { }
