package com.uninter.raizes.repository;
import com.uninter.raizes.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
}