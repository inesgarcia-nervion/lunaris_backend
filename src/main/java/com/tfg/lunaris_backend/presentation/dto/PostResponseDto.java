package com.tfg.lunaris_backend.presentation.dto;

import java.util.List;

/**
 * DTO para la respuesta de un post, con formato compatible con el frontend.
 */
public class PostResponseDto {
    private Long id;
    private UserDto user;
    private List<String> imageUrls;
    private String text;
    private int likes;
    private boolean liked;
    private List<CommentDto> comments;

    public PostResponseDto() {
    }

    public PostResponseDto(Long id, UserDto user, List<String> imageUrls, String text, int likes, boolean liked,
            List<CommentDto> comments) {
        this.id = id;
        this.user = user;
        this.imageUrls = imageUrls;
        this.text = text;
        this.likes = likes;
        this.liked = liked;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public UserDto getUser() {
        return user;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public static class UserDto {
        private String name;
        private String avatarUrl;

        public UserDto() {
        }

        public UserDto(String name, String avatarUrl) {
            this.name = name;
            this.avatarUrl = avatarUrl;
        }

        public String getName() {
            return name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }
    }

    public static class CommentDto {
        private Long id;
        private UserDto user;
        private String text;

        public CommentDto() {
        }

        public CommentDto(Long id, UserDto user, String text) {
            this.id = id;
            this.user = user;
            this.text = text;
        }

        public Long getId() {
            return id;
        }

        public UserDto getUser() {
            return user;
        }

        public String getText() {
            return text;
        }
    }
}
