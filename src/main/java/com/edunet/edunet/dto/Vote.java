package com.edunet.edunet.dto;

public record Vote(int v) {

    public Vote {
        if (v != 1 && v != -1) {
            throw new IllegalArgumentException("Vote must be 1 or -1");
        }
    }
}
