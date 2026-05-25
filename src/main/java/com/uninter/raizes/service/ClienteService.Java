package com.uninter.raizes.service;
import com.uninter.raizes.model.Cliente;
import com.uninter.raizes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;



@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }    

// ------------------------- Salvar Cliente -------------------------
public Cliente salvarCliente(Cliente cliente){

    // Se cliente nao aceitar lgpd retorna erro
   if (cliente.getAceiteLgpd() == null || cliente.getAceiteLgpd() == false){
        throw new IllegalArgumentException("O cliente deve aceitar os termos LGPD para seguir com o cadastro.");
    }

    // Se cpf for nulo ou vazio retorna um erro
    if (cliente.getCpf() == null || cliente.getCpf().isEmpty()){
        throw new IllegalArgumentException("O CPF é obrigatório.");
    }
    
    // se o CPF ja existe ele retorna erro, 
    if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()){
        throw new IllegalArgumentException("O CPF já está cadastrado.");
    }
    
    // verifica o email, se ja ta cadasttrado retorna erro, se não, salva
    if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
        throw new IllegalArgumentException("O email já está cadastrado.");
    }else {
        return clienteRepository.save(cliente);
    }

}


// ------------------------- Consultar CLiente ---------------------
    //por cpf
    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    //por id
    public Optional<Cliente> buscarClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

// ---------------------- lista todos os CLientes ----------------------
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

// ----------------------- Atualizar dados -----------------------
public Cliente atualizarDadosCliente(Integer id, Cliente novoCliente){

    Optional<Cliente> clienteAtual = clienteRepository.findById(id);
    if (clienteAtual.isPresent()){
        Cliente cliente = clienteAtual.get();

        cliente.setNome(novoCliente.getNome());
        cliente.setEmail(novoCliente.getEmail());
        cliente.setTelefone(novoCliente.getTelefone());
        cliente.setDataNascimento(novoCliente.getDataNascimento());
        

        return clienteRepository.save(cliente);

    }else {
        throw new IllegalArgumentException("Cliente não encontrado.");
    }
}


// ------------------ Pontos de fidelidade ------------------

public void somaPontos(Integer id, Integer pontos){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        Cliente clienteAtual = cliente.get();
        clienteAtual.setPontosFidelidade(clienteAtual.getPontosFidelidade() + pontos);
        clienteRepository.save(clienteAtual);
    }
}

public void menosPontos(Integer id, Integer pontos){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        Cliente clienteAtual = cliente.get();
        if (clienteAtual.getPontosFidelidade() < pontos) {
            throw new IllegalArgumentException("Pontos insuficientes para resgate.");
        } else {
            clienteAtual.setPontosFidelidade(clienteAtual.getPontosFidelidade() - pontos);
        }
        clienteRepository.save(clienteAtual);
    }
}

public Integer consultaPontos(Integer id){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        return cliente.get().getPontosFidelidade();
    } else {
        throw new IllegalArgumentException("Cliente não encontrado.");
        
    }

}

}