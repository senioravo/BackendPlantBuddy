package com.example.BackendPlantBuddy.repository;

import com.example.BackendPlantBuddy.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    List<Producto> findByDisponibleTrue();
    
    List<Producto> findByCategoria_NombreAndDisponibleTrue(String categoria);
    
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Producto> buscarProductos(@Param("busqueda") String busqueda);
    
    @Query("SELECT p FROM Producto p WHERE p.disponible = true AND " +
           "p.categoria.nombre = :categoria AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Producto> buscarProductosPorCategoria(
        @Param("busqueda") String busqueda, 
        @Param("categoria") String categoria
    );
    
    List<Producto> findTop10ByDisponibleTrueOrderByRatingDesc();
}
