package com.uninter.raizes.model;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;





//AllArgs e NoArgs são do Lombok para gerar os construtores autimatico
@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String telefone;
    
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Boolean aceiteLgpd;

    @Column(nullable = false)
    private Integer pontosFidelidade;

}
// Como os construtores são gerados pelo lombok, nao implementei abaixo
// Annotations @Collum são para dizer ao banco que nao pode ser nulo ou
// nao pode se repetir.
// ID é a chave primaria. -
// 24/05 

//Melhorias para implementar futuramente:
// - Não deixar alterar o ID e os pontos de fidelidade na requisição de cadastro. (Json "PUT")
// - Nao deixar alterar o ID e os pontos no Patch na atualização dos dados.









