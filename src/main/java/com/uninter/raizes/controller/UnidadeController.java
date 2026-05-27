package com.uninter.raizes.controller;

import com.uninter.raizes.service.UnidadeService;
import com.uninter.raizes.model.Unidade;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {
    private final UnidadeService unidadeService;
    
    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    // ------------------------- Salvar Unidade -------------------------
    @PostMapping
    public ResponseEntity<Unidade> salvarUnidade(@RequestBody Unidade unidade) {
        Unidade novaUnidade = unidadeService.salvarUnidade(unidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaUnidade);
    }

    // ------------------------- Consultar Unidade -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarUnidadePorId(@PathVariable Integer id){
        var unidade = unidadeService.buscarUnidadePorId(id);
        if (unidade.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(unidade.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ------------------------- Listar Unidades -------------------------
    @GetMapping("/list")
    public ResponseEntity<List<Unidade>> listarUnidades(){
        List<Unidade> unidades = unidadeService.listarUnidades();
        return ResponseEntity.status(HttpStatus.CREATED).body(unidades);
    }
    
    // ------------------------- Editar Unidade -------------------------
    @PatchMapping("/{id}")
    public ResponseEntity<Unidade> editarUnidade(@PathVariable Integer id, @RequestBody Unidade unidade) {
        var unidadeExistente = unidadeService.buscarUnidadePorId(id);
        if (unidadeExistente.isPresent()) {
            Unidade unidadeEditada = unidadeService.atualizarUnidade(id, unidade);
            return ResponseEntity.status(HttpStatus.CREATED).body(unidadeEditada);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
