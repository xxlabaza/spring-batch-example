# Описание #

## Что такое Spring Batch? ##

**Spring Batch** предоставляет повторно используемые функции, которые необходимы в процессе обработки большого числа записей, включая логгирование/трассировку, управление транзакциями, обработка статистики, перезапуск и пропуск задач и управление ресурсами. Он также предоставляет продвинутые сервисы и возможности, которые включают в себя высокопроизводительные задачи обработки данных большого объема через техники оптимизации и разбиения. Как простые, так и сложные объемные пакетные задачи могут использовать фреймворк, который использует хорошо масштабируемый способ обработки значительных объемов информации.

Возможности:

* Управление транзакциями;
* chunk-обработка;
* Декларативный I/O;
* Запуск/Остановка/Перезапуск задач;
* Повтор/Пропуск шагов задач;
* Web-интерфейс панели администрирования ([Spring Batch Admin](https://github.com/codecentric/spring-boot-starter-batch-web)).

## Работа приложения ##

Во время запуска приложения, создаётся встроенная база данных **HSQLDB**, которая заполняется тестовыми данными пользователей (101 запись). После запуска, необходимо обратиться по единственному ендпоинту:

```bash
$> curl localhost:9090/
```

В ответ ничего не вернётся, но зато запустится выполнение задача **MyJob**, которая сообщает о ходе своего выполнения в консоль приложения.

### Описание MyJob ###

Задача **MyJob** состоит из трёх основных бинов, сконфигурённых в [MyJobBeanConfiguration](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/MyJobBeanConfiguration.java) и представляющих собой три элемента одного шага задачи типа **read-process-write** (помимо этого типа, существует ещё тип **tasklet**):

* **reader** - **JpaPagingItemReader**-объект, позволяющий читать из созданной **HSQLDB**-базы по заданному **JPQL**-запросу объекты типа [Person](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/model/Person.java). При создании, данный бин, из контекста выполнения, получает назначенные ему объектом [RangePartitioner](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/RangePartitioner.java) значения **from** и **to** - обозначающие диапазон для чтения из базы данных;
* **processor** - экземпляр класса [PersonProcessor](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/PersonProcessor.java), который представляет собой простой обработчик считаных **reader**'ом данных. Он преобразует экземпляр класса [Person](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/model/Person.java) в объект-обёртку [ThreadInfoPersonWrapper](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/ThreadInfoPersonWrapper.java), который содержит, помимо преобразуемого объекта - информация о имени потока, в котором происходило преобразование, и имя контекста;
* **writer** - объект [MyItemWriterToSystemOut](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/MyJobBeanConfiguration.java), добавляющий к экземпляру [ThreadInfoPersonWrapper](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/ThreadInfoPersonWrapper.java) информацию об имени потока, в котором происходит запись и выводящий список всех объектов на печать в стандартный поток вывода.

Вышеописанные бины объединены в шаг задачи **partitionedStep**, описанного в файле [MyJobBatchConfiguration](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/MyJobBatchConfiguration.java), который, в свою очередь, обёрнут в **masterStep** (собственно, единственный шаг задачи **myJob**), который реализует разделение входных данных (101 запись пользователей) при помощи бина класса [RangePartitioner](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/RangePartitioner.java).

Смысл всей этой хитрой конструкции прост - у нас есть источник данных (в нашем случае - таблица в **HSQLDB** базе) и нам нужно прочитать всё из него, для этого мы, при помощи [RangePartitioner](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/java/ru/xxlabaza/test/batch/job/RangePartitioner.java) разбиваем его на указанное количество частей, далее, в отдельных потоках происходит процесс чтения, обработки и записи. Настраивается всё это добро через [application.yml](https://github.com/xxlabaza/spring-batch-example/blob/master/src/main/resources/application.yml):

```yaml
...

app.batch:
    myJob:
      chunkSize:    10  // размер записей, обрабатываемых одновременно в рамках одной порции
      partitions:   5   // количество порций, на которое будет разбит считываемый источник
      threads:      4   // количество потоков, обрабатывающих цикл read-process-write
```

## Полезные ссылки ##

* [Быстрый туториал по Spring Batch](http://www.javacodegeeks.com/2015/03/spring-batch-tutorial.html);
* [Описание того, как можно скалировать задачи и отдельные шаги](http://stackoverflow.com/a/29108483), а не только разбивать на части в рамках одной **JVM**;
* [Тестирование Spring Batch приложений](https://blog.codecentric.de/en/2015/12/testing-spring-batch-applications/);
* [Советы, как готовить Spring Batch](https://blog.codecentric.de/en/2014/11/enterprise-java-batch-best-practice-architecture/);
* [Советы по xml-based конфигурации](https://blog.codecentric.de/en/2014/08/writing-jsr-352-style-jobs-spring-batch-part-1-configuration-options/).
