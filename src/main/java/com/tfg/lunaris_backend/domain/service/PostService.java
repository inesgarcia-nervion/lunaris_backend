package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tfg.lunaris_backend.data.repository.PostRepository;
import com.tfg.lunaris_backend.domain.model.Post;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByIdDesc();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
