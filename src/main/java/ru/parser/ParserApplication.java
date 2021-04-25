package ru.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.parser.information.TypeOfStrategy;
import ru.parser.service.Client;

import java.util.Scanner;

@Slf4j
@SpringBootApplication
public class ParserApplication implements CommandLineRunner {

    @Autowired
    Client client;


    public static void main(String[] args) {
        SpringApplication.run(ParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String str;
        boolean check;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                do {
                    System.out.println("\nдля выхода нажмите q");
                    System.out.println("введите url:");
                    if ((str = scanner.nextLine()).equals("q")) {
                        return;
                    }
                    check = client.isUrlValid(str);
                    if (!check) {
                        System.out.println("вы ввели не url");//если просто набор букв
                    } else {
                        check = client.downloadPage(str);
                        if (!check) {
                            System.out.println("попробуйте другой url");//если сайт не существует/недоступен
                        } else {
                            System.out.println("\nзапрос сохраняется в файл...\n");
                        }
                    }
                } while (!check);

                System.out.println("выберите вариант парсинга (цифру):");
                System.out.println("1 - " + TypeOfStrategy.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY);
                System.out.println("2 - " + TypeOfStrategy.SPLIT_FILE_INTO_WORDS_USING_JSOUP);
                System.out.println("3 - " + TypeOfStrategy.SPLIT_FILE_INTO_WORDS_BY_READING_LINES_AS_BODY_FRAGMENT);

                if ((str = scanner.nextLine()).equals("q")) return;
                switch (str) {
                    case ("1"):
                        client.parse(TypeOfStrategy.SPLIT_FILE_INTO_WORDS_WITH_DELIMITERS_ONLY);
                        break;
                    case ("2"):
                        client.parse(TypeOfStrategy.SPLIT_FILE_INTO_WORDS_USING_JSOUP);
                        break;
                    case ("3"):
                        client.parse(TypeOfStrategy.SPLIT_FILE_INTO_WORDS_BY_READING_LINES_AS_BODY_FRAGMENT);
                        break;
                }

                client.saveStatistics();
                client.showAllStatisticsWithoutDetails();
                System.out.println("хотите продолжить(1) или посмотреть имеющуюся статистику(2)?");
                if ((str = scanner.nextLine()).equals("q")) return;
                if (str.equals("2")) {
                    System.out.println("введите id статистики, которую хотите посмотреть");
                    if ((str = scanner.nextLine()).equals("q")) return;
                    client.showStatisticsAndDetailsByStatisticsId(Long.parseLong(str));
                }
            }
        }
    }
}
