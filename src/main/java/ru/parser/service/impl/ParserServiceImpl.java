package ru.parser.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.parser.constants.ErrorMessagesConstants;
import ru.parser.constants.InfoMessagesConstants;
import ru.parser.constants.TypeOfStrategyConstants;
import ru.parser.service.ParserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@Slf4j
@Service
public class ParserServiceImpl implements ParserService {

    @Override
    public boolean saveResponseToHtml(String url, Path pathTo) {
        try {
            Document document = Jsoup.connect(url).maxBodySize(0).get();
            Files.write(pathTo, Arrays.asList(document.toString()), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            log.info(InfoMessagesConstants.requestSaved + " " + url.toString());
            return true;
        } catch (IOException e) {
            log.error("{} или {}", ErrorMessagesConstants.requestFailed, ErrorMessagesConstants.failedToWriteToFile);
        }
        return false;
    }

    @Override
    public void splitFileIntoWordsWithDelimitersOnly(Path pathFrom, Path pathTo) {
        (readFromFile(pathFrom)).forEach(line -> writeToFile(parseLine(line), pathTo));
        log.error("{}; {}", InfoMessagesConstants.fileParsed, TypeOfStrategyConstants.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY);
    }

    @Override
    public void splitFileIntoWordsUsingJsoup(Path pathFrom, Path pathTo) {
        try {
            Document document = Jsoup.parse(pathFrom.toFile(), "UTF-8");
            writeToFile(parseLine(document.text()), pathTo);
            log.error("{}; {}", InfoMessagesConstants.fileParsed, TypeOfStrategyConstants.SPLIT_FILE_INTO_WORDS_USING_JSOUP);
        } catch (IOException e) {
            log.error(ErrorMessagesConstants.failedParseDocument);
        }
    }

    @Override
    public void splitFileIntoWordsByReadingLinesAsBodyFragment(Path pathFrom, Path pathTo) {
        readFromFile(pathFrom).forEach(line ->
                writeToFile(parseLine(Jsoup.parseBodyFragment(line).body().text()), pathTo));
        log.error("{}; {}", InfoMessagesConstants.fileParsed, TypeOfStrategyConstants.SPLIT_FILE_INTO_WORDS_BY_READING_LINES_AS_BODY_FRAGMENT);
    }

    @Override
    public String[] parseLine(String line) {
        String delimiters = "[,.!?|{}_'\";/=<>\\-:\\[\\]()\\s]";
        return line.split(delimiters);
    }

    @Override
    public Map<String, Integer> countWords(Path pathFrom) {
        Map<String, Integer> words = new HashMap<>();
        readFromFile(pathFrom).forEach(line -> {
            if (!words.containsKey(line)) {
                int count = 1;
                words.put(line, count);
            } else {
                int countOfWord = words.get(line) + 1;
                words.put(line, countOfWord);
            }
        });
        return words;
    }

    @Override
    public Stream<String> readFromFile(Path pathFrom) {
        try {
            return Files.lines(pathFrom, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(ErrorMessagesConstants.failedToReadFromFile);
        }
        return null;
    }

    @Override
    public void writeToFile(String[] strings, Path pathTo) {
        stream(strings).filter(Predicate.not(""::equals)).forEach(str -> {
            try {
                Files.write(pathTo, Arrays.asList(str), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error(ErrorMessagesConstants.failedToWriteToFile);
            }
        });
    }
}