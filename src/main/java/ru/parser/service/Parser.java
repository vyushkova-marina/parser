package ru.parser.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import ru.parser.information.ErrorMessageText;
import ru.parser.information.InfoMessageText;
import ru.parser.information.TypeOfStrategy;
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
public class Parser {

    /**
     * метод возвращает истину,
     * если запрос запрос успешно отправился и сохранился
     *
     * @param url    куда посылать запрос
     * @param pathTo путь, куда сохранять файл
     * @return true если запрос запрос успешно отправился и сохранился, иначе false
     */
    public boolean saveResponseToHtml(String url, Path pathTo) {
        try {
            Document document = Jsoup.connect(url).maxBodySize(0).get();
            Files.write(pathTo, Arrays.asList(document.toString()), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            log.info(InfoMessageText.requestSaved + " " + url.toString());
            return true;
        } catch (IOException e) {
            log.error("{} или {}", ErrorMessageText.requestFailed, ErrorMessageText.failedToWriteToFile);
        }
        return false;
    }

    /**
     * метод читает файл построчно и разделяет строку с помощью простых разделителей на слова,
     * после чего записывает слова в другой файл
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    public void splitFileIntoWordsWithDelimitersOnly(Path pathFrom, Path pathTo) {
        (readFromFile(pathFrom)).forEach(line -> writeToFile(parseLine(line), pathTo));
        log.error("{}; {}", InfoMessageText.fileParsed, TypeOfStrategy.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY);
    }

    /**
     * метод строит DOM-модель из заданного файла,
     * после чего получает текст элемента и всех дочерних элементов,
     * разбивает его на слова,
     * затем записывает в другой файл полученный результат
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    public void splitFileIntoWordsUsingJsoup(Path pathFrom, Path pathTo) {
        try {
            Document document = Jsoup.parse(pathFrom.toFile(), "UTF-8");
            writeToFile(parseLine(document.text()), pathTo);
            log.error("{}; {}", InfoMessageText.fileParsed, TypeOfStrategy.SPLIT_FILE_INTO_WORDS_USING_JSOUP);
        } catch (IOException e) {
            log.error(ErrorMessageText.failedParseDocument);
        }
    }

    /**
     * метод читает данные из файла построчно
     * и строит из каждой строки DOM-модель,
     * после чего получает текст элемента и всех дочерних элементов,
     * рзбивает на слова, и записывает в другой файл полученный результат
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    public void splitFileIntoWordsByReadingLinesAsBodyFragment(Path pathFrom, Path pathTo) {
        readFromFile(pathFrom).forEach(line ->
                writeToFile(parseLine(Jsoup.parseBodyFragment(line).body().text()), pathTo));
        log.error("{}; {}", InfoMessageText.fileParsed, TypeOfStrategy.SPLIT_FILE_INTO_WORDS_BY_READING_LINES_AS_BODY_FRAGMENT);
    }

    /**
     * разделяет строку на слова с помощью разделителей
     *
     * @param line строка
     * @return массив слов
     */
    private String[] parseLine(String line) {
        String delimiters = "[,.!?|{}_'\";/=<>\\-:\\[\\]()\\s]";
        return line.split(delimiters);
    }

    /**
     * метод читает слова из разобранного файла и считает количество вхождений каждого слова
     *
     * @param pathFrom откуда читать данные
     * @return hashMap cо ститистикой вхождений
     */
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

    /**
     * метод возврадает стрим, из которого можно читать данные
     *
     * @param pathFrom откуда читать данные
     * @return строковый стрим
     */
    private Stream<String> readFromFile(Path pathFrom) {
        try {
            return Files.lines(pathFrom, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(ErrorMessageText.failedToReadFromFile);
        }
        return null;
    }

    /**
     * метод записывает массив слов
     *
     * @param strings массив слов
     * @param pathTo  куда записывать данные
     */
    private void writeToFile(String[] strings, Path pathTo) {
        stream(strings).filter(Predicate.not(""::equals)).forEach(str -> {
            try {
                Files.write(pathTo, Arrays.asList(str + "\n"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error(ErrorMessageText.failedToWriteToFile);
            }
        });
    }
}