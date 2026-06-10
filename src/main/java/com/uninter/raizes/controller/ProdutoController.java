package com.uninter.raizes.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uninter.raizes.model.Produto;
import com.uninter.raizes.service.ProdutoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;




@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }
// ------------------------- Salvar Produto -------------------------
    @PostMapping
    public ResponseEntity<Produto> salvarProduto(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

// ------------------------ Listar Produtos ------------------------
    @GetMapping("/list")
    public ResponseEntity<List<Produto>> listarProdutos(){
        List<Produto> produtos = produtoService.listarProdutos();
        return ResponseEntity.status(HttpStatus.OK).body(produtos);
    }

// ------------------------- Consultar Produto -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProdutoPorId(@PathVariable Integer id){
        var produto = produtoService.buscarProdutoPorId(id);        
        if (produto.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(produto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Integer id, @RequestBody Produto novoProduto) {
            Produto produtoAtualizado = produtoService.atualizarProduto(id, novoProduto);
            if (produtoAtualizado != null) {
            return ResponseEntity.status(HttpStatus.OK).body(produtoAtualizado);
            } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
