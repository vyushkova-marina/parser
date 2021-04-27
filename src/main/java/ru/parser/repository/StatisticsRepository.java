package ru.parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.parser.entity.Statistics;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    List<Statistics> findAll();
    Statistics findById(long id);
    Statistics findByUrlAndStrategy(String url, String typeOfStrategy);
}
