package com.example.Dentmarc.service;

import com.example.Dentmarc.models.Producto;
import com.example.Dentmarc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public void guardarProducto(Producto producto){
        productoRepository.save(producto);
    }

    public Producto obtenerProductoPorId(Long id){
        Optional<Producto> productoOptional = productoRepository.findById(id);
        if(productoOptional.isPresent()){
            Producto producto = productoOptional.get();
            return producto;
        }
        return null;
    }

    public Producto obtenerProductoPorSKU(String SKU){
        Optional<Producto> productoOptional = productoRepository.findBySKU(SKU);
        if(productoOptional.isPresent()){
            Producto producto = productoOptional.get();
            return producto;
        }
        return null;
    }
}
