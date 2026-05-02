package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tfg.lunaris_backend.data.repository.NewsRepository;
import com.tfg.lunaris_backend.domain.model.News;

import java.util.List;

/**
 * Servicio que gestiona la lógica de negocio de las noticias.
 */
@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public List<News> getAll() {
        return newsRepository.findAllByOrderByIdDesc();
    }

    public News getById(Long id) {
        return newsRepository.findById(id).orElse(null);
    }

    public News create(News news) {
        return newsRepository.save(news);
    }

    public void delete(Long id) {
        newsRepository.deleteById(id);
    }
}
