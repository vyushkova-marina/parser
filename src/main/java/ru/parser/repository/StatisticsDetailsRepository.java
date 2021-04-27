package ru.parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.parser.entity.Statistics;
import ru.parser.entity.StatisticsDetails;
import java.util.Set;

@Repository
public interface StatisticsDetailsRepository extends JpaRepository<StatisticsDetails, Long> {
    Set<StatisticsDetails> findByStatisticsOrderByNumberOfQuantity(Statistics statistics);
}
