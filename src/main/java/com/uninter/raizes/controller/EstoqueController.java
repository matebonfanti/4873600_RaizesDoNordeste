package com.uninter.raizes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.uninter.raizes.model.Estoque;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

import com.uninter.raizes.service.EstoqueService;



@RestController
@RequestMapping("/estoque")

public class EstoqueController {
    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;

    }


//----------- Adicionar Produto ao Estoque -----------
@PostMapping("/adicionar")
public ResponseEntity<Estoque> adicionarAoEstoque(@RequestParam Integer produtoId, @RequestParam Integer unidadeId, @RequestParam Integer quantidade){
    Estoque estoque = estoqueService.adicionarEstoque(produtoId, unidadeId, quantidade);
    return ResponseEntity.status(HttpStatus.CREATED).body(estoque);
}

//----------- Diminuir Produto do Estoque -----------
@PatchMapping("/diminuir")
public ResponseEntity<Void> diminuirDoEstoque(@RequestParam Integer unidadeId, @RequestParam Integer produtoId, @RequestParam Integer quantidade){
    estoqueService.diminuirEstoque(unidadeId, produtoId, quantidade);
    return ResponseEntity.status(HttpStatus.OK).build();
}

//----------- Consultar Estoque -----------

@GetMapping("/{id}")
public ResponseEntity<List<Estoque>> listarEstoquePorUnidade(@PathVariable Integer id){

    List<Estoque> estoque = estoqueService.listarEstoquePorUnidade(id);
    return ResponseEntity.status(HttpStatus.OK).body(estoque);
}



}
