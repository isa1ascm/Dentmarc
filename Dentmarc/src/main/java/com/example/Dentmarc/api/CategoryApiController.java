package com.example.Dentmarc.api;

import com.example.Dentmarc.api.ApiDtos.CategoryResponse;
import com.example.Dentmarc.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryApiController {
    private final CategoriaRepository categoriaRepository;
    private final ApiMapper apiMapper;

    public CategoryApiController(CategoriaRepository categoriaRepository, ApiMapper apiMapper) {
        this.categoriaRepository = categoriaRepository;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.isActiva())
                .map(apiMapper::toCategoryResponse)
                .toList();
    }
}
