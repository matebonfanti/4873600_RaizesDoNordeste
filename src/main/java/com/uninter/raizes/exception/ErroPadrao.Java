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


@Data
@AllArgsConstructor
public class ErroPadrao{
    private String erro;
    private String mensagem;
    private List<DetalhesErro> detalhes;
    private LocalDateTime timestamp;
    private String path;

} 

@Data
@AllArgsConstructor
class DetalhesErro{
    private String campo;
    private String mensagem;
}


@RestControllerAdvice
class TratadordeErros{

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

}