package com.uninter.raizes.service;

import com.uninter.raizes.enums.StatusPedido;
import com.uninter.raizes.model.*;
import com.uninter.raizes.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        return pedidoRepository.save(pedido);
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

        return pedidoRepository.save(pedido);
    }
}