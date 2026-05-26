package com.uninter.raizes.controller;

import com.uninter.raizes.model.Cliente;
import com.uninter.raizes.service.ClienteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController{


    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    // ---------------- Cadastrar Cliente (Salvar) -----------------
    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente){
        Cliente clienteCadastrado = clienteService.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCadastrado);
    }

    // --------------- Consultar Cliente --------------

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id){
        var cliente = clienteService.buscarClientePorId(id);
        if (cliente.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf){

        var cliente = clienteService.buscarClientePorCpf(cpf);
        if (cliente.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ---------------------- Listar Clientes ----------------------
    @GetMapping("/list")
    public ResponseEntity<List<Cliente>> listarClientes(){
        List<Cliente> clientes = clienteService.listarClientes();
        return ResponseEntity.status(HttpStatus.OK).body(clientes);
    }

    // ----------------------- Atualizar dados -----------------------
    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> atualizarDadosCliente(@PathVariable Integer id, @RequestBody Cliente novoCliente){
        Cliente clienteAtualizado = clienteService.atualizarDadosCliente(id, novoCliente);
        if (clienteAtualizado != null){
            return ResponseEntity.status(HttpStatus.OK).body(clienteAtualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ------------------ Pontos de fidelidade ------------------
    @GetMapping("/{id}/pontos")
    public ResponseEntity<Integer> consultarPontos(@PathVariable Integer id){
        Integer pontos = clienteService.consultaPontos(id);
        if (pontos != null){
            return ResponseEntity.status(HttpStatus.OK).body(pontos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }    
    }

    @PatchMapping("/{id}/pontos/soma")
    public ResponseEntity<Void> somaPontos(@PathVariable Integer id, @RequestParam Integer pontos){
        clienteService.somaPontos(id, pontos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/pontos/menos")
    public ResponseEntity<Void> menosPontos(@PathVariable Integer id, @RequestParam Integer pontos){
        clienteService.menosPontos(id, pontos);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    
    




}
