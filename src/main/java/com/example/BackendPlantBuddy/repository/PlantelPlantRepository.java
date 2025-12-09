package com.example.BackendPlantBuddy.repository;

import com.example.BackendPlantBuddy.entity.PlantelPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantelPlantRepository extends JpaRepository<PlantelPlant, Integer> {
    List<PlantelPlant> findByUsuario_Id(Integer userId);
    Optional<PlantelPlant> findByUsuario_IdAndProducto_Id(Integer userId, Integer productId);
    void deleteByUsuario_IdAndProducto_Id(Integer userId, Integer productId);
    boolean existsByUsuario_IdAndProducto_Id(Integer userId, Integer productId);
}
