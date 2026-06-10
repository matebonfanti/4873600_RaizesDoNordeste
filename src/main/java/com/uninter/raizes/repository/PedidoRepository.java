package com.uninter.raizes.repository;

import com.uninter.raizes.enums.CanalPedido;
import com.uninter.raizes.enums.StatusPedido;
import com.uninter.raizes.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
    List<Pedido> findByStatusPedido(StatusPedido status);
    List<Pedido> findByCanalPedidoAndStatusPedido(CanalPedido canal, StatusPedido status);

    
}