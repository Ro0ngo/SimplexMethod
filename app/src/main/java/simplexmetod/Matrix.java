package simplexmetod;

import java.util.*;

// Алгортим: создаётся целевая функция(вектор массива Fraction значений), вместе с копией
// Создаётся вектор базисных и свободных переменных(boolean значения)
// Создаётся матрица размера m*n, где m = basisVector.length - 1, n > 0
// Вызывается метод matrix.rendererColumn(basisVector), который меняет местами столбцы, делая базисные переменные слева
// Выполняется метод гаусса (gauss)
// Вызывается метод matrix.restoreColumnOrderWithVector(), возвращает столбцы на свои места
// Выполняется matrix.solution(targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy)),
// Который преобразовывает целевую функцию, заменяя базисные переменные на свободные.
// Добавление внизу матрицы строки целевой функции
// Удаление столбца базисных переменных

/**
 * Класс для представления матрицы дробей.
 */
public class Matrix {
    private Fraction[][] data;
    private int rows;
    private int cols;
    private int[] columnOrder;

    /**
     * Конструктор для создания матрицы из двумерного массива дробей.
     *
     * @param data Двумерный массив объектов Fraction.
     * @throws IllegalArgumentException Если матрица пуста (нулевая или имеет нулевую длину).
     */
    public Matrix(Fraction[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Матрица не может быть пустой.");
        }
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
        this.columnOrder = initializeColumnOrder(cols);
    }

    /**
     * Устанавливает элемент матрицы.
     *
     * @param row   Index строки.
     * @param col   Index столбца.
     * @param value Значение элемента в виде объекта Fraction.
     * @throws IndexOutOfBoundsException Если индексы выходят за пределы матрицы.
     */
    public void setElement(int row, int col, Fraction value) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Id выходят за пределы матрицы.");
        }
        data[row][col] = value;
    }

    /**
     * Получает элемент матрицы.
     *
     * @param row Index строки.
     * @param col Index столбца.
     * @return Значение элемента в виде объекта Fraction.
     * @throws IndexOutOfBoundsException Если индексы выходят за пределы матрицы.
     */
    public Fraction getElement(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Id выходят за пределы матрицы.");
        }
        return data[row][col];
    }

    /**
     * Проверяет, является ли строка вырожденной (все элементы нулевые).
     *
     * @param row Index строки для проверки.
     * @return true, если строка вырождена; false в противном случае.
     */
    private boolean isRowDegenerate(int row) {
        for (Fraction element : data[row]) {
            if (!element.isEqualTo(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, является ли столбец вырожденным (все элементы нулевые).
     *
     * @param col Index столбца для проверки.
     * @return true, если столбец вырожден; false в противном случае.
     */
    private boolean isColumnDegenerate(int col) {
        for (int i = 0; i < rows; i++) {
            if (!data[i][col].isEqualTo(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Применяет прямой ход метода Гаусса для приведения матрицы к ступенчатому виду.
     * После выполнения этого метода на главной диагонали будут единицы.
     *
     * @throws IllegalArgumentException Матрица содержит вырожденные строки или столбцы.
     */
    public boolean forwardElimination() {
        for (int i = 0; i < Math.min(rows, cols); i++) {
            int maxRow = i;
            for (int k = i + 1; k < rows; k++) {
                if (data[k][i].isGreaterThan(data[maxRow][i])) {
                    maxRow = k;
                }
            }

            if (data[maxRow][i].isEqualTo(Fraction.ZERO) || isRowDegenerate(maxRow) || isColumnDegenerate(i)) {
                return true;
            }

            Fraction[] temp = data[maxRow];
            data[maxRow] = data[i];
            data[i] = temp;

            Fraction leadingElement = data[i][i];
            for (int j = 0; j < cols; j++) {
                data[i][j] = data[i][j].divide(leadingElement);
            }

            for (int j = i + 1; j < rows; j++) {
                Fraction factor = data[j][i];
                for (int k = i; k < cols; k++) {
                    data[j][k] = data[j][k].subtract(factor.multiply(data[i][k]));
                }
            }
        }
        return false;
    }


    /**
     * Находит индексы базисных переменных.
     *
     * @param basicVector Вектор boolean значений, где true - базисная переменная, false - иначе.
     * @return Id базисных переменных.
     */
    public List<Integer> isBasicVector(boolean[] basicVector) {
        List<Integer> basic = new LinkedList<>();
        for (int i = 0; i < basicVector.length; i++) {
            if (basicVector[i]) {
                basic.add(i);
            }
        }
        return basic;
    }


    /**
     * Находит индексы свободных переменных.
     *
     * @param basicVector Вектор boolean значений, где false - свободная переменная, true - иначе.
     * @return Id свободных переменных.
     */
    public List<Integer> isFreeVector(boolean[] basicVector) {
        List<Integer> free = new LinkedList<>();
        for (int i = 0; i < basicVector.length; i++) {
            if (!basicVector[i]) {
                free.add(i);
            }
        }
        return free;
    }


    /**
     * Преобразует целевую функцию, заменяя базисные переменные на свободные.
     *
     * @param targetFunction Целевая функция.
     * @param isBasic        Вектор индексов базисных переменных.
     * @param isFree         Вектор индексов свободных переменных.
     * @return Возвращает преобразованную целевую функцию(без базисных переменных, на их месте свободные).
     */
    public Fraction[] solution(Fraction[] targetFunction, List<Integer> isBasic, List<Integer> isFree) {
        Fraction[] transformedFunction = new Fraction[targetFunction.length];
        Arrays.fill(transformedFunction, Fraction.ZERO);

        List<Integer> freeCopy = new ArrayList<>(isFree);
        freeCopy.add(targetFunction.length - 1);

        for (int j = 0; j < targetFunction.length; j++) {
            if (freeCopy.contains(j)) {
                transformedFunction[j] = transformedFunction[j].add(targetFunction[j]);
            }
        }

        for (int i = 0; i < getRows(); i++) {
            for (Integer indexCol : isBasic) {
                if (data[i][indexCol].isEqualTo(Fraction.ONE)) {
                    Fraction[] updateVector = createUpdateVector(i, indexCol, targetFunction[indexCol]);
                    transformedFunction = addVectors(transformedFunction, updateVector);
                }
            }
        }

        transformedFunction[transformedFunction.length - 1] =
                transformedFunction[transformedFunction.length - 1].multiply(Fraction.NEGATIVE_ONE);

        return transformedFunction;
    }


    private Fraction[] createUpdateVector(int row, int indexCol, Fraction coefficient) {
        Fraction[] vector = getRowFromMatrix(row);
        vector[indexCol] = Fraction.ZERO;
        vector = multVectorByNumberExceptLastIndex(vector);

        return multiplyVectorByNumber(vector, coefficient);
    }


    /**
     * Домножает элементы строки матрицы, кроме последнего, на -1.
     *
     * @param vector Вектор строки матрицы.
     * @return Вектор значений переменных, перенесённых на другую сторону знака =.
     */
    private Fraction[] multVectorByNumberExceptLastIndex(Fraction[] vector) {

        for (int i = 0; i < vector.length - 1; i++) {
            vector[i] = vector[i].multiply(Fraction.NEGATIVE_ONE);
        }

        return vector;
    }

    /**
     * Применяет обратный ход метода Гаусса для приведения матрицы к диагональному виду.
     * После выполнения этого метода матрица будет в форме, пригодной для нахождения решений.
     *
     * @throws IllegalArgumentException Матрица содержит некорректные данные для обратного хода.
     */
    public void backwardSubstitution() {
        for (int i = Math.min(rows, cols) - 1; i >= 0; i--) {
            if (data[i][i].isEqualTo(Fraction.ZERO)) {
                throw new IllegalArgumentException("Матрица содержит нулевой элемент на главной диагонали.");
            }

            for (int j = i - 1; j >= 0; j--) {
                Fraction factor = data[j][i];
                for (int k = i; k < cols; k++) {
                    data[j][k] = data[j][k].subtract(factor.multiply(data[i][k]));
                }
            }
        }
    }


    /**
     * Выполняет метод Гаусса, объединяющий прямой и обратный ходы.
     */
    public boolean gauss() {
        if (forwardElimination()){
            return true;
        }
        backwardSubstitution();
        return false;
    }

    /**
     * Возвращает количество строк в матрице.
     *
     * @return Количество строк.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Возвращает количество столбцов в матрице.
     *
     * @return Количество столбцов.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Создаёт массив порядка столбцов.
     *
     * @param columnCount Количество столбцов.
     * @return Массив с порядком столбцов (0, 1, ..., columnCount-1).
     */
    private int[] initializeColumnOrder(int columnCount) {
        int[] columnOrder = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnOrder[i] = i;
        }
        return columnOrder;
    }

    /**
     * Меняет местами два столбца в матрице и обновляет порядок столбцов.
     *
     * @param col1 Id первого столбца.
     * @param col2 Id второго столбца.
     * @throws IndexOutOfBoundsException Если индексы выходят за пределы матрицы.
     */
    public void swapColumns(int col1, int col2) {
        if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            throw new IndexOutOfBoundsException("Id столбцов выходят за пределы матрицы.");
        }

        for (int i = 0; i < rows; i++) {
            Fraction temp = data[i][col1];
            data[i][col1] = data[i][col2];
            data[i][col2] = temp;
        }

        int tempOrder = columnOrder[col1];
        columnOrder[col1] = columnOrder[col2];
        columnOrder[col2] = tempOrder;
    }

    /**
     * Восстанавливает столбцы матрицы в исходном порядке и сбрасывает порядок столбцов.
     */
    public void restoreColumnOrderWithVector() {
        Fraction[][] restoredMatrix = new Fraction[rows][cols];

        for (int originalIndex = 0; originalIndex < cols; originalIndex++) {
            int currentIndex = columnOrder[originalIndex];
            for (int row = 0; row < rows; row++) {
                restoredMatrix[row][currentIndex] = data[row][originalIndex];
            }
        }

        this.columnOrder = initializeColumnOrder(cols);
        this.data = restoredMatrix;
    }

    /**
     * Перестановка столбцов, согласно вектору. Вначале идут базисные переменные, а затем свободные.
     *
     * @param basisVector Вектор из boolean значений, где true - базисная переменная, false - иначе.
     * @throws IllegalArgumentException Длина вектора должна быть на 1 меньше, чем кол-во столбцов(т.е равно кол-ву переменных) матрицы
     */
    public void rendererColumn(boolean[] basisVector) {
        if (basisVector.length != cols - 1) {
            throw new IllegalArgumentException("Длина basisVector должна быть на 1 меньше количества столбцов матрицы.");
        }

        for (int i = 0; i < basisVector.length; i++) {
            if (!basisVector[i]) {
                for (int j = i + 1; j < basisVector.length; j++) {
                    if (basisVector[j]) {
                        swapColumns(i, j);
                        boolean temp = basisVector[i];
                        basisVector[i] = basisVector[j];
                        basisVector[j] = temp;
                        break;
                    }
                }
            }
        }
    }


    /**
     * Возвращает текущий порядок столбцов.
     *
     * @return Массив с порядком столбцов.
     */
    public int[] getColumnOrder() {
        return columnOrder;
    }

    /**
     * Выводит текущий порядок столбцов.
     */
    public void printColumnOrder() {
        System.out.println(Arrays.toString(columnOrder));
    }

    /**
     * Выводит матрицу на экран.
     */
    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (data[i][j].getDenominator() == 1) {
                    System.out.print(data[i][j].getNumerator() + "\t");
                } else {
                    System.out.print(data[i][j] + "\t");
                }
            }
            System.out.println();
        }
    }

    /**
     * Возвращает текущую матрицу в виде двумерного массива Fraction.
     *
     * @return Копия матрицы в виде двумерного массива.
     */
    public Fraction[][] toArray() {
        Fraction[][] arrayCopy = new Fraction[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, arrayCopy[i], 0, cols);
        }
        return arrayCopy;
    }

    /**
     * Выводит матрицу в формате линейных уравнений
     * Нулевые коэффициенты пропускаются.
     */
    public void printMatrixAsEquations() {
        for (int i = 0; i < rows; i++) {
            StringBuilder equation = new StringBuilder();
            boolean hasNonZeroCoefficient = false;

            for (int j = 0; j < cols - 1; j++) {
                Fraction coefficient = data[i][j];

                if (!coefficient.isEqualTo(Fraction.ZERO)) {
                    hasNonZeroCoefficient = true;

                    if (!equation.isEmpty() && coefficient.isGreaterThan(Fraction.ZERO)) {
                        equation.append(" + ");
                    } else if (coefficient.isLessThan(Fraction.ZERO)) {
                        equation.append(" - ");
                        coefficient = coefficient.multiply(Fraction.NEGATIVE_ONE);
                    }

                    if (coefficient.isEqualTo(Fraction.ONE)) {
                        equation.append("x").append(columnOrder[j]);
                    } else if (coefficient.isEqualTo(Fraction.NEGATIVE_ONE)) {
                        equation.append("-x").append(columnOrder[j]);
                    } else {
                        if (coefficient.getDenominator() == 1) {
                            equation.append(coefficient.getNumerator());
                        } else {
                            equation.append(coefficient);
                        }
                        equation.append("*x").append(columnOrder[j]);
                    }
                }
            }

            if (!hasNonZeroCoefficient) {
                System.out.println("0 = " + data[i][cols - 1]);
                continue;
            }

            equation.append(" = ").append(data[i][cols - 1]);

            System.out.println(equation);
        }
    }

    /**
     * Добавляет строку в матрицу.
     *
     * @param newRow Новая строка, представляемая массивом объектов Fraction.
     * @throws IllegalArgumentException Если количество элементов в новой строке не соответствует количеству столбцов матрицы.
     */
    public void addRow(Fraction[] newRow) {
        if (newRow == null || newRow.length != cols) {
            throw new IllegalArgumentException("Новая строка должна иметь столько же элементов, сколько и количество столбцов в матрице.");
        }

        Fraction[][] newData = new Fraction[rows + 1][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, newData[i], 0, cols);
        }

        newData[rows] = newRow;

        this.data = newData;
        this.rows += 1;
    }

    /**
     * Удаляет столбец из матрицы.
     *
     * @param colIndex Id столбца для удаления.
     * @throws IndexOutOfBoundsException Если id столбца выходит за пределы допустимого диапазона.
     */
    public void removeColumn(int colIndex) {
        if (colIndex < 0 || colIndex >= cols) {
            throw new IndexOutOfBoundsException("Id столбца находится вне допустимого диапазона.");
        }

        Fraction[][] newData = new Fraction[rows][cols - 1];

        for (int i = 0; i < rows; i++) {
            int newCol = 0;
            for (int j = 0; j < cols; j++) {
                if (j != colIndex) {
                    newData[i][newCol] = data[i][j];
                    newCol++;
                }
            }
        }

        int[] newColumnOrder = new int[cols - 1];
        int newIndex = 0;
        for (int i = 0; i < cols; i++) {
            if (i != colIndex) {
                newColumnOrder[newIndex] = columnOrder[i];
                newIndex++;
            }
        }

        this.data = newData;
        this.columnOrder = newColumnOrder;
        this.cols -= 1;
    }

    /**
     * Умножение вектора на число. (В данном контексте умножение строки матрицы на число)
     *
     * @param vector     Вектор типа Fraction.
     * @param multiplier Число типа Fraction.
     * @return Одномерный тензор (Новую строку матрицы после преобразования)
     */
    public Fraction[] multiplyVectorByNumber(Fraction[] vector, Fraction multiplier) {
        Fraction[] resultVector = new Fraction[vector.length];
        for (int i = 0; i < vector.length; i++) {
            resultVector[i] = vector[i].multiply(multiplier);
        }
        return resultVector;
    }

    /**
     * Получение строки из матрицы.
     *
     * @param rowIndex Id строки.
     * @return Строку с объектами типа Fraction.
     * @throws IndexOutOfBoundsException Введённый id строки выходит за пределы размеров матрицы
     */
    public Fraction[] getRowFromMatrix(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows) {
            throw new IndexOutOfBoundsException("Id строки вне допустимого диапазона.");
        }

        Fraction[] row = new Fraction[cols];
        for (int col = 0; col < cols; col++) {
            row[col] = getElement(rowIndex, col);
        }
        return row;
    }

    /**
     * Сложение двух векторов.
     *
     * @param vector1 Первый вектор.
     * @param vector2 Второй вектор.
     * @return Результат сложения тензоров.
     */
    public Fraction[] addVectors(Fraction[] vector1, Fraction[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Векторы должны быть одинаковой длины для сложения.");
        }

        Fraction[] resultVector = new Fraction[vector1.length];
        for (int i = 0; i < vector1.length; i++) {
            resultVector[i] = vector1[i].add(vector2[i]);
        }
        return resultVector;
    }

    /**
     * Замена текущей строки двумерного тензора для симплекс-хода
     *
     * @param rowIndex Id строки.
     * @param newRow   Новая строка.
     * @param colIndex Id опорного столбца(сам по себе в ходе симлекс-метода этот элемент изменяется)
     * @throws IndexOutOfBoundsException Id за пределами размеров матрицы.
     * @throws IllegalArgumentException  Неверная длина новой стоки
     */
    public void setRowInMatrix(int rowIndex, Fraction[] newRow, int colIndex) {
        if (rowIndex < 0 || rowIndex >= rows) {
            throw new IndexOutOfBoundsException("Id строки вне допустимого диапазона.");
        }
        if (newRow.length != cols) {
            throw new IllegalArgumentException("Длина новой строки должна совпадать с количеством столбцов в матрице.");
        }

        for (int col = 0; col < cols; col++) {
            if (col != colIndex)
                setElement(rowIndex, col, newRow[col]);
        }
    }


    /**
     * Замена текущей строки двумерного тензора для симплекс-хода
     *
     * @param rowIndex Id строки.
     * @param newRow   Новая строка.
     * @throws IndexOutOfBoundsException Id за пределами размеров матрицы.
     * @throws IllegalArgumentException  Неверная длина новой стоки
     */
    public void setRowInMatrix(int rowIndex, Fraction[] newRow) {
        if (rowIndex < 0 || rowIndex >= rows) {
            throw new IndexOutOfBoundsException("Id строки вне допустимого диапазона.");
        }
        if (newRow.length != cols) {
            throw new IllegalArgumentException("Длина новой строки должна совпадать с количеством столбцов в матрице.");
        }

        for (int col = 0; col < cols; col++) {
            setElement(rowIndex, col, newRow[col]);
        }
    }

    /**
     * Удаляет столбцы из матрицы по списку индексов, используя метод removeColumn.
     *
     * @param colIndices Список id столбцов для удаления.
     * @throws IndexOutOfBoundsException Если какой-либо из id выходит за пределы допустимого диапазона.
     */
    public void removeColumns(List<Integer> colIndices) {
        colIndices.sort(Comparator.reverseOrder());

        for (int colIndex : colIndices) {
            if (colIndex < 0 || colIndex >= cols) {
                throw new IndexOutOfBoundsException("Id столбца " + colIndex + " находится вне допустимого диапазона.");
            }
            removeColumn(colIndex);
        }
    }

    /**
     * Выводит одну строку матрицы на основе указанного индекса.
     *
     * @param rowIndex Id строки, которую нужно вывести.
     * @throws IllegalArgumentException Если id строки выходит за пределы допустимого диапазона.
     */
    public void printRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows) {
            throw new IllegalArgumentException("Id строки " + rowIndex + " находится вне допустимого диапазона.");
        }

        for (int j = 0; j < cols; j++) {
            if (data[rowIndex][j].getDenominator() == 1) {
                System.out.print(data[rowIndex][j].getNumerator() + "\t");
            } else {
                System.out.print(data[rowIndex][j] + "\t");
            }
        }
        System.out.println();
    }


}