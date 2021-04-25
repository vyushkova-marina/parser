package ru.parser.service;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private Parser parser;
    private final Path pathToFirstFile = Paths.get("src\\test\\temporary files\\beforeParsing.html");
    private final Path pathToSecondFile = Paths.get("src\\test\\temporary files\\testAfterParsing.html");


    @BeforeEach
    void beforeEach() throws IOException {
        Files.createDirectories(pathToFirstFile.getParent());
        Files.createFile(pathToFirstFile);
        Files.createDirectories(pathToSecondFile.getParent());
        Files.createFile(pathToSecondFile);
        parser = new Parser();
    }


    @AfterEach
    void afterEach() throws IOException {
        Files.delete(pathToFirstFile);
        Files.delete(pathToSecondFile);
    }

    @Test
    void testSaveResponseToHtml_file_saved() throws IOException {
        parser.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        assertNotEquals(0, Files.size(pathToFirstFile));
    }

    @Test
    void testSaveResponseToHtml_file_not_saved_wrong_path_to_file() throws IOException {
        Path path = Paths.get("src\\test\\temp\\test.html");
        parser.saveResponseToHtml(("http://example.com"), path);
        assertFalse(Files.exists(path));
        Files.deleteIfExists(path);
    }

    @Test
    void testSaveResponseToHtml_file_not_saved_wrong_url() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.saveResponseToHtml(("qwerty"), pathToFirstFile));
    }


    @Test
    void testSplitFileIntoWordsWithDelimitersOnly_file_parsed() throws IOException {
        parser.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parser.splitFileIntoWordsWithDelimitersOnly(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));

    }

    @Test
    void testSplitFileIntoWordsWithDelimitersOnly_empty_file_for_parsing() throws IOException {
        Path path = Paths.get("src\\test\\temporary files\\test.html");
        Files.createFile(path);
        parser.splitFileIntoWordsWithDelimitersOnly(path, pathToSecondFile);
        assertEquals(Files.size(path), Files.size(pathToSecondFile));
        Files.delete(path);
    }

    @Test
    void testSplitFileIntoWordsUsingJsoup_file_parsed() throws IOException {
        parser.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parser.splitFileIntoWordsUsingJsoup(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    @Test
    void testSplitFileIntoWordsUsingJsoup_empty_file_for_parsing() throws IOException {
        Path path = Paths.get("src\\test\\temporary files\\test.html");
        Files.createFile(path);
        parser.splitFileIntoWordsUsingJsoup(path, pathToSecondFile);
        assertEquals(Files.size(path), Files.size(pathToSecondFile));
        Files.delete(path);
    }

    @Test
    void testSplitFileIntoWordsByReadingLinesAsBodyFragment_file_parsed() throws IOException {
        parser.saveResponseToHtml(("http://example.com"), pathToFirstFile);
        parser.splitFileIntoWordsByReadingLinesAsBodyFragment(pathToFirstFile, pathToSecondFile);
        assertNotEquals(0, Files.size(pathToSecondFile));
    }

    @Test
    void test_splitFileIntoWordsByReadingLinesAsBodyFragment_empty_file_for_parsing() throws IOException {
        Path path = Paths.get("src\\test\\temporary files\\test.html");
        Files.createFile(path);
        parser.splitFileIntoWordsByReadingLinesAsBodyFragment(path, pathToSecondFile);
        assertEquals(Files.size(path), Files.size(pathToSecondFile));
        Files.delete(path);
    }

    @Test
    void testCountWords_ok() throws IOException {
        Path path = Paths.get("src\\test\\temporary files\\test.html");
        Files.createFile(path);
        String string = "набор\nслов\nслов\nслов\nдля\nтеста\nнабор\nслов\nдля\nтеста";
        Files.write(path, Arrays.asList(string), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        Map<String, Integer> map = parser.countWords(path);
        assertEquals(4, map.size());
        Files.delete(path);
    }
}