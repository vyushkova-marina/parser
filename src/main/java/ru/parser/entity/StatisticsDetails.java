package ru.parser.entity;

import lombok.*;
import javax.persistence.*;

@Table(name = "statistics_details")
@Data
@Entity
public class StatisticsDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ToString.Exclude
    @ManyToOne
    private Statistics statistics;

    @Column(name = "word", length =300)
    private String word;

    @Column(name = "number_of_quantity")
    private int numberOfQuantity;

}
