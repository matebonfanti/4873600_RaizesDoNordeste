package com.uninter.raizes.controller;

import com.uninter.raizes.model.Pedido;
import com.uninter.raizes.model.Usuario;
import com.uninter.raizes.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}