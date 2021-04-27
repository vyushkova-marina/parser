package ru.parser.service;

import ru.parser.entity.Statistics;

import java.nio.file.Path;

public interface ParserProcessingService {
    /**
     * метод создает файлы для работы с http ответом, данными
     * и создает новый объект статистики
     * возвращает истину если запрос отправился
     *
     * @param url куда отправлять запрос
     * @return true, если запрос и сохранение успешны
     */
    boolean downloadPage(String url);

    /**
     * метод разбивает файл на слова в зависимости от выбранной стратегии
     *
     * @param strategy каким способом разделять текст
     */
    void parse(String strategy);

    /**
     * метод сохраняет статистику в бд, перед этим проверяя,
     * существует ли в бд такая запись (сравнивая по типу стратегии и url)
     */
    void saveStatistics();

    /**
     * метод проверяет, существует ли такая статистика в бд,
     * и если есть, то возвращает ее
     *
     * @param statistics статистика на проверку
     * @return null если такой статистики нет в бд или существующая статистика
     */
    Statistics checkIfStatisticsExists(Statistics statistics);

    /**
     * метод отображает всю статистику без подробных деталей (статистика по каждому слову)
     */
    void showAllStatisticsWithoutDetails();

    /**
     * метод отображает всю статистику с деталями (статистика по каждому слову)
     * по id статистики
     *
     * @param id id статистики
     */
    void showStatisticsAndDetailsByStatisticsId(long id);

    /**
     * метод создает файл
     *
     * @param path по какому пути искать проверяемый файл
     */
    void checkFile(Path path);

    /**
     * метод проверяет, является ли строка URL
     *
     * @param url адрес для проверки
     * @return
     */
    boolean isUrlValid(String url);
}
