package com.uninter.raizes.repository;
import com.uninter.raizes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Para consultar um cliente pelo CPF
    Optional<Cliente> findByCpf(String cpf);
}


//Repositorio para o cliente com a opção de consulta por cpf.
// 24/05
