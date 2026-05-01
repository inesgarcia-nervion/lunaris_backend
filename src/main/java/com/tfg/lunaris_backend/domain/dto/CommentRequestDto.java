package com.tfg.lunaris_backend.domain.dto;

/**
 * DTO para crear un comentario en un post.
 */
public class CommentRequestDto {
    private String text;
    private String userAvatarUrl;

    public CommentRequestDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
