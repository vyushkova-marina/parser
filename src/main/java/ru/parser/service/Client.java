package ru.parser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.parser.entity.Statistics;
import ru.parser.entity.StatisticsDetails;
import ru.parser.information.InfoMessageText;
import ru.parser.repository.StatisticsDetailsRepository;
import ru.parser.repository.StatisticsRepository;
import ru.parser.information.ErrorMessageText;
import ru.parser.information.TypeOfStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class Client {
    private final Path finalPathFile = Paths.get("src\\main\\downloaded pages\\parsedPage.txt");
    private final Path sourcePathFile = Paths.get("src\\main\\downloaded pages\\page.html");
    private final StatisticsDetailsRepository statisticsDetailsRepository;
    private final StatisticsRepository statisticsRepository;
    private final Parser parser;
    private Statistics statistics;

    /**
     * метод создает файлы для работы с http ответом, данными
     * и создает новый объект статистики
     * возвращает истину если запрос отправился
     *
     * @param url куда отправлять запрос
     * @return true, если запрос и сохранение успешны
     */
    public boolean downloadPage(String url) {
        checkFile(sourcePathFile);
        checkFile(finalPathFile);
        if (parser.saveResponseToHtml(url, sourcePathFile)) {
            statistics = new Statistics();
            statistics.setUrl(url);
            return true;
        }
        return false;
    }

    /**
     * метод разбивает файл на слова в зависимости от выбранной стратегии
     *
     * @param strategy каким способом разделять текст
     */
    public void parse(String strategy) {
        statistics.setStrategy(strategy);
        switch (strategy) {
            case TypeOfStrategy.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY:
                parser.splitFileIntoWordsWithDelimitersOnly(sourcePathFile, finalPathFile);
                break;
            case TypeOfStrategy.SPLIT_FILE_INTO_WORDS_USING_JSOUP:
                parser.splitFileIntoWordsUsingJsoup(sourcePathFile, finalPathFile);
                break;
            default:
                parser.splitFileIntoWordsByReadingLinesAsBodyFragment(sourcePathFile, finalPathFile);
                break;
        }
    }

    /**
     * метод сохраняет статистику в бд, перед этим проверяя,
     * существует ли в бд такая запись (сравнивая по типу стратегии и url)
     */
    public void saveStatistics() {
        Statistics foundStatistics = checkIfExists(statistics);
        if (foundStatistics != null) {
            statistics = foundStatistics;
        }
        statisticsRepository.save(statistics);
        Map<String, Integer> map = parser.countWords(finalPathFile);
        for (Map.Entry<String, Integer> entry : (map.entrySet())) {
            StatisticsDetails statisticsDetails = new StatisticsDetails();
            statisticsDetails.setStatistics(statistics);
            if (entry.getKey().length() >= 200) {
                statisticsDetails.setWord(entry.getKey().substring(0, 200));
            } else {
                statisticsDetails.setWord(entry.getKey());
            }
            statisticsDetails.setNumberOfQuantity(entry.getValue());
            statisticsDetailsRepository.save(statisticsDetails);
        }
        log.info(InfoMessageText.statisticsSaved);
    }

    /**
     * метод проверяет, существует ли такая статистика в бд,
     * и если есть, то возвращает ее
     *
     * @param statistics статистика на проверку
     * @return null если такой статистики нет в бд или существующая статистика
     */
    private Statistics checkIfExists(Statistics statistics) {
        return statisticsRepository.findByUrlAndStrategy(
                statistics.getUrl(), statistics.getStrategy());
    }

    /**
     * метод отображает всю статистику без подробных деталей (статистика по каждому слову)
     */
    public void showAllStatisticsWithoutDetails() {
        List<Statistics> statisticsList = statisticsRepository.findAll();
        for (Statistics statistics : statisticsList) {
            System.out.println(statistics.toString());
        }
    }

    /**
     * метод отображает всю статистику с деталями (статистика по каждому слову)
     * по id статистики
     *
     * @param id id статистики
     */
    public void showStatisticsAndDetailsByStatisticsId(long id) {
        Statistics statistics = statisticsRepository.findById(id);
        System.out.println(statistics.toString());
        Set<StatisticsDetails> statisticsDetailsSet = statisticsDetailsRepository.findByStatisticsOrderByNumberOfQuantity(statistics);
        for (StatisticsDetails statisticsDetails : statisticsDetailsSet) {
            System.out.println(statisticsDetails.toString());
        }
    }

    /**
     * метод создает файл
     *
     * @param path по какому пути искать проверяемый файл
     */
    public void checkFile(Path path) {
        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            log.error(ErrorMessageText.failedToCreateOrDeleteFile);
        }
    }

    /**
     * метод проверяет, является ли строка URL
     *
     * @param url адрес для проверки
     * @return
     */
    public boolean isUrlValid(String url) {
        Pattern pattern = Pattern.compile("^(?:(?:https?|ftp|telnet)://(?:[а-яёa-z0-9_-]{1,32}(?::[а-яёa-z0-9_-]{1,32})?@)?)?(?:(?:[а-яёa-z0-9-]{1,128}\\.)+(?:ru|su|com|net|org|mil|edu|arpa|gov|biz|info|aero|рф|inc|name|[a-z]{2})|(?!0)(?:(?!0[^.]|255)[0-9]{1,3}\\.){3}(?!0|255)[0-9]{1,3})(?:/[а-яёa-z0-9.,_@%&?+=\\~/-]*)?(?:#[^ '\\\"&]*)?$");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();

    }

}
