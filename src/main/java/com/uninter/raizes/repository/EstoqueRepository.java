package com.uninter.raizes.repository;

import com.uninter.raizes.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    
    List<Estoque> findByUnidadeId(Integer unidadeId);

    Optional<Estoque> findByProdutoIdAndUnidadeId(Integer produtoId, Integer unidadeId);
    
}
