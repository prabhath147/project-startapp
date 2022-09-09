package com.order.service.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGenOrder")
    @SequenceGenerator(name = "seqGenOrder", sequenceName = "seqOrder", allocationSize = 1)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Item> items = new HashSet<>();

    @Column(name = "quantity")
    private Long quantity = 0L;

    @Column(name = "price")
    private Double price = 0D;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_details_id", referencedColumnName = "order_details_id")
    @ToString.Exclude
    private OrderDetails orderDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_address_id", referencedColumnName = "address_id")
    @ToString.Exclude
    private Address orderAddress;

    @Column(name = "order_date")
    @Temporal(TemporalType.DATE)
    private Date orderDate = new Date();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Orders order = (Orders) o;
        return orderId != null && Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
