package com.uninter.raizes.controller;
import com.uninter.raizes.service.EstoqueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.uninter.raizes.model.Estoque;
import com.uninter.raizes.model.Produto;
import java.util.List;


@RestController
@RequestMapping("/cardapio")
public class CardapioController {

    private final EstoqueService estoqueService;
    public CardapioController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/unidade/{id}")
    public ResponseEntity<List<Produto>> consultarCardapio(@PathVariable Integer id){

        List<Estoque> estoques = estoqueService.listarEstoquePorUnidade(id);
        List<Produto> cardapio = estoques.stream().map(Estoque::getProduto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(cardapio);
    }

    
}
