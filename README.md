### Очень краткое описание
Парсер сохраняет http response в файл, после чего разбивает его на слова одним из трех способов:
1. раздеяет весь файл на слова, включая теги
2. строит DOM-модель из всего файла, достает текст между тегов и разделяет его на слова
3. читает файл построчно, из строки строит DOM-модель и достает текст между тегов, после чего делит на слова

После разделения статистика сохраняется в базу данных, которая существует только пока приложение запущено

### Как запустить
- скачать исходники проекта 
    - загрузить проект в idea и запустить из нее
    - в папке с исходниками в командной строке выполнить команду `mvn clean package`
        - в папке target появится файл parser-0.0.1-SNAPSHOT.jar 
        - выполнить команду `java -jar parser-0.0.1-SNAPSHOT.jar`
    
