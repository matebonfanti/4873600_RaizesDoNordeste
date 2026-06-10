package com.uninter.raizes.service;

import com.uninter.raizes.enums.StatusPedido;
import com.uninter.raizes.model.*;
import com.uninter.raizes.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uninter.raizes.enums.CanalPedido;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository; 
    private final EstoqueService estoqueService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository,
                         UnidadeRepository unidadeRepository,
                         EstoqueService estoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
        this.estoqueService = estoqueService;
    }

    @Transactional
    public Pedido criarPedido(Integer clienteId, Integer unidadeId, Pedido dadosPedido) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada.")); // 🆕 Garante que a loja existe

        if (dadosPedido.getCanalPedido() == null) {
            throw new IllegalArgumentException("O canal do pedido é obrigatório.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUnidade(unidade); 
        pedido.setCanalPedido(dadosPedido.getCanalPedido());
        pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);

        double valorTotal = 0.0;
        List<ItemPedido> itensProcessados = new ArrayList<>();

        for (ItemPedido itemInput : dadosPedido.getItens()) {
            Produto produto = produtoRepository.findById(itemInput.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

            estoqueService.diminuirEstoque(unidadeId, produto.getId(), itemInput.getQuantidade());

            ItemPedido item = new ItemPedido();
            item.setProduto(produto);
            item.setQuantidade(itemInput.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());
            item.setPedido(pedido);

            valorTotal += item.getPrecoUnitario() * item.getQuantidade();
            itensProcessados.add(item);
        }

        pedido.setValorTotal(valorTotal);
        pedido.setItens(itensProcessados);

        Pedido pedidosalvo = pedidoRepository.save(pedido);
        log.info("[AUDITORIA] Pedido criado | pedidoId={} | clienteId={} | unidadeId={} | canal={}",
        pedidosalvo.getId(), clienteId, unidadeId, dadosPedido.getCanalPedido());

        return pedidosalvo;
    }

    @Transactional
    public Pedido processarPagamentoMock(Long pedidoId, boolean aprovado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatusPedido() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Este pedido não está aguardando pagamento.");
        }

        if (aprovado) {
            pedido.setStatusPedido(StatusPedido.PREPARANDO); 
        } else {
            pedido.setStatusPedido(StatusPedido.CANCELADO); 

            
            for (ItemPedido item : pedido.getItens()) {
                estoqueService.adicionarEstoque(
                    item.getProduto().getId(),
                    pedido.getUnidade().getId(), 
                    item.getQuantidade()
                );
            }
        }

        log.info("[AUDITORIA] Pagamento mock | pedidoId={} | resultado={} | novoStatus={}",
        pedidoId, aprovado ? "APROVADO" : "RECUSADO", pedido.getStatusPedido());

        return pedidoRepository.save(pedido);
    }

    public Pedido buscarPorId(Long id) {
    return pedidoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
}

    public List<Pedido> listar(CanalPedido canalPedido, StatusPedido status) {
    if (canalPedido != null && status != null) {
        return pedidoRepository.findByCanalPedidoAndStatusPedido(canalPedido, status);
    } else if (canalPedido != null) {
        return pedidoRepository.findByCanalPedido(canalPedido);
    } else if (status != null) {
        return pedidoRepository.findByStatusPedido(status);
    } else {
        return pedidoRepository.findAll();
    }
}

    @Transactional
    public Pedido atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatusPedido() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Pedido cancelado não pode ser alterado.");
        }

        pedido.setStatusPedido(novoStatus);
        log.info("[AUDITORIA] Status atualizado | pedidoId={} | novoStatus={}", id, novoStatus);

        return pedidoRepository.save(pedido);
    }



}