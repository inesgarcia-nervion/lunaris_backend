package com.tfg.lunaris_backend.presentation.dto;

import java.util.List;

/**
 * DTO para crear o actualizar un post.
 */
public class PostRequestDto {
    private String text;
    private List<String> imageUrls;
    private String userAvatarUrl;

    public PostRequestDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
}
