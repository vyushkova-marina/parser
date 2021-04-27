package ru.parser.service;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public interface ParserService {
    /**
     * метод возвращает истину,
     * если запрос запрос успешно отправился и сохранился
     *
     * @param url    куда посылать запрос
     * @param pathTo путь, куда сохранять файл
     * @return true если запрос запрос успешно отправился и сохранился, иначе false
     */
    boolean saveResponseToHtml(String url, Path pathTo);

    /**
     * метод читает файл построчно и разделяет строку с помощью простых разделителей на слова,
     * после чего записывает слова в другой файл
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    void splitFileIntoWordsWithDelimitersOnly(Path pathFrom, Path pathTo);

    /**
     * метод строит DOM-модель из заданного файла,
     * после чего получает текст элемента и всех дочерних элементов,
     * разбивает его на слова,
     * затем записывает в другой файл полученный результат
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    void splitFileIntoWordsUsingJsoup(Path pathFrom, Path pathTo);

    /**
     * метод читает данные из файла построчно
     * и строит из каждой строки DOM-модель,
     * после чего получает текст элемента и всех дочерних элементов,
     * рзбивает на слова, и записывает в другой файл полученный результат
     *
     * @param pathFrom откуда читать данные
     * @param pathTo   куда записывать данные после разбора
     */
    void splitFileIntoWordsByReadingLinesAsBodyFragment(Path pathFrom, Path pathTo);

    /**
     * метод читает слова из разобранного файла и считает количество вхождений каждого слова
     *
     * @param pathFrom откуда читать данные
     * @return hashMap cо ститистикой вхождений
     */
    Map<String, Integer> countWords(Path pathFrom);

    /**
     * разделяет строку на слова с помощью разделителей
     *
     * @param line строка
     * @return массив слов
     */
     String[] parseLine(String line);
    /**
     * метод возврадает стрим, из которого можно читать данные
     *
     * @param pathFrom откуда читать данные
     * @return строковый стрим
     */
     Stream<String> readFromFile(Path pathFrom);
    /**
     * метод записывает массив слов
     *
     * @param strings массив слов
     * @param pathTo  куда записывать данные
     */
     void writeToFile(String[] strings, Path pathTo);

}
