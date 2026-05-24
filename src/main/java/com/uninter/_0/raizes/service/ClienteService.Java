package com.uninter._0.raizes.service;
import com.uninter._0.raizes.model.Cliente;
import com.uninter._0.raizes.repository.ClienteRepository;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }    

// ------------------------- Salvar Cliente -------------------------
public Cliente salvarCliente(Cliente cliente){

    // Se cliente nao aceitar lgpd retorna erro
   if (cliente.getaceiteLgpd() == null || cliente.getaceiteLgpd() == false){
        // implementar tratamento de erro posteriormente
        return null;
    }

    // Se cpf for nulo ou vazio retorna um erro
    if (cliente.getCpf() == null || cliente.getCpf().isEmpty()){
        // implementar tratamento de erro posteriormente
        return null;
    }
    // se o CPF ja existe ele retorna erro, se não, salva
    if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()){
        // implementar tratamento de erro posteriormente
        return null;
    } else {
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
        // implementar tratamento de erro caso nao encontre o ID.
        return null;
    }


// ------------------ Pontos de fidelidade ------------------

public void somaPontos(Integer id, Integer pontos){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        Cliente clienteAtual = cliente.get();
        clienteAtual.setPontos(clienteAtual.getPontos() + pontos);
        clienteRepository.save(clienteAtual);
    }
}

public void menosPontos(Integer id, Integer pontos){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        Cliente clienteAtual = cliente.get();
        if (clienteAtual.getpontosFidelidade() < pontos) {
            // lançar execeção se os pontos forem menores do que podem tirar
        } else {
            clienteAtual.setpontosFidelidade(clienteAtual.getpontosFidelidade() - pontos);
        }
        clienteRepository.save(clienteAtual);
    }
}

public Integer consultaPontos(Integer id){
    Optional<Cliente> cliente = clienteRepository.findById(id);
    if (cliente.isPresent()) {
        return cliente.get().getpontosFidelidade();
    } else {
        // implementar tratamento de erro caso nao encontre o ID.
        return null;
    }

}