package simplexmetod;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.beans.value.ChangeListener;

import java.io.*;
import java.util.*;

public class Control extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab taskTab = new Tab("Постановка задачи");
        VBox taskLayout = new VBox(10);
        taskLayout.setAlignment(Pos.TOP_CENTER);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Файл");
        MenuItem openItem = new MenuItem("Открыть");
        MenuItem saveItem = new MenuItem("Сохранить");
        MenuItem exitItem = new MenuItem("Выход");

        fileMenu.getItems().addAll(openItem, saveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);

        exitItem.setOnAction(event -> primaryStage.close());

        Menu helpMenu = new Menu("Помощь");
        MenuItem howToUseItem = new MenuItem("Как пользоваться приложением");
        MenuItem aboutItem = new MenuItem("О программе");

        helpMenu.getItems().addAll(howToUseItem, aboutItem);
        menuBar.getMenus().add(helpMenu);

        howToUseItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Как пользоваться приложением");
            alert.setHeaderText(null);
            alert.setContentText("""
                    1. Выберите количество переменных.
                    2. Укажите количество базисных переменных.
                    3. Выберите тип задачи (min/max).
                    4. Введите целевую функцию и матрицу.
                    5. Нажмите 'Применить' для обработки данных.
                    6. Используйте 'Сохранить' для сохранения результатов.
                    7. Используйте 'Открыть' для получения результатов из файла.
                    8. Нажмите 'Сбросить' для сброса данных.
                    """);
            alert.showAndWait();
        });

        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("О программе");
            alert.setHeaderText(null);
            alert.setContentText("Это приложение для решения задач оптимизации.\nВерсия 1.0");
            alert.showAndWait();
        });

        ComboBox<Integer> numberComboBox = new ComboBox<>();
        numberComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        ComboBox<String> minMaxComboBox = new ComboBox<>();
        minMaxComboBox.getItems().addAll("min", "max");
        minMaxComboBox.setValue("min");

        ComboBox<Integer> basisVarsComboBox = new ComboBox<>();

        ComboBox<String> fractionTypeComboBox = new ComboBox<>();
        fractionTypeComboBox.getItems().addAll("Обыкновенные", "Десятичные");
        fractionTypeComboBox.setValue("Обыкновенные");

        VBox goalFunctionBox = new VBox(10);
        Label goalFunctionLabel = new Label("Введите целевую функцию:");
        goalFunctionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: blue;");
        goalFunctionBox.getChildren().add(goalFunctionLabel);

        HBox goalFunctionFields = new HBox(5);
        goalFunctionFields.setSpacing(5);

        goalFunctionBox.setVisible(false);
        goalFunctionFields.setVisible(false);

        VBox matrixBox = new VBox(10);
        Label matrixLabel = new Label("Введите матрицу:");
        matrixLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: blue;");
        matrixBox.getChildren().add(matrixLabel);
        matrixBox.setVisible(false);

        Label errorMessage = new Label();
        errorMessage.setTextFill(Color.RED);
        errorMessage.setVisible(false);

        Label rowEnum = new Label("              ");

        Button applyButton = new Button("Применить");
        Button resetButton = new Button("Сбросить");
        HBox buttonContainer = new HBox(10, applyButton, resetButton);


        numberComboBox.setOnAction(event -> {
            try {
                goalFunctionFields.getChildren().clear();
                goalFunctionBox.setVisible(true);
                goalFunctionFields.setVisible(true);

                int count = numberComboBox.getValue();

                basisVarsComboBox.getItems().clear();
                for (int i = 1; i <= count; i++) {
                    basisVarsComboBox.getItems().add(i);
                }
                basisVarsComboBox.setValue(1);

                List<BooleanProperty> validityList = new ArrayList<>();

                for (int i = 0; i < count + 1; i++) {
                    HBox fieldContainer = new HBox(5);
                    fieldContainer.setAlignment(Pos.CENTER);
                    TextField textField = new TextField();
                    textField.setPrefWidth(50);

                    BooleanProperty isValid = new SimpleBooleanProperty(true);
                    validityList.add(isValid);

                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        try {
                            String fractionPattern = "^-?\\d+/\\d+$";
                            String noLeadingZeroPattern = "^-?(0|([1-9][0-9]*))$";
                            String noLeadingZeroInFractionPattern = "^-?([1-9][0-9]*)/([1-9][0-9]*)$";

                            if (newValue.isEmpty()) {
                                textField.setStyle(""); // Пустое поле считается валидным
                                errorMessage.setVisible(false);
                                isValid.set(true);
                            } else if (newValue.matches(noLeadingZeroPattern)) {
                                textField.setStyle("");
                                errorMessage.setVisible(false);
                                isValid.set(true);
                            } else if (newValue.matches(fractionPattern)) {
                                String[] parts = newValue.split("/");
                                try {
                                    int denominator = Integer.parseInt(parts[1]);
                                    if (denominator == 0) {
                                        textField.setStyle("-fx-border-color: red;");
                                        errorMessage.setText("Знаменатель не может быть равен 0");
                                        errorMessage.setVisible(true);
                                        isValid.set(false);
                                    } else if (!newValue.matches(noLeadingZeroInFractionPattern)) {
                                        textField.setStyle("-fx-border-color: red;");
                                        errorMessage.setText("В дроби не могут быть ведущие нули");
                                        errorMessage.setVisible(true);
                                        isValid.set(false);
                                    } else {
                                        textField.setStyle("");
                                        errorMessage.setVisible(false);
                                        isValid.set(true);
                                    }
                                } catch (NumberFormatException e) {
                                    textField.setStyle("-fx-border-color: red;");
                                    errorMessage.setText("Некорректный ввод");
                                    errorMessage.setVisible(true);
                                    isValid.set(false);
                                }
                            } else {
                                textField.setStyle("-fx-border-color: red;");
                                errorMessage.setText("Неверный формат. Введите число или дробь (число/число)");
                                errorMessage.setVisible(true);
                                isValid.set(false);
                            }
                        } catch (Exception ignored) {
                        }
                    });

                    Label variableLabel = new Label("x" + (i == count ? "0" : (i + 1)));

                    VBox centeredContainer = new VBox(5);
                    centeredContainer.setAlignment(Pos.CENTER);
                    centeredContainer.getChildren().addAll(variableLabel, textField);
                    fieldContainer.getChildren().add(centeredContainer);

                    goalFunctionFields.getChildren().add(fieldContainer);
                }

                applyButton.disableProperty().bind(Bindings.createBooleanBinding(
                        () -> validityList.stream().anyMatch(valid -> !valid.get()),
                        validityList.toArray(new Observable[]{})
                ));

            } catch (Exception ignored) {
            }
        });

        List<List<TextField>> matrixFields = new ArrayList<>();

        basisVarsComboBox.setOnAction(event -> {
            try {
                if (numberComboBox.getValue() != null && basisVarsComboBox.getValue() != null) {
                    int variablesCount = numberComboBox.getValue();
                    int freeVarsCount = basisVarsComboBox.getValue();

                    matrixBox.setVisible(true);
                    VBox matrixInputFields = new VBox(5);
                    matrixInputFields.setSpacing(5);
                    matrixBox.getChildren().clear();
                    matrixBox.getChildren().add(matrixLabel);

                    HBox columnHeaders = new HBox(10);
                    columnHeaders.setAlignment(Pos.CENTER_LEFT);

                    columnHeaders.getChildren().add(rowEnum);

                    for (int i = 0; i < variablesCount + 1; i++) {
                        VBox centeredContainer = new VBox(5);
                        centeredContainer.setAlignment(Pos.CENTER);

                        Label columnLabel = new Label("x" + (i == variablesCount ? "0" : (i + 1)));
                        columnLabel.setMinWidth(50);

                        centeredContainer.getChildren().add(columnLabel);
                        columnHeaders.getChildren().add(centeredContainer);
                    }

                    matrixBox.getChildren().add(columnHeaders);
                    matrixFields.clear();

                    for (int i = 0; i < freeVarsCount; i++) {
                        HBox row = new HBox(10);
                        row.setAlignment(Pos.CENTER_LEFT);

                        Label rowLabel = new Label("f" + (i + 1) + "(x):");
                        row.getChildren().add(rowLabel);

                        List<TextField> rowFields = new ArrayList<>();

                        for (int j = 0; j < variablesCount + 1; j++) {
                            TextField matrixCell = new TextField();
                            matrixCell.setPrefWidth(50);
                            row.getChildren().add(matrixCell);

                            matrixCell.textProperty().addListener((observable, oldValue, newValue) -> {
                                try {
                                    String fractionPattern = "^-?\\d+/\\d+$";
                                    String noLeadingZeroPattern = "^-?(0|([1-9][0-9]*))$";
                                    String noLeadingZeroInFractionPattern = "^-?([1-9][0-9]*)/([1-9][0-9]*)$";

                                    if (newValue.isEmpty()) {
                                        matrixCell.setStyle("");
                                        errorMessage.setVisible(false);
                                    } else if (newValue.matches(noLeadingZeroPattern)) {
                                        matrixCell.setStyle("");
                                        errorMessage.setVisible(false);
                                    } else if (newValue.matches(fractionPattern)) {
                                        String[] parts = newValue.split("/");
                                        try {
                                            int denominator = Integer.parseInt(parts[1]);
                                            if (denominator == 0) {
                                                matrixCell.setStyle("-fx-border-color: red;");
                                                errorMessage.setText("Знаменатель не может быть равен 0");
                                                errorMessage.setVisible(true);
                                            } else if (!newValue.matches(noLeadingZeroInFractionPattern)) {
                                                matrixCell.setStyle("-fx-border-color: red;");
                                                errorMessage.setText("В дроби не могут быть ведущие нули");
                                                errorMessage.setVisible(true);
                                            } else {
                                                matrixCell.setStyle("");
                                                errorMessage.setVisible(false);
                                            }
                                        } catch (NumberFormatException e) {
                                            matrixCell.setStyle("-fx-border-color: red;");
                                            errorMessage.setText("Некорректный ввод");
                                            errorMessage.setVisible(true);
                                        }
                                    } else {
                                        matrixCell.setStyle("-fx-border-color: red;");
                                        errorMessage.setText("Неверный формат. Введите число или дробь (число/число)");
                                        errorMessage.setVisible(true);
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                            rowFields.add(matrixCell);
                        }
                        matrixFields.add(rowFields);
                        matrixInputFields.getChildren().add(row);
                    }
                    matrixBox.getChildren().add(matrixInputFields);
                }
            } catch (Exception ignored) {
            }
        });

        HBox topRow = new HBox(10);
        topRow.getChildren().addAll(new Label("Кол-во переменных:"), numberComboBox, new Label("Кол-во базисных:"), basisVarsComboBox, new Label("Задача на:"), minMaxComboBox, new Label("Вид дроби:"), fractionTypeComboBox);
        topRow.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-background-radius: 5;");
        topRow.setAlignment(Pos.CENTER_LEFT);

        buttonContainer.setStyle("-fx-padding: 0 0 10 0;");
        buttonContainer.setAlignment(Pos.CENTER);

        applyButton.getStyleClass().add("apply-button");
        resetButton.getStyleClass().add("reset-button");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        taskLayout.getChildren().addAll(menuBar, topRow, goalFunctionBox, goalFunctionFields, matrixBox, errorMessage, spacer, buttonContainer);

        saveItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить данные");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                saveToFile(file, collectData(numberComboBox,
                        basisVarsComboBox,
                        minMaxComboBox,
                        fractionTypeComboBox,
                        goalFunctionFields,
                        matrixFields));
            }
        });

        openItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл для загрузки");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                loadData(
                        file.getAbsolutePath(),
                        numberComboBox,
                        basisVarsComboBox,
                        minMaxComboBox,
                        fractionTypeComboBox,
                        goalFunctionFields,
                        matrixFields);
            }
        });

        taskTab.setContent(taskLayout);
        taskTab.setClosable(false);

        Tab freeVarsTab = new Tab("Свободные переменные");
        freeVarsTab.setClosable(false);

        Tab basicVarsTab = new Tab("Базисные переменные");
        basicVarsTab.setContent(new Label("Здесь будут базисные переменные"));
        basicVarsTab.setClosable(false);

        freeVarsTab.setContent(new StackPane(new Text("Необходимо выбрать данные во вкладке 'Постановки задачи' и нажать на кнопку 'Применить'")));
        basicVarsTab.setContent(new StackPane(new Text("Необходимо выбрать данные во вкладке 'Постановки задачи' и нажать на кнопку 'Применить'")));

        applyButton.setOnAction(e -> {
            String data = collectData(numberComboBox, basisVarsComboBox, minMaxComboBox, fractionTypeComboBox, goalFunctionFields, matrixFields);
            freeVarsTab.setContent(createBasisVariablesTab(numberComboBox.getValue(), basisVarsComboBox.getValue(), matrixFields, goalFunctionFields, minMaxComboBox, fractionTypeComboBox.getValue().equals("Десятичные")));
            basicVarsTab.setContent(createArtificialBasis(matrixFields, goalFunctionFields, minMaxComboBox, fractionTypeComboBox.getValue().equals("Десятичные")));
            System.out.println(data);
        });

        applyButton.setDisable(true);
        resetButton.setOnAction(event -> {
            numberComboBox.setValue(null);
            basisVarsComboBox.getItems().clear();
            minMaxComboBox.setValue("min");
            fractionTypeComboBox.setValue("Обыкновенные");

            goalFunctionBox.setVisible(false);
            goalFunctionFields.getChildren().clear();
            matrixBox.setVisible(false);
            matrixFields.forEach(row -> row.forEach(TextInputControl::clear));

            freeVarsTab.setContent(new StackPane(new Text("Необходимо выбрать данные во вкладке 'Постановки задачи' и нажать на кнопку 'Применить'")));
            errorMessage.setVisible(false);

        });

        tabPane.getTabs().addAll(taskTab, freeVarsTab, basicVarsTab);

        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        primaryStage.setTitle("Задача линейного программирования");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Сохраняет данные в файл.
     * @param file Файл.
     * @param data Данные.
     */
    private void saveToFile(File file, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка сохранения");
            alert.setHeaderText(null);
            alert.setContentText("Не удалось сохранить файл: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Получение матрицы из виджетов.
     * @param matrixFields Матрица.
     * @return Преобразованная матрица.
     */
    private List<List<Fraction>> getMatrixData(List<List<TextField>> matrixFields) {
        List<List<Fraction>> matrixData = new ArrayList<>();
        try {
            for (List<TextField> rowFields : matrixFields) {
                List<Fraction> rowData = new ArrayList<>();
                for (TextField cell : rowFields) {
                    String text = cell.getText().trim();
                    if (text.isEmpty()) {
                        rowData.add(Fraction.ZERO);
                    } else {
                        rowData.add(Fraction.fromString(text));
                    }
                }
                matrixData.add(rowData);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: некорректное значение в матрице!");
        }
        return matrixData;
    }

    /**
     * Получение данных из первой вкладки для сохранения.
     * @param numberComboBox Кол-во строк матрицы.
     * @param basisVarsComboBox Кол-во столбцов матрицы.
     * @param minMaxComboBox Минимизация или максимизация целевой функции.
     * @param fractionTypeComboBox Какой вид дроби интересует (обыкновенная или десятичная).
     * @param goalFunctionFields Целевая функция.
     * @param matrixFields Матрица.
     * @return Возвращает строковое преставление этих данных.
     */
    private String collectData(ComboBox<Integer> numberComboBox, ComboBox<Integer> basisVarsComboBox, ComboBox<String> minMaxComboBox, ComboBox<String> fractionTypeComboBox, HBox goalFunctionFields, List<List<TextField>> matrixFields) {
        if (numberComboBox.getValue() == null || basisVarsComboBox.getValue() == null || minMaxComboBox.getValue() == null || fractionTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
            showError("Пожалуйста, заполните обязательные поля.");
            return null;
        }

        Integer numberOfVariables = numberComboBox.getValue();
        Integer numberOfFreeVars = basisVarsComboBox.getValue();
        String taskType = minMaxComboBox.getValue();
        int selectedIndex = fractionTypeComboBox.getSelectionModel().getSelectedIndex();
        int fractionType = selectedIndex == 0 ? 0 : 1;

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append(numberOfVariables).append("\n")
                .append(numberOfFreeVars).append("\n")
                .append(taskType).append("\n")
                .append(fractionType).append("\n");

        Fraction[] goalFunctionValues = new Fraction[numberOfVariables + 1];
        for (int i = 0; i < goalFunctionFields.getChildren().size(); i++) {
            HBox fieldContainer = (HBox) goalFunctionFields.getChildren().get(i);
            Node variableContainer = fieldContainer.getChildren().getFirst();
            if (variableContainer instanceof VBox vbox) {
                TextField textField = (TextField) vbox.getChildren().get(1);
                String input = textField.getText().trim();
                if (input.isEmpty()) {
                    goalFunctionValues[i] = Fraction.ZERO;
                } else {
                    goalFunctionValues[i] = Fraction.fromString(input);
                }
            } else if (variableContainer instanceof HBox hbox) {
                TextField textField = (TextField) hbox.getChildren().get(1);
                String input = textField.getText().trim();
                if (input.isEmpty()) {
                    goalFunctionValues[i] = Fraction.ZERO;
                } else {
                    goalFunctionValues[i] = Fraction.fromString(input);
                }
            }
        }
        for (Fraction value : goalFunctionValues) {
            dataBuilder.append(value).append(" ");
        }
        dataBuilder.append("\n");

        List<List<Fraction>> matrixData = getMatrixData(matrixFields);
        for (List<Fraction> row : matrixData) {
            for (Fraction value : row) {
                dataBuilder.append(value).append(" ");
            }
            dataBuilder.append("\n");
        }

        return dataBuilder.toString();
    }

    /**
     * Чтение данных из файла
     * @param filePath Путь до файла.
     * @param numberComboBox Кол-во строк матрицы.
     * @param basisVarsComboBox Кол-во столбцов матрицы.
     * @param minMaxComboBox Минимизация или максимизация целевой функции.
     * @param fractionTypeComboBox Вид дроби.
     * @param goalFunctionFields Целевая функция.
     * @param matrixFields Матрица.
     */
    private void loadData(
            String filePath,
            ComboBox<Integer> numberComboBox,
            ComboBox<Integer> basisVarsComboBox,
            ComboBox<String> minMaxComboBox,
            ComboBox<String> fractionTypeComboBox,
            HBox goalFunctionFields,
            List<List<TextField>> matrixFields
    ) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int numberOfVariables = Integer.parseInt(reader.readLine());
            int numberOfFreeVars = Integer.parseInt(reader.readLine());

            if (numberOfVariables < numberOfFreeVars) {
                throw new IllegalArgumentException("Количество переменных не может быть меньше количества базисных переменных.");
            }

            numberComboBox.setValue(numberOfVariables);
            basisVarsComboBox.setValue(numberOfFreeVars);

            String taskType = reader.readLine().trim();
            if (!taskType.equals("min") && !taskType.equals("max")) {
                throw new IllegalArgumentException("Тип задачи должен быть 'min' или 'max'.");
            }

            int fractionType = Integer.parseInt(reader.readLine().trim());
            if (fractionType != 0 && fractionType != 1) {
                throw new IllegalArgumentException("Тип дроби должен быть 0 (для обыкновенных) или 1 (для десятичных).");
            }

            String[] goalFunctionValues = reader.readLine().split(" ");
            if (goalFunctionValues.length != numberOfVariables + 1) {
                throw new IllegalArgumentException("Количество значений целевой функции должно быть равно количеству переменных + 1.");
            }

            List<String[]> matrixRows = new ArrayList<>();
            for (List<TextField> rowFields : matrixFields) {
                String[] rowValues = reader.readLine().split(" ");
                if (rowValues.length != rowFields.size()) {
                    throw new IllegalArgumentException("Количество значений в строке матрицы не совпадает с количеством полей.");
                }
                matrixRows.add(rowValues);
            }

            minMaxComboBox.setValue(taskType);
            fractionTypeComboBox.getSelectionModel().select(fractionType);

            for (int i = 0; i < goalFunctionValues.length; i++) {
                HBox fieldContainer = (HBox) goalFunctionFields.getChildren().get(i);
                Node variableContainer = fieldContainer.getChildren().getFirst();
                String value = goalFunctionValues[i];

                if (variableContainer instanceof VBox vbox) {
                    TextField textField = (TextField) vbox.getChildren().get(1);
                    textField.setText(value);
                } else if (variableContainer instanceof HBox hbox) {
                    TextField textField = (TextField) hbox.getChildren().get(1);
                    textField.setText(value);
                }
            }

            for (int i = 0; i < matrixRows.size(); i++) {
                String[] rowValues = matrixRows.get(i);
                for (int j = 0; j < rowValues.length; j++) {
                    matrixFields.get(i).get(j).setText(rowValues[j]);
                }
            }

        } catch (IOException | NumberFormatException e) {
            showError("Ошибка при загрузке файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Вывод ошибки на экран
     * @param text Текст для ошибки
     */
    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /**
     * Метод для работы с тнерьей вкладкой(базисными переменными).
     * @param matrixFields Матрица.
     * @param goalFunctionFields Целевая функция.
     * @param minMaxComboBox Минимизация или максимизация.
     * @param isDecimal Вид дроби.
     * @return Отрисованная вкладка.
     */
    private BorderPane createArtificialBasis(List<List<TextField>> matrixFields,
                                             HBox goalFunctionFields, ComboBox<String> minMaxComboBox,
                                             boolean isDecimal) {
        int variableCount = matrixFields.getFirst().size();
        int maxSelectable = matrixFields.size();

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        VBox matrixContainer = new VBox(10);
        matrixContainer.setAlignment(Pos.CENTER);

        TextArea matrixArea = new TextArea();
        matrixArea.setEditable(false);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        Button backButton = new Button("Назад");
        Button solveButton = new Button("Решение");
        backButton.getStyleClass().add("action-button");
        solveButton.getStyleClass().add("action-button");
        solveButton.setDisable(true);
        buttonsBox.getChildren().addAll(backButton, solveButton);

        boolean[] booleanArray = new boolean[variableCount+maxSelectable-1];
        for (int i = 0; i < variableCount-1; i++) {
            booleanArray[i] = false;
        }
        for (int i = variableCount-1; i <= variableCount+maxSelectable-2; i++){
            booleanArray[i] = true;
        }

        List<Boolean> selectedValues = new ArrayList<>(booleanArray.length-1);
        for (boolean b : booleanArray) {
            selectedValues.add(b);
        }

        try {
            matrixContainer.getChildren().clear();
            List<SimplexMethod> simplexMethodList = new ArrayList<>();
            Fraction[] targetFunction = extractGoalFunctionValues(goalFunctionFields);

            List<List<String>> stringMatrix = convertTextFieldsToStringMatrix(matrixFields);
            Matrix matrixFromFields = convertToMatrix(stringMatrix);

            if (minMaxComboBox.getValue() != null && minMaxComboBox.getValue().equals("max")) {
                for (int i = 0; i < targetFunction.length; i++) {
                    targetFunction[i] = targetFunction[i].multiply(Fraction.NEGATIVE_ONE);
                }
            }

            Fraction[] newTargetFunction = new Fraction[targetFunction.length];
            matrixFromFields.printMatrix();
            for (int i = 0; i < newTargetFunction.length; i++) {
                newTargetFunction[i] = matrixFromFields.getValueFromCol(i).multiply(Fraction.NEGATIVE_ONE);
            }

            List<Integer> freeVector = matrixFromFields.isFreeVector(booleanArray);

            matrixFromFields.addRow(newTargetFunction);

            SimplexMethod table = new SimplexMethod(matrixFromFields,
                    newTargetFunction, matrixFromFields.isBasicVector(booleanArray),
                    matrixFromFields.isFreeVector(booleanArray));
            table.updateTable();

            processAndDisplayMatrix(table, simplexMethodList, matrixArea, matrixContainer, selectedValues, minMaxComboBox, false, backButton, isDecimal);

            System.out.println("--------------");
            freeVector = removeElement(freeVector, 0);
            table = new SimplexMethod(matrixFromFields, matrixFromFields.isBasicVector(booleanArray), freeVector);
            table.printTable();
            System.out.println("-------------");


        } catch (IllegalArgumentException e) {
            showError("Ошибка: " + e.getMessage());
        }

        borderPane.setCenter(matrixContainer);
        borderPane.setBottom(buttonsBox);

        return borderPane;
    }

    /**
     * Метод для отрисовки второй вкладки(свободные переменные).
     * @param variableCount Кол-во переменных.
     * @param maxSelectable Кол-во базисных переменных.
     * @param matrixFields Матрица.
     * @param goalFunctionFields Целевая функция.
     * @param minMaxComboBox Минимизация или максимизация.
     * @param isDecimal Тип дроби.
     * @return Отрисованная вкладка.
     */
    private BorderPane createBasisVariablesTab(int variableCount, int maxSelectable,
                                               List<List<TextField>> matrixFields,
                                               HBox goalFunctionFields, ComboBox<String> minMaxComboBox,
                                               boolean isDecimal) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        List<Boolean> selectedValues = new ArrayList<>(variableCount);

        List<SimplexMethod> tableList = new ArrayList<>();

        HBox variablesBox = new HBox(10);
        variablesBox.setPadding(new Insets(10));
        variablesBox.setAlignment(Pos.CENTER);

        for (int i = 1; i <= variableCount; i++) {
            VBox variableBox = new VBox(5);
            Label label = new Label("X" + i);
            CheckBox checkBox = new CheckBox();
            variableBox.getChildren().addAll(label, checkBox);
            variableBox.setAlignment(Pos.CENTER);
            variablesBox.getChildren().add(variableBox);
            checkBoxes.add(checkBox);
            selectedValues.add(false);
        }

        Label instructionLabel = new Label("Выберите базисные переменные:");
        instructionLabel.setPadding(new Insets(0, 10, 0, 0));

        HBox textAndVariablesBox = new HBox(10);
        textAndVariablesBox.setAlignment(Pos.CENTER_LEFT);
        textAndVariablesBox.getChildren().addAll(instructionLabel, variablesBox);
        textAndVariablesBox.setSpacing(20);

        VBox matrixContainer = new VBox(10);
        matrixContainer.setAlignment(Pos.CENTER);

        TextArea matrixArea = new TextArea();
        matrixArea.setEditable(false);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        Button backButton = new Button("Назад");
        Button solveButton = new Button("Решение");
        backButton.getStyleClass().add("action-button");
        solveButton.getStyleClass().add("action-button");
        solveButton.setDisable(true);
        buttonsBox.getChildren().addAll(backButton, solveButton);

        ChangeListener<Boolean> checkBoxListener = (observable, oldValue, newValue) -> {
            long selectedCount = checkBoxes.stream().filter(CheckBox::isSelected).count();

            for (int j = 0; j < checkBoxes.size(); j++) {
                CheckBox checkBox = checkBoxes.get(j);
                selectedValues.set(j, checkBox.isSelected());
                checkBox.setDisable(!checkBox.isSelected() && selectedCount >= maxSelectable);
            }

            solveButton.setDisable(selectedCount < maxSelectable);

            try {
                if (selectedCount == maxSelectable) {
                    matrixContainer.getChildren().clear();
                    List<SimplexMethod> simplexMethodList = new ArrayList<>();

                    boolean[] booleanArray = new boolean[selectedValues.size()];
                    for (int i = 0; i < selectedValues.size(); i++) {
                        booleanArray[i] = selectedValues.get(i);
                    }
                    boolean[] vectorCopy = Arrays.copyOf(booleanArray, booleanArray.length);
                    Fraction[] targetFunction = extractGoalFunctionValues(goalFunctionFields);

                    List<List<String>> stringMatrix = convertTextFieldsToStringMatrix(matrixFields);
                    Matrix matrixFromFields = convertToMatrix(stringMatrix);
                    matrixFromFields.rendererColumn(booleanArray);
                    if (matrixFromFields.gauss()) {
                        throw new IllegalArgumentException("Вырожденные строки или столбцы");
                    }

                    matrixFromFields.restoreColumnOrderWithVector();

                    if (minMaxComboBox.getValue() != null && minMaxComboBox.getValue().equals("max")) {
                        for (int i = 0; i < targetFunction.length; i++) {
                            targetFunction[i] = targetFunction[i].multiply(Fraction.NEGATIVE_ONE);
                        }
                    }

                    matrixFromFields.addRow(matrixFromFields.solution(targetFunction, matrixFromFields.isBasicVector(vectorCopy), matrixFromFields.isFreeVector(vectorCopy)));
                    matrixFromFields.removeColumns(matrixFromFields.isBasicVector(vectorCopy));
                    SimplexMethod table = new SimplexMethod(matrixFromFields,
                            targetFunction, matrixFromFields.isBasicVector(vectorCopy),
                            matrixFromFields.isFreeVector(vectorCopy));
                    table.updateTable();

                    solveButton.setOnAction(event -> {
                        backButton.setDisable(true);
                        tableList.add(processAndDisplayMatrix(table, simplexMethodList, matrixArea, matrixContainer, selectedValues, minMaxComboBox, true, backButton, isDecimal));
                    });

                    tableList.add(processAndDisplayMatrix(table, simplexMethodList, matrixArea, matrixContainer, selectedValues, minMaxComboBox, false, backButton, isDecimal));

                    System.out.println("_____");
                    tableList.getLast().printTable();
                    System.out.println("__________");
                } else {
                    matrixContainer.getChildren().clear();
                }
            } catch (IllegalArgumentException e) {
                showError("Ошибка: " + e.getMessage());
            }
        };

        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(checkBoxListener);
        }

        borderPane.setTop(textAndVariablesBox);
        borderPane.setCenter(matrixContainer);
        borderPane.setBottom(buttonsBox);

        BorderPane.setAlignment(buttonsBox, Pos.CENTER);
        BorderPane.setMargin(buttonsBox, new Insets(10));

        return borderPane;
    }

    /**
     * Метод для отрисовки матрицы
     * @param container Контейнер.
     * @param rowLabels Вектор свободных переменных.
     * @param columnLabels Вектор базисных переменных.
     * @param matrix Матрица.
     * @param supportElements Опорные элементы.
     * @param pivotElement Лучший опорный элемент.
     * @param table Таблица для симплекс метода.
     * @param simplexMethodList Вектор из таблиц.
     * @param matrixArea Виджет для отрисоки ответа.
     * @param matrixContainer Контейнер для матрицы.
     * @param selectedValues Базисные переменные.
     * @param minMaxComboBox Минимизация или максимизация.
     * @param isDecision Является ли ответом.
     * @param backButton Кнопка назад.
     * @param isDecimal Тип дроби.
     */
    private void drawStyledButtonMatrix(
            VBox container,
            List<String> rowLabels,
            List<String> columnLabels,
            List<List<String>> matrix,
            List<int[]> supportElements,
            int[] pivotElement,
            SimplexMethod table,
            List<SimplexMethod> simplexMethodList,
            TextArea matrixArea,
            VBox matrixContainer,
            List<Boolean> selectedValues,
            ComboBox<String> minMaxComboBox,
            boolean isDecision,
            Button backButton,
            boolean isDecimal
    ) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        int rows = rowLabels.size() + 1;
        int columns = columnLabels.size() + 1;

        for (int j = 0; j < columns; j++) {
            Label columnHeader = new Label(j == columns - 1 ? "const" : columnLabels.get(j));
            columnHeader.getStyleClass().add("header-style");
            columnHeader.setMaxWidth(Double.MAX_VALUE);
            columnHeader.setAlignment(Pos.CENTER);
            gridPane.add(columnHeader, j + 1, 0);
        }

        for (int i = 0; i < rows; i++) {
            Label rowHeader = new Label(i == rows - 1 ? "f(x)" : rowLabels.get(i));
            rowHeader.getStyleClass().add("header-style");
            rowHeader.setMaxWidth(Double.MAX_VALUE);
            rowHeader.setAlignment(Pos.CENTER);
            gridPane.add(rowHeader, 0, i + 1);

            for (int j = 0; j < columns; j++) {
                Button button;

                if (isDecimal) {
                    String newValue = Fraction.decimalFromStringToString(matrix.get(i).get(j));
                    button = new Button(newValue);
                }

                else {
                    button = new Button(matrix.get(i).get(j));
                }

                button.setPrefSize(50, 30);

                if (i == rows - 1 && j == columns - 1) {
                    button.getStyleClass().add("bottom-right-cell");
                } else if (i == rows - 1 || j == columns - 1) {
                    button.getStyleClass().add("last-row-column");
                } else {
                    button.getStyleClass().add("cell-style");
                }

                if (isDecision) {
                    if (table.getNegativeVariableIndices().isEmpty() || table.getNegativeVariableIndices() == null) {
                        isDecision = false;
                    }
                    table.simplexMove(pivotElement[0], pivotElement[1], table.getNegativeVariableIndices());
                    processAndDisplayMatrix(table, simplexMethodList, matrixArea, matrixContainer, selectedValues, minMaxComboBox, true, backButton, isDecimal);
                }

                for (int[] element : supportElements) {
                    if (element[0] == i && element[1] == j) {
                        button.getStyleClass().add("support-element");
                        final int rowIndex = i;
                        final int colIndex = j;
                        button.setOnAction(event -> {
                            handleSupportElementClick(rowIndex, colIndex);

                            table.simplexMove(rowIndex, colIndex, table.getNegativeVariableIndices());
                            processAndDisplayMatrix(table, simplexMethodList, matrixArea, matrixContainer, selectedValues, minMaxComboBox, false, backButton, isDecimal);

                        });
                    }
                }

                if (pivotElement[0] == i && pivotElement[1] == j) {
                    button.getStyleClass().add("pivot-element");
                }

                gridPane.add(button, j + 1, i + 1);
            }
        }

        container.getChildren().clear();
        container.getChildren().add(gridPane);
    }


    /**
     * Вывод индексов кнопок, на которые нажали.
     * @param row Строка кнопки.
     * @param col Столбец кнопки.
     */
    private void handleSupportElementClick(int row, int col) {
        System.out.printf("Row: %d, Col: %d%n", row, col);
    }

    /**
     * Переводит матрицу из строковой во Fraction
     * @param matrixFields Матрица в строковом преставлении.
     * @return Матрица в дробном преставлении.
     */
    private Matrix convertToMatrix(List<List<String>> matrixFields) {
        int rows = matrixFields.size();
        int cols = matrixFields.getFirst().size();

        Fraction[][] fractions = new Fraction[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String textValue = matrixFields.get(i).get(j);
                fractions[i][j] = Fraction.fromString(textValue);
            }
        }

        return new Matrix(fractions);
    }

    /**
     * Переводит матрицу из виджета в строковое представление.
     * @param matrixFields Матрица, где каждый элемент это виджет.
     * @return Матрица в строковом преставлении.
     */
    private List<List<String>> convertTextFieldsToStringMatrix(List<List<TextField>> matrixFields) {
        List<List<String>> stringMatrix = new ArrayList<>();

        for (List<TextField> row : matrixFields) {
            List<String> stringRow = new ArrayList<>();
            for (TextField textField : row) {
                stringRow.add(textField.getText());
            }
            stringMatrix.add(stringRow);
        }

        return stringMatrix;
    }

    /**
     * Перевод целевой функции из виджетов во Fraction.
     * @param goalFunctionFields Целевая функция.
     * @return Изменённая целевая функция.
     */
    public Fraction[] extractGoalFunctionValues(HBox goalFunctionFields) {
        int size = goalFunctionFields.getChildren().size();
        Fraction[] targetFunction = new Fraction[size];

        for (int i = 0; i < size; i++) {
            HBox fieldContainer = (HBox) goalFunctionFields.getChildren().get(i);
            VBox centeredContainer = (VBox) fieldContainer.getChildren().getFirst();
            TextField textField = (TextField) centeredContainer.getChildren().get(1);

            String input = textField.getText().trim();
            targetFunction[i] = Fraction.fromString(input);
        }

        return targetFunction;
    }

    /**
     * Переводит матрицу из Fraction в строковое представление.
     * @param data Матрица.
     * @return Изменённая матрица.
     */
    private List<List<String>> convertMatrixToList(Fraction[][] data) {
        List<List<String>> matrix = new ArrayList<>();

        for (Fraction[] row : data) {
            List<String> rowList = new ArrayList<>();
            for (Fraction fraction : row) {
                rowList.add(fraction.toString());
            }
            matrix.add(rowList);
        }

        return matrix;
    }


    /**
     * Получение ответа в симплекс методе.
     * @param table Матрица.
     * @param taskType Тип задачи(мин, макс).
     * @param isDecimal Тип дроби.
     * @return Строка для ответа.
     */
    private String getFormattedAnswer(SimplexMethod table, String taskType, boolean isDecimal) {
        StringBuilder result = new StringBuilder();

        Fraction[] vector = new Fraction[table.getMatrix().getRows() - 1 + table.getMatrix().getCols() - 1];
        Arrays.fill(vector, Fraction.ZERO);

        for (int i = 0; i < table.getMatrix().getRows() - 1; i++) {
            vector[table.getIsBasic().get(i)] = table.getMatrix().getElement(i, table.getMatrix().getCols() - 1);
        }

        Fraction objectiveValue = table.getMatrix().getElement(table.getMatrix().getRows() - 1, table.getMatrix().getCols() - 1);

        if (taskType.equals("min")) {
            objectiveValue = objectiveValue.multiply(Fraction.NEGATIVE_ONE);
        }

        if (isDecimal) {
            result.append(String.format("f' = %s%n", objectiveValue.toDecimal()));
        } else {
            result.append(String.format("f' = %s%n", objectiveValue));
        }

        result.append("x̅ = (");
        for (int i = 0; i < vector.length; i++) {
            result.append(isDecimal ? vector[i].toDecimal() : vector[i]);
            if (i < vector.length - 1) {
                result.append(", ");
            }
        }
        result.append(")");

        return result.toString();
    }


    /**
     * Вывод ответа при решении задачи симлекс методом.
     * @param table Матрица.
     * @param taskType Тип задачи.
     * @param isDecimal Тип дроби.
     * @return Строковое представление ответа.
     */
    private String determineAnswer(SimplexMethod table, String taskType, boolean isDecimal) {
        if (table.unlimitedVerification()) {
            if (taskType.equals("min")) {
                return "Неограничено снизу";
            }
            return "Неограничено сверху";
        } else {
            return getFormattedAnswer(table, taskType, isDecimal);
        }
    }

    private SimplexMethod processAndDisplayMatrix(SimplexMethod table, List<SimplexMethod> simplexMethodList,
                                         TextArea matrixArea, VBox matrixContainer, List<Boolean> selectedValues,
                                         ComboBox<String> minMaxComboBox,
                                         boolean isDecision, Button backButton, boolean isDecimal) {
        matrixContainer.getChildren().clear();
        matrixArea.setVisible(false);

        simplexMethodList.add(table);

        table = simplexMethodList.getLast();
        List<String> rowLabels = table.convertToStringList(table.getIsBasic());
        List<String> columnLabels = table.convertToStringList(table.getIsFree());

        if ((!(table.getNegativeVariableIndices() == null || table.getNegativeVariableIndices().isEmpty())
                && table.getSupportElement(table.getNegativeVariableIndices()) != null
                && !table.getSupportElement(table.getNegativeVariableIndices()).isEmpty())) {
            List<int[]> supportElements = table.getSupportElement(table.getNegativeVariableIndices());

            int[] pivotElement = table.getBestSupportElement();

            matrixArea.setVisible(false);

            drawStyledButtonMatrix(matrixContainer, rowLabels, columnLabels,
                    table.getMatrixAsListOfStrings(), supportElements,
                    pivotElement, table, simplexMethodList,
                    matrixArea, matrixContainer,
                    selectedValues, minMaxComboBox,
                    isDecision, backButton, isDecimal);

            System.out.println(selectedValues);
        } else {
            SimplexMethod finalTable = table;
            Platform.runLater(() -> {
                String answer = determineAnswer(finalTable, minMaxComboBox.getValue(), isDecimal);

                matrixArea.setText(answer);
                matrixArea.setVisible(true);
                matrixArea.requestLayout();
            });

            matrixContainer.getChildren().add(matrixArea);
            matrixArea.setManaged(true);

        }

        return table;
    }

    /**
     * Удаление элемента из массива.
     * @param array Массив.
     * @param index Индекс элемента для удаления.
     * @return Новый массив.
     */
    public Fraction[] removeElement(Fraction[] array, int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона: " + index);
        }
        Fraction[] newArray = new Fraction[array.length - 1];

        System.arraycopy(array, 0, newArray, 0, index);
        if (array.length - (index + 1) >= 0)
            System.arraycopy(array, index + 1, newArray, index + 1 - 1, array.length - (index + 1));

        return newArray;
    }

    /**
     * Удаление элемента из списка.
     * @param vector Вектор из целых чисел.
     * @param index Индекс для удаления.
     * @return Изменённый список.
     */
    public List<Integer> removeElement(List<Integer> vector, int index) {
        if (index < 0 || index >= vector.size()) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона: " + index);
        }

        List<Integer> updatedVector = new ArrayList<>(vector);
        updatedVector.remove(index);

        return updatedVector;
    }


    public static void main(String[] args) {
        launch();
    }
}