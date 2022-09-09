//package com.order.service.model;
//
//import lombok.*;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class RepeatOrderItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenAddress")
//    @SequenceGenerator(name = "seqGenAddress", sequenceName = "seqAddress")
//    @Column(name = "repeat_order_item_id", nullable = false)
//    private Long id;
//
//    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
//    @ToString.Exclude
//    private Product product;
//    
//    private Long quantity;
//
//}
