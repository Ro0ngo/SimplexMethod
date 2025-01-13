# Решение ЗЛП симплекс-методом

#### Задание по курсу "Методы оптимизации". Построение симплекс-таблицы с помощью метода Гаусса(выбор базисных переменных) и искусственными базисными переменными

## Клонирование проекта
```
git clone https://github.com/Ro0ngo/SimplexMethod.git
```

## Запуск проекта
```
./gradlew run
```
```
gradle run
```

## Добавление JRE
#### Чтобы создать исполняемый файл (EXE), необходимо создать папку `JRE/` в корне проекта и скачать JDK в эту папку:
- **x64 Installer**:
    - [Java SE Development Kit 23](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
    - [Скачать JDK 23 для Windows x64](https://download.oracle.com/java/23/latest/jdk-23_windows-x64_bin.exe)

## Создание EXE файла
#### После того как вы добавили JRE, вы можете создать исполняемый файл:
```
./gradlew createExe
```
```
gradle createExe
```