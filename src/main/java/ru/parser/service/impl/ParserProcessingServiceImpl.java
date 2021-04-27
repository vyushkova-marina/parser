package ru.parser.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.parser.constants.InfoMessagesConstants;
import ru.parser.entity.Statistics;
import ru.parser.entity.StatisticsDetails;
import ru.parser.repository.StatisticsDetailsRepository;
import ru.parser.repository.StatisticsRepository;
import ru.parser.constants.ErrorMessagesConstants;
import ru.parser.constants.TypeOfStrategyConstants;
import ru.parser.service.ParserService;
import ru.parser.service.ParserProcessingService;
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
public class ParserProcessingServiceImpl implements ParserProcessingService {
    private final Path finalPathFile = Paths.get("./downloaded pages/parsedPage.txt");
    private final Path sourcePathFile = Paths.get("./downloaded pages/page.html");
    private final StatisticsDetailsRepository statisticsDetailsRepository;
    private final StatisticsRepository statisticsRepository;
    private final ParserService parserServiceImpl;
    private Statistics statistics;

    @Override
    public boolean downloadPage(String url) {
        checkFile(sourcePathFile);
        checkFile(finalPathFile);
        if (parserServiceImpl.saveResponseToHtml(url, sourcePathFile)) {
            statistics = new Statistics();
            statistics.setUrl(url);
            return true;
        }
        return false;
    }

    @Override
    public void parse(String strategy) {
        statistics.setStrategy(strategy);
        switch (strategy) {
            case TypeOfStrategyConstants.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY:
                parserServiceImpl.splitFileIntoWordsWithDelimitersOnly(sourcePathFile, finalPathFile);
                break;
            case TypeOfStrategyConstants.SPLIT_FILE_INTO_WORDS_USING_JSOUP:
                parserServiceImpl.splitFileIntoWordsUsingJsoup(sourcePathFile, finalPathFile);
                break;
            default:
                parserServiceImpl.splitFileIntoWordsByReadingLinesAsBodyFragment(sourcePathFile, finalPathFile);
                break;
        }
    }

    @Override
    public void saveStatistics() {
        Statistics foundStatistics = checkIfStatisticsExists(statistics);
        if (foundStatistics != null) {
            statistics = foundStatistics;
        } else {
            statisticsRepository.save(statistics);
        }
        Map<String, Integer> map = parserServiceImpl.countWords(finalPathFile);
        StatisticsDetails statisticsDetails;
        for (Map.Entry<String, Integer> entry : (map.entrySet())) {
            statisticsDetails = new StatisticsDetails();
            statisticsDetails.setStatistics(statistics);
            if (entry.getKey().length() >= 200) {
                statisticsDetails.setWord(entry.getKey().substring(0, 200));
            } else {
                statisticsDetails.setWord(entry.getKey());
            }
            statisticsDetails.setNumberOfQuantity(entry.getValue());
            statisticsDetailsRepository.save(statisticsDetails);
        }
        log.info(InfoMessagesConstants.statisticsSaved);
    }

    @Override
    public Statistics checkIfStatisticsExists(Statistics statistics) {
        return statisticsRepository.findByUrlAndStrategy(
                statistics.getUrl(), statistics.getStrategy());
    }

    @Override
    public void showAllStatisticsWithoutDetails() {
        List<Statistics> statisticsList = statisticsRepository.findAll();
        for (Statistics statistics : statisticsList) {
            System.out.println(statistics.toString());
        }
    }

    @Override
    public void showStatisticsAndDetailsByStatisticsId(long id) {
        Statistics statistics = statisticsRepository.findById(id);
        System.out.println(statistics.toString());
        //Set<StatisticsDetails> statisticsDetailsSet = statistics.getStatisticsSet();

        Set<StatisticsDetails> statisticsDetailsSet = statisticsDetailsRepository.findByStatisticsOrderByNumberOfQuantity(statistics);
        for (StatisticsDetails statisticsDetails : statisticsDetailsSet) {
            System.out.println(statisticsDetails.toString());
        }
    }

    @Override
    public void checkFile(Path path) {
        try {
            Files.deleteIfExists(path);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        } catch (IOException e) {
            log.error(ErrorMessagesConstants.failedToCreateOrDeleteFile);
        }
    }

    @Override
    public boolean isUrlValid(String url) {
        Pattern pattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zа-яА-ЯA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zА-Яа-яA-Z0-9()]{1,6}\\b([-a-zа-яА-ЯA-Z0-9()@:%_\\+.~#?&//=]*)");
        //Pattern pattern = Pattern.compile("^(?:(?:https?|ftp|telnet)://(?:[а-яёa-z0-9_-]{1,32}(?::[а-яёa-z0-9_-]{1,32})?@)?)?(?:(?:[а-яёa-z0-9-]{1,128}\\.)+(?:ru|su|com|net|org|mil|edu|arpa|gov|biz|info|aero|рф|inc|name|[a-z]{2})|(?!0)(?:(?!0[^.]|255)[0-9]{1,3}\\.){3}(?!0|255)[0-9]{1,3})(?:/[а-яёa-z0-9.,_@%&?+=\\~/-]*)?(?:#[^ '\\\"&]*)?$");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();

    }

}
