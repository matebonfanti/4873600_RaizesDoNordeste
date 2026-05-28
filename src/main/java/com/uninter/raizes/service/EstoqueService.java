package com.uninter.raizes.service;

import com.uninter.raizes.model.Estoque;
import com.uninter.raizes.repository.EstoqueRepository;
import com.uninter.raizes.repository.ProdutoRepository;
import com.uninter.raizes.repository.UnidadeRepository;
import com.uninter.raizes.model.Produto;
import com.uninter.raizes.model.Unidade;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EstoqueService {
    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository, UnidadeRepository unidadeRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }




    //------------------------------- Adicionar Estoque -----------------------------
    public Estoque adicionarEstoque(Integer produtoId, Integer unidadeId, Integer quantidade) {
        Optional<Produto> produtoaux = produtoRepository.findById(produtoId);
        Optional<Unidade> unidadeaux = unidadeRepository.findById(unidadeId);

        if (produtoaux.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + produtoId);
        }

        if (unidadeaux.isEmpty()) {
            throw new IllegalArgumentException("Unidade não encontrada com ID: " + unidadeId);
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        Produto produto = produtoaux.get();
        Unidade unidade = unidadeaux.get();


        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setUnidade(unidade);
        estoque.setQuantidade(quantidade);

        return estoqueRepository.save(estoque);

}

//------------------------------- Remover Estoque -----------------------------
public void removerEstoque(Integer unidadeId, Integer ProdutoId, Integer quantidade){

    if (quantidade <= 0 || quantidade == null) {
        throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
    }

    Optional<Estoque> estoqueaux = estoqueRepository.findByProdutoIdAndUnidadeId(ProdutoId, unidadeId);
    if (estoqueaux.isEmpty()) {
        throw new IllegalArgumentException("Estoque não encontrado para o produto ID: " + ProdutoId + " e unidade ID: " + unidadeId);
    }

    Estoque estoque = estoqueaux.get();
    if (estoque.getQuantidade() < quantidade) {
        throw new IllegalArgumentException("Quantidade insuficiente em estoque. Quantidade disponível: " + estoque.getQuantidade());
    }
    estoque.setQuantidade(estoque.getQuantidade() - quantidade);
    estoqueRepository.save(estoque);

}



//------------------------------- Consultar Estoque -----------------------------
public List<Estoque> listarEstoquePorUnidade(Integer unidadeId){

    Optional<Unidade> unidadeaux = unidadeRepository.findById(unidadeId);
    if (unidadeaux.isEmpty()) {
        throw new IllegalArgumentException("Unidade não encontrada com ID: " + unidadeId);
    }
    return estoqueRepository.findByUnidadeId(unidadeId);
}
}