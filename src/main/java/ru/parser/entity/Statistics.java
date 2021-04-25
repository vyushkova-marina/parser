package ru.parser.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Table(name = "statistics",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"url", "strategy_of_parsing"}))
@Entity
@Data
@NoArgsConstructor
public class Statistics {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "url", length = 500)
    private String url;


    @Column(name = "strategy_of_parsing")
    private String strategy;

}
