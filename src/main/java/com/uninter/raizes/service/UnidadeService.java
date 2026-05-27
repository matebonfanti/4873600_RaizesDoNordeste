package com.uninter.raizes.service;

import org.springframework.stereotype.Service;
import com.uninter.raizes.model.Unidade;
import com.uninter.raizes.repository.UnidadeRepository;
import java.util.List;
import java.util.Optional;


@Service    
public class UnidadeService {
    private final UnidadeRepository unidadeRepository;
    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

// ------------------------- Salvar Unidade -------------------------
    public Unidade salvarUnidade(Unidade unidade){
        if (unidade.getNome() == null || unidade.getNome().isEmpty()){
            throw new IllegalArgumentException("O nome da unidade é obrigatório.");
        }
        if (unidade.getCidade() == null || unidade.getCidade().isEmpty()){
            throw new IllegalArgumentException("O nome da cidade é obrigatório.");
        }
        return unidadeRepository.save(unidade);

    }


// ------------------------- Buscar Unidade por ID -------------------------
    public Optional<Unidade> buscarUnidadePorId(Integer id){
        return unidadeRepository.findById(id);
        
    }
// --------------------------- Listar Unidades ---------------------------
    public List<Unidade> listarUnidades(){
        return unidadeRepository.findAll();
    }
//------------------------------ Edita Unidade ------------------------------
    public Unidade atualizarUnidade(Integer id, Unidade novaUnidade){
        Optional<Unidade> unidadeExistente = unidadeRepository.findById(id);
        
        if (unidadeExistente.isPresent()){
            Unidade unidade = unidadeExistente.get();
            unidade.setNome(novaUnidade.getNome());
            unidade.setCidade(novaUnidade.getCidade());
            unidade.setDescricao(novaUnidade.getDescricao());
            return unidadeRepository.save(unidade);
        } else {
            return null;
        }

    }






    
}
