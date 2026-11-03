package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Categoria;
import com.example.Dentmarc.repository.CategoriaRepository;
import com.example.Dentmarc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    // estado: "activas", "ocultas" o vacio para todas
    public List<Categoria> buscarCategorias(String busqueda, String estado) {
        List<Categoria> categorias;
        if (busqueda == null || busqueda.isBlank()) {
            categorias = categoriaRepository.findAll();
        } else {
            categorias = categoriaRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        }

        if ("activas".equals(estado)) {
            return categorias.stream().filter(Categoria::isActiva).toList();
        }
        if ("ocultas".equals(estado)) {
            return categorias.stream().filter(c -> !c.isActiva()).toList();
        }
        return categorias;
    }

    public Categoria obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public long contarActivas() {
        return categoriaRepository.countByActiva(true);
    }

    public List<Categoria> obtenerOcultas() {
        return categoriaRepository.findByActivaFalse();
    }

    // Crea la categoria (id null) o actualiza una existente
    public void guardarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        categoria.setNombre(categoria.getNombre().trim());

        Categoria repetida = categoriaRepository.findByNombreIgnoreCase(categoria.getNombre()).orElse(null);
        if (repetida != null && !repetida.getId().equals(categoria.getId())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        if (categoria.getIcono() == null || categoria.getIcono().isBlank()) {
            categoria.setIcono("fa-tag");
        }
        categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        if (productoRepository.countByCategoria_Id(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: la categoría tiene productos asignados");
        }
        categoriaRepository.deleteById(id);
    }
}
