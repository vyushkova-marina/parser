package ru.parser.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.*;
import java.util.Set;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Column(name = "statistics_details")
    @OneToMany(mappedBy = "statistics")
    private Set<StatisticsDetails> statisticsSet;
}
