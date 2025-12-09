package com.example.BackendPlantBuddy.repository;

import com.example.BackendPlantBuddy.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    List<Compra> findByUsuario_IdOrderByCreatedAtDesc(Integer userId);
    List<Compra> findByUsuario_IdAndStatusOrderByCreatedAtDesc(Integer userId, com.example.BackendPlantBuddy.entity.EstadoCompra status);
}
