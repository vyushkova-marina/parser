package ru.parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.parser.entity.Statistics;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findAll();
    Statistics findById(long id);
    Statistics findByUrlAndStrategy(String url, String typeOfStrategy);
}
