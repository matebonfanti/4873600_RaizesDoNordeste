package com.uninter.raizes.service;
import com.uninter.raizes.model.Produto;
import com.uninter.raizes.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }



// ------------------------- Salvar Produto -------------------------

public Produto salvarProduto(Produto produto){
    if (produto.getNome() == null || produto.getNome().isEmpty()){
        throw new IllegalArgumentException("O nome do produto é obrigatório.");
    }

    if (produto.getDescricao() == null || produto.getDescricao().isEmpty()){
        throw new IllegalArgumentException("Favor inserir uma descrição para o produto.");
    }

    if (produto.getPreco() == null || produto.getPreco() <= 0){
        throw new IllegalArgumentException("O Valor do produto é invalido.");
    }

    return produtoRepository.save(produto);

}

// ------------------------- Buscar Produto por ID -------------------------
public Optional<Produto> buscarProdutoPorId(Integer id){
    return produtoRepository.findById(id);
    
}

// --------------------------- Listar Produtos ---------------------------
public List<Produto> listarProdutos(){
    return produtoRepository.findAll();
}


//------------------------------ Edita Produto ------------------------------
public Produto atualizarProduto(Integer id, Produto novoProduto){
    Optional<Produto> produtoExistente = produtoRepository.findById(id);
    
    if (produtoExistente.isPresent()){
        Produto produto = produtoExistente.get();
        produto.setNome(novoProduto.getNome());
        produto.setDescricao(novoProduto.getDescricao());
        produto.setPreco(novoProduto.getPreco());
        return produtoRepository.save(produto);
    } else {
        return null;
    }

}
}