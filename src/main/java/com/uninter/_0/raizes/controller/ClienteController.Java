package com.uninter._0.raizes.service;
import com.uninter._0.raizes.model.Cliente;
import com.uninter._0.raizes.repository.ClienteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/clientes")
public class ClienteController{


    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    // DEPOIS IMPLEMENTAR O PADRÃO DE ERRO EM JSON CONFORME TRABALHO


    // ---------------- Cadastrar Cliente (Salvar) -----------------
    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente){
        Cliente clienteCadastrado = clienteService.salvarCliente(cliente);
        if (clienteCadastrado != null){
            return ResponseEntity.ok(clienteCadastrado);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // --------------- Consultar Cliente --------------

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id){
        var cliente = clienteService.buscarClientePorId(id);
        if (cliente.isPresent()){
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf){

        var cliente = clienteService.buscarClientePorCpf(cpf);
        if (cliente.isPresent()){
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ---------------------- Listar Clientes ----------------------
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        List<Cliente> clientes = clienteService.ListarClientes();
        return ResponseEntity.ok(clientes);
    }

    // ----------------------- Atualizar dados -----------------------
    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> atualizarDadosCliente(@PathVariable Integer id, @RequestBody Cliente novoCliente){
        Cliente clienteAtualizado = clienteService.atualizarDadosCliente(id, novoCliente);
        if (clienteAtualizado != null){
            return ResponseEntity.ok(clienteAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }


    // ------------------ Pontos de fidelidade ------------------
    @GetMapping("/{id}/pontos")
    public ResponseEntity<Integer> consultarPontos(@PathVariable Integer id){
        Integer pontos = clienteService.consultaPontos(id);
        if (pontos != null){
            return ResponseEntity.ok(pontos);
        } else {
            return ResponseEntity.notFound().build();
        }    
    }

    @PatchMapping("/{id}/pontos/soma")
    public ResponseEntity<Void> somaPontos(@PathVariable Integer id, @RequestParam Integer pontos){
        clienteService.somaPontos(id, pontos);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/pontos/menos")
    public ResponseEntity<Void> menosPontos(@PathVariable Integer id, @RequestParam Integer pontos){
        clienteService.menosPontos(id, pontos);
        return ResponseEntity.ok().build();
    }



    
    




}