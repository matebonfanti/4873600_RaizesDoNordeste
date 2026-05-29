package com.uninter.raizes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "estoques", uniqueConstraints = {@UniqueConstraint(columnNames = {"produto_id", "unidade_id"})}) // Ponto de melhoria explicado abaixo
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;


    
}


// implementado 27/05


//Ponto de melhora, pode ter repetição de produto

//