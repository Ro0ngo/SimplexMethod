package simplexmetod;

import javafx.application.Application;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    2. Укажите количество свободных переменных.
                    3. Выберите тип задачи (min/max).
                    4. Введите целевую функцию и матрицу.
                    5. Нажмите 'Применить' для обработки данных.
                    6. Используйте 'Сохранить' для сохранения результатов.""");
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

        ComboBox<Integer> freeVarsComboBox = new ComboBox<>();

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

                freeVarsComboBox.getItems().clear();
                for (int i = 1; i <= count; i++) {
                    freeVarsComboBox.getItems().add(i);
                }
                freeVarsComboBox.setValue(1);

                List<BooleanProperty> validityList = new ArrayList<>();

                for (int i = 0; i < count + 1; i++) {
                    HBox fieldContainer = new HBox(5);
                    fieldContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    TextField textField = new TextField();
                    textField.setPrefWidth(50);

                    BooleanProperty isValid = new SimpleBooleanProperty(true);
                    validityList.add(isValid);

                    textField.textProperty().addListener((observable, oldValue, newValue) -> {
                        try {
                            String fractionPattern = "^\\d+/\\d+$";
                            String noLeadingZeroPattern = "^(0|([1-9][0-9]*))$";
                            String noLeadingZeroInFractionPattern = "^([1-9][0-9]*)/([1-9][0-9]*)$";

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
                    centeredContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    centeredContainer.getChildren().addAll(variableLabel, textField);
                    fieldContainer.getChildren().add(centeredContainer);

                    goalFunctionFields.getChildren().add(fieldContainer);
                }

                applyButton.disableProperty().bind(Bindings.createBooleanBinding(
                        () -> validityList.stream().anyMatch(valid -> !valid.get()),
                        validityList.toArray(new Observable[] {})
                ));

            } catch (Exception ignored) {
            }
        });

        List<List<TextField>> matrixFields = new ArrayList<>();

        freeVarsComboBox.setOnAction(event -> {
            try {
                if (numberComboBox.getValue() != null && freeVarsComboBox.getValue() != null) {
                    int variablesCount = numberComboBox.getValue();
                    int freeVarsCount = freeVarsComboBox.getValue();

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
                                    String fractionPattern = "^\\d+/\\d+$";
                                    String noLeadingZeroPattern = "^(0|([1-9][0-9]*))$";
                                    String noLeadingZeroInFractionPattern = "^([1-9][0-9]*)/([1-9][0-9]*)$";

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
        topRow.getChildren().addAll(new Label("Кол-во переменных:"), numberComboBox, new Label("Кол-во свободных:"), freeVarsComboBox, new Label("Задача на:"), minMaxComboBox, new Label("Вид дроби:"), fractionTypeComboBox);
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
                        freeVarsComboBox,
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
                        freeVarsComboBox,
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
        applyButton.setOnAction(e -> {
            String data = collectData(numberComboBox, freeVarsComboBox, minMaxComboBox, fractionTypeComboBox, goalFunctionFields, matrixFields);
            freeVarsTab.setContent(createFreeVariablesTab(numberComboBox.getValue(), freeVarsComboBox.getValue()));
            System.out.println(data);
        });

        applyButton.setDisable(true);
        resetButton.setOnAction(event -> {
            numberComboBox.setValue(null);
            freeVarsComboBox.getItems().clear();
            minMaxComboBox.setValue("min");
            goalFunctionBox.setVisible(false);
            goalFunctionFields.getChildren().clear();
            matrixBox.setVisible(false);
            errorMessage.setVisible(false);

            applyButton.setDisable(true);
            freeVarsTab.setContent(new StackPane(new Text("Необходимо выбрать данные во вкладке 'Постановки задачи' и нажать на кнопку 'Применить'")));
        });

        tabPane.getTabs().addAll(taskTab, freeVarsTab, basicVarsTab);

        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        primaryStage.setTitle("Матрица переменных");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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

    private List<List<Double>> getMatrixData(List<List<TextField>> matrixFields) {
        List<List<Double>> matrixData = new ArrayList<>();
        try {
            for (List<TextField> rowFields : matrixFields) {
                List<Double> rowData = new ArrayList<>();
                for (TextField cell : rowFields) {
                    String text = cell.getText().trim();
                    rowData.add(text.isEmpty() ? 0.0 : Double.parseDouble(text));
                }
                matrixData.add(rowData);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: некорректное значение в матрице!");
        }
        return matrixData;
    }

    private String collectData(ComboBox<Integer> numberComboBox, ComboBox<Integer> freeVarsComboBox, ComboBox<String> minMaxComboBox, ComboBox<String> fractionTypeComboBox, HBox goalFunctionFields, List<List<TextField>> matrixFields) {
        if (numberComboBox.getValue() == null || freeVarsComboBox.getValue() == null || minMaxComboBox.getValue() == null || fractionTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
            showError("Пожалуйста, заполните обязаткльные поля.");
            return null;
        }

        Integer numberOfVariables = numberComboBox.getValue();
        Integer numberOfFreeVars = freeVarsComboBox.getValue();
        String taskType = minMaxComboBox.getValue();
        int selectedIndex = fractionTypeComboBox.getSelectionModel().getSelectedIndex();
        int fractionType = selectedIndex == 0 ? 0 : 1;

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append(numberOfVariables).append("\n")
                .append(numberOfFreeVars).append("\n")
                .append(taskType).append("\n")
                .append(fractionType).append("\n");

        int[] goalFunctionValues = new int[numberOfVariables + 1];
        for (int i = 0; i < goalFunctionFields.getChildren().size(); i++) {
            HBox fieldContainer = (HBox) goalFunctionFields.getChildren().get(i);
            Node variableContainer = fieldContainer.getChildren().getFirst();
            if (variableContainer instanceof VBox vbox) {
                TextField textField = (TextField) vbox.getChildren().get(1);
                String input = textField.getText().trim();
                goalFunctionValues[i] = input.isEmpty() ? 0 : Integer.parseInt(input);
            } else if (variableContainer instanceof HBox hbox) {
                TextField textField = (TextField) hbox.getChildren().get(1);
                String input = textField.getText().trim();
                goalFunctionValues[i] = input.isEmpty() ? 0 : Integer.parseInt(input);
            }
        }
        for (int value : goalFunctionValues) {
            dataBuilder.append(value).append(" ");
        }
        dataBuilder.append("\n");

        List<List<Double>> matrixData = getMatrixData(matrixFields);
        for (List<Double> row : matrixData) {
            for (Double value : row) {
                dataBuilder.append(value).append(" ");
            }
            dataBuilder.append("\n");
        }

        return dataBuilder.toString();
    }

    private void loadData(
            String filePath,
            ComboBox<Integer> numberComboBox,
            ComboBox<Integer> freeVarsComboBox,
            ComboBox<String> minMaxComboBox,
            ComboBox<String> fractionTypeComboBox,
            HBox goalFunctionFields,
            List<List<TextField>> matrixFields
    ) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int numberOfVariables = Integer.parseInt(reader.readLine());
            numberComboBox.setValue(numberOfVariables);

            int numberOfFreeVars = Integer.parseInt(reader.readLine());
            freeVarsComboBox.setValue(numberOfFreeVars);

            String taskType = reader.readLine();
            minMaxComboBox.setValue(taskType);

            int fractionType = Integer.parseInt(reader.readLine());
            fractionTypeComboBox.getSelectionModel().select(fractionType);

            String[] goalFunctionValues = reader.readLine().split(" ");
            for (int i = 0; i < goalFunctionFields.getChildren().size(); i++) {
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

            for (List<TextField> rowFields : matrixFields) {
                String[] rowValues = reader.readLine().split(" ");
                for (int j = 0; j < rowFields.size(); j++) {
                    rowFields.get(j).setText(rowValues[j]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            showError("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private BorderPane createFreeVariablesTab(int variableCount, int maxSelectable) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
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
        }

        Label instructionLabel = new Label("Выберите свободные переменные:");
        instructionLabel.setPadding(new Insets(0, 10, 0, 0));

        HBox textAndVariablesBox = new HBox(10);
        textAndVariablesBox.setAlignment(Pos.CENTER_LEFT);
        textAndVariablesBox.getChildren().addAll(instructionLabel, variablesBox);
        textAndVariablesBox.setSpacing(20);

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

            for (CheckBox checkBox : checkBoxes) {
                checkBox.setDisable(!checkBox.isSelected() && selectedCount >= maxSelectable);
            }

            solveButton.setDisable(selectedCount < maxSelectable);
        };

        for (CheckBox checkBox : checkBoxes) {
            checkBox.selectedProperty().addListener(checkBoxListener);
        }

        borderPane.setTop(textAndVariablesBox);
        borderPane.setBottom(buttonsBox);

        BorderPane.setAlignment(buttonsBox, Pos.CENTER);
        BorderPane.setMargin(buttonsBox, new Insets(10));

        return borderPane;
    }

    public static void main(String[] args) {
        launch();
    }
}