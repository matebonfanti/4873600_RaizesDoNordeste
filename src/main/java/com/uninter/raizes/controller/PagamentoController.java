package com.uninter.raizes.controller;

import com.uninter.raizes.model.Pedido;
import com.uninter.raizes.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PedidoService pedidoService;

    public PagamentoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/simular")
    public ResponseEntity<Pedido> simularPagamento(@RequestParam Long pedidoId, 
                                                   @RequestParam boolean aprovado) {
        
        Pedido pedidoAtualizado = pedidoService.processarPagamentoMock(pedidoId, aprovado);
        return ResponseEntity.ok(pedidoAtualizado);
    }
}