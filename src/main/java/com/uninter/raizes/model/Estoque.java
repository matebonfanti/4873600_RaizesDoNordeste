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


//

//Para este MVP, adotou-se a premissa simplificada de que o 'Produto' de venda 
 //* é o mesmo item controlado no 'Estoque' (ex: unidades de tapioca prontas).
 //* O Cardápio de uma unidade é, portanto, derivado diretamente dos itens 
 //* com registro de estoque ativo para aquele local.
 //* Em um cenário real de operação  a melhorprática seria implementar o conceito de "Ficha Técnica".
 //* Isso exigiria a separação de responsabilidades em novas entidades:
 //* 1. Insumo/Ingrediente (Controlados no Estoque - ex: kg de goma, queijo).
 //* 2. Produto/Cardápio (O item final de venda - ex: Tapioca de Queijo).
 //* 3. Ficha Técnica (A relação de quais insumos compõem um Produto).