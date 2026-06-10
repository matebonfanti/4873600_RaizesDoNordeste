package com.uninter.raizes.controller;

import com.uninter.raizes.enums.CanalPedido;
import com.uninter.raizes.enums.StatusPedido;
import com.uninter.raizes.model.Pedido;
import com.uninter.raizes.model.Usuario;
import com.uninter.raizes.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@AuthenticationPrincipal Usuario usuarioAutenticado,
                                              @RequestParam Integer unidadeId,
                                              @RequestBody Pedido dadosPedido) {
        
        Pedido novoPedido = pedidoService.criarPedido(usuarioAutenticado.getId(), unidadeId, dadosPedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(pedido);
    }


    @GetMapping
    public ResponseEntity<List<Pedido>> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status) {
        List<Pedido> pedidos = pedidoService.listar(canalPedido, status);
        return ResponseEntity.ok(pedidos);
}


    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
        @PathVariable Long id,
        @RequestParam StatusPedido novoStatus) {
            
    Pedido atualizado = pedidoService.atualizarStatus(id, novoStatus);
    return ResponseEntity.ok(atualizado);
}


}