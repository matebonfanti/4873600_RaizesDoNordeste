package com.uninter.raizes.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import com.uninter.raizes.exception.AutenticacaoException;
import com.uninter.raizes.exception.RegraDeNegocioException;

@Data
@AllArgsConstructor
public class ErroPadrao {
    private String erro;
    private String mensagem;
    private List<DetalhesErro> detalhes;
    private LocalDateTime timestamp;
    private String path;
} 

@Data
@AllArgsConstructor
class DetalhesErro {
    private String campo;
    private String mensagem;
}


class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}


@RestControllerAdvice
class TratadordeErros {

    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroPadrao> tratarIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request){
        ErroPadrao erro = new ErroPadrao(
            "DADOS_INVALIDOS",
            ex.getMessage(),
            new ArrayList<>(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroPadrao> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request){
        ErroPadrao erro = new ErroPadrao(
            "NAO_ENCONTRADO",
            ex.getMessage(),
            new ArrayList<>(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroPadrao> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex, HttpServletRequest request){
        ErroPadrao erro = new ErroPadrao(
            "CREDENCIAIS_INVALIDAS",
            ex.getMessage(),
            new ArrayList<>(),
            LocalDateTime.now(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroPadrao> tratarAutenticacao(
        AutenticacaoException ex, HttpServletRequest request) {
    ErroPadrao erro = new ErroPadrao(
        "AUTENTICACAO_INVALIDA",
        ex.getMessage(),
        new ArrayList<>(),
        LocalDateTime.now(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroPadrao> tratarRegraDeNegocio(
        RegraDeNegocioException ex, HttpServletRequest request) {
    ErroPadrao erro = new ErroPadrao(
        "REGRA_DE_NEGOCIO",
        ex.getMessage(),
        new ArrayList<>(),
        LocalDateTime.now(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(erro); 
}


}