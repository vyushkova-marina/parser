package ru.parser.service;

import org.junit.jupiter.api.*;
import ru.parser.service.impl.ParserServiceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParserServiceImplTest {

    private ParserServiceImpl parserImpl;
    private final Path pathToFirstFile = Paths.get("./temporary files/beforeParsing.html");
    private final Path pathToSecondFile = Paths.get("./temporary files/testAfterParsing.html");

    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(pathToFirstFile.getParent());
        Files.createFile(pathToFirstFile);
        Files.createDirectories(pathToSecondFile.getParent());
        Files.createFile(pathToSecondFile);
        parserImpl = new ParserServiceImpl();
    }

    @AfterEach
    void afterEach() throws IOException {
        Files.delete(pathToFirstFile);
        Files.delete(pathToSecondFile);
    }

    /**
     * проверка, сохраняется ли http response в файл, если http request и URL корретно заданы
     *
     * @throws IOException
     */
    @Test
    void testSaveResponseToHtmlFileSaved() throws IOException {
        parserImpl.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        assertNotEquals(0, Files.size(pathToFirstFile));
    }

    /**
     * проверка, сохраняется ли http response в файл, если задан неправильный путь к файлу
     *
     * @throws IOException
     */
    @Test
    void testSaveResponseToHtmlFileNotSavedWrongPathToFile() throws IOException {
        Path path = Paths.get("./wrong/test.html");
        parserImpl.saveResponseToHtml(("http://example.com"), path);
        assertFalse(Files.exists(path));
    }

    /**
     * проверка, сохраняется ли http response в файл, если задан неправильный URL
     */
    @Test
    void testSaveResponseToHtmlFileNotSavedWrongUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> parserImpl.saveResponseToHtml(("qwerty"), pathToFirstFile));
    }

    /**
     * проверка, разделяется ли файл только разделителями, если заданы корректные аргументы
     *
     * @throws IOException
     */
    @Test
    void testSplitFileIntoWordsWithDelimitersOnlyFileParsed() throws IOException {
        parserImpl.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parserImpl.splitFileIntoWordsWithDelimitersOnly(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    /**
     * проверка, разделяется ли файл только разделителями, если файл для парсинга пустой
     *
     * @throws IOException
     */
    @Test
    void testSplitFileIntoWordsWithDelimitersOnlyEmptyFileForParsing() throws IOException {
        Path path = Paths.get("./temporary files/test.html");
        Files.createFile(path);
        parserImpl.splitFileIntoWordsWithDelimitersOnly(path, pathToSecondFile);
        assertEquals(Files.size(path), Files.size(pathToSecondFile));
        Files.delete(path);
    }

    /**
     * проверка, разделяется ли файл при использовании DOM-модели, если заданы корректные аргументы
     *
     * @throws IOException
     */
    @Test
    void testSplitFileIntoWordsUsingJsoupFileParsed() throws IOException {
        parserImpl.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parserImpl.splitFileIntoWordsUsingJsoup(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    /**
     * проверка, разделяется ли файл при использовании DOM-модели, если файл для парсинга пустой
     *
     * @throws IOException
     */
    @Test
    void testSplitFileIntoWordsUsingJsoupEmptyFileForParsing() throws IOException {
        parserImpl.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parserImpl.splitFileIntoWordsUsingJsoup(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    /**
     * проверка, разделяется ли файл, при использовании DOM-модели для строки, если заданы корректные аргументы
     *
     * @throws IOException
     */
    @Test
    void testSplitFileIntoWordsByReadingLinesAsBodyFragmentFileParsed() throws IOException {
        parserImpl.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parserImpl.splitFileIntoWordsByReadingLinesAsBodyFragment(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    /**
     * проверка, разделяется ли файл, при использовании DOM-модели для строки, если файл для прсинга пустой
     *
     * @throws IOException
     */
    @Test
    void test_splitFileIntoWordsByReadingLinesAsBodyFragmentEmptyFileForParsing() throws IOException {
        Path path = Paths.get("./temporary files/test.html");
        Files.createFile(path);
        parserImpl.splitFileIntoWordsByReadingLinesAsBodyFragment(path, pathToSecondFile);
        assertEquals(Files.size(path), Files.size(pathToSecondFile));
        Files.delete(path);
    }

    /**
     * проверка, корректно ли подсчитывается количество вхождений слов
     * @throws IOException
     */
    @Test
    void testCountWordsOk() throws IOException {
        Path path = Paths.get("./temporary files/test.html");
        Files.createFile(path);
        String string = "набор\nслов\nслов\nслов\nдля\nтеста\nнабор\nслов\nдля\nтеста";
        Files.write(path, Arrays.asList(string), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        Map<String, Integer> map = parserImpl.countWords(path);
        assertEquals(4, map.size());
        Files.delete(path);
    }
}