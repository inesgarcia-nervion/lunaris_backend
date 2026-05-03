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

    /**
     * Obtiene todas las noticias ordenadas por ID de forma descendente.
     * 
     * @return Lista de noticias ordenadas por ID descendente.
     */
    public List<News> getAll() {
        return newsRepository.findAllByOrderByIdDesc();
    }

    /**
     * Obtiene una noticia por su ID.
     * 
     * @param id ID de la noticia a obtener.
     * @return La noticia con el ID especificado, o null si no se encuentra.
     */
    public News getById(Long id) {
        return newsRepository.findById(id).orElse(null);
    }

    /**
     * Crea una nueva noticia.
     * 
     * @param news Objeto de tipo News que contiene la información de la noticia a
     *             crear.
     * @return La noticia creada con su ID asignado.
     */
    public News create(News news) {
        return newsRepository.save(news);
    }

    /**
     * Elimina una noticia por su ID.
     * 
     * @param id ID de la noticia a eliminar.
     */
    public void delete(Long id) {
        newsRepository.deleteById(id);
    }
}
