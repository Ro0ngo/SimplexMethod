package simplexmetod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для работы с симлекс-методом, а в частности с таблицей
 */
public class SimplexMethod {
    private Matrix matrix;
    private Fraction[] targetFunction;
    private List<Integer> isBasicVariable;
    private List<Integer> isFreeVariable;

    /**
     * Конструктор для создадия таблицы для симлекс-метода, а также действия на этой таблицой.
     *
     * @param matrix   Матрица линейных уравнений (В контексте задания матрица A после преобразования методом Гаусса).
     * @param function Целевая функция.
     * @param isBasic  Вектор Id базисных переменных.
     * @param isFree   Вектор Id свободных переменных.
     * @throws IllegalArgumentException Хотя бы один из параметров равен null
     * @throws IllegalArgumentException Длина целевой функции не совпадает с кол-вом солбцов матрицы
     */
    public SimplexMethod(Matrix matrix, Fraction[] function, List<Integer> isBasic, List<Integer> isFree) {

        if (matrix == null || function == null || isBasic == null || isFree == null) {
            throw new IllegalArgumentException("Ни один из параметров не может быть null.");
        }
        if (matrix.getCols() != isFree.toArray().length + 1) {
            throw new IllegalArgumentException("Длина целевой функции должна совпадать с кол-вом свободных переменных + 1");
        }
        if (matrix.getRows() != isBasic.toArray().length + 1) {
            throw new IllegalArgumentException("Ширина целевой функции должна совпадать с кол-вом базисных переменных + 1");
        }

        this.matrix = matrix;
        this.targetFunction = function;
        this.isBasicVariable = isBasic;
        this.isFreeVariable = isFree;
    }

    /**
     * Конструктор для симлекс метода. В частности для искусственного базиса.
     *
     * @param matrix  Матрица линейных уравнений.
     * @param isBasic Вектор Id базисных переменных.
     * @param isFree  Вектор Id свободных переменных.
     */
    public SimplexMethod(Matrix matrix, List<Integer> isBasic, List<Integer> isFree) {

        if (matrix == null || isBasic == null || isFree == null) {
            throw new IllegalArgumentException("Ни один из параметров не может быть null.");
        }
        if (matrix.getCols() != isFree.toArray().length + 1) {
            throw new IllegalArgumentException("Длина целевой функции должна совпадать с кол-вом свободных переменных + 1");
        }
        if (matrix.getRows() != isBasic.toArray().length + 1) {
            throw new IllegalArgumentException("Ширина целевой функции должна совпадать с кол-вом базисных переменных + 1");
        }

        this.matrix = matrix;
        this.isBasicVariable = isBasic;
        this.isFreeVariable = isFree;

    }

    /**
     * Возвращает матрицу.
     *
     * @return Матрица.
     */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * Возвращает вектор целевой функции.
     *
     * @return Функция.
     */
    public Fraction[] getFunction() {
        return targetFunction;
    }

    /**
     * Возвращает вектор базисные переменных.
     *
     * @return Вектор Id базисных переменных.
     */
    public List<Integer> getIsBasic() {
        return isBasicVariable;
    }

    /**
     * Возвращает вектор свободных переменных.
     *
     * @return Вектор Id свободных переменных.
     */
    public List<Integer> getIsFree() {
        return isFreeVariable;
    }

    /**
     * Симлекс-ход
     *
     * @param rowIndex            Id строки опорного элемента.
     * @param colIndex            Id столбца опорного элемента.
     * @param negativeElementFunc Вектор id отрицательных значений целевой функции
     */
    public void simplexMove(int rowIndex, int colIndex, List<Integer> negativeElementFunc) {

        if (rowIndex < 0 || rowIndex >= matrix.getRows()) {
            throw new IllegalArgumentException("Id строки " + rowIndex + " выходит за пределы допустимого диапазона.");
        }
        if (colIndex < 0 || colIndex >= matrix.getCols()) {
            throw new IllegalArgumentException("Id столбца " + colIndex + " выходит за пределы допустимого диапазона.");
        }

        System.out.println("id " + rowIndex + " " + colIndex);
        for (int i = 0; i < matrix.getRows() - 1; i++) {
            if (matrix.getElement(i, matrix.getCols() - 1).isLessThan(Fraction.ZERO)) {
                matrix.setRowInMatrix(i, matrix.multiplyVectorByNumber(matrix.getRowFromMatrix(i), Fraction.NEGATIVE_ONE));
            }
        }

        Fraction supElement = matrix.getElement(rowIndex, colIndex);
        supElement = (Fraction.ONE).divide(supElement);

        Fraction[] referenceLine = matrix.getRowFromMatrix(rowIndex);
        matrix.setElement(rowIndex, colIndex, supElement);

        // Проход по строке, кроме опорного элемента
        for (int i = 0; i < matrix.getCols(); i++) {
            if (i != colIndex) {
                matrix.setElement(rowIndex, i, matrix.getElement(rowIndex, i).multiply(supElement));
            }
        }

        // Проход по столбцу, кроме опорного элемента
        for (int i = 0; i < matrix.getRows(); i++) {
            if (i != rowIndex) {
                matrix.setElement(i, colIndex, matrix.getElement(i, colIndex).multiply(supElement).multiply(Fraction.NEGATIVE_ONE));
            }
        }

        System.out.println("basis" + this.convertToStringList(this.isBasicVariable));
        System.out.println("free" + this.convertToStringList(this.isFreeVariable));
        swapVariables(colIndex, rowIndex);
        System.out.println("basis" + this.convertToStringList(this.isBasicVariable));
        System.out.println("free" + this.convertToStringList(this.isFreeVariable));

        for (int i = 0; i < matrix.getRows(); i++) {
            if (i != rowIndex) {
                Fraction[] oldLine = matrix.getRowFromMatrix(i);
                Fraction[] newLine = matrix.addVectors(matrix.multiplyVectorByNumber(referenceLine, matrix.getElement(i, colIndex)), oldLine);
                matrix.setRowInMatrix(i, newLine, colIndex);
            }
        }
    }

    /**
     * Симлекс ход для базисного метода. С удалением столбца из матрицы.
     *
     * @param rowIndex            Id строки опорного элемента.
     * @param colIndex            Id столбца опорного элемента.
     * @param negativeElementFunc Вектор столбцов по которым можно продолжить симлекс-ход.
     */
    public void simplexMoveWithDeleteLine(int rowIndex, int colIndex, List<Integer> negativeElementFunc) {

        simplexMove(rowIndex, colIndex, negativeElementFunc);
        matrix.removeColumn(colIndex);
        this.isFreeVariable = removeElement(this.isFreeVariable, colIndex);

    }

    /**
     * Метод для перемещения двух значений между собой (В данном контексте для смены базисных и свободных переменных местами).
     *
     * @param freeIndex  Id в векторе свободных переменных.
     * @param basicIndex Id в векторе базисных переменных.
     */
    public void swapVariables(int freeIndex, int basicIndex) {
        if (freeIndex < 0 || freeIndex >= isFreeVariable.size()) {
            throw new IndexOutOfBoundsException("Id для isFreeVariable вне допустимого диапазона: " + freeIndex);
        }
        if (basicIndex < 0 || basicIndex >= isBasicVariable.size()) {
            throw new IndexOutOfBoundsException("Id для isBasicVariable вне допустимого диапазона: " + basicIndex);
        }

        int temp = isFreeVariable.get(freeIndex);
        isFreeVariable.set(freeIndex, isBasicVariable.get(basicIndex));
        isBasicVariable.set(basicIndex, temp);
    }

    /**
     * Находит столбцы по которым можно продолжить симлекс-ход.
     *
     * @return Вектор с id столбцов, элементы которых являются отрицательными.
     */
    public List<Integer> getNegativeVariableIndices() {
        List<Integer> negativeIndices = new ArrayList<>();

        for (int i = 0; i < matrix.getCols() - 1; i++) {
            Fraction value = matrix.getElement(matrix.getRows() - 1, i);
            if (value.isLessThan(Fraction.ZERO)) {
                negativeIndices.add(i);
            }
        }
        return negativeIndices;
    }

    /**
     * Проверяет, являются ли все элементы в указанном столбце (кроме последнего) отрицательными.
     *
     * @param colIndex Id столбца для проверки.
     * @return true, если все элементы в столбце (кроме последнего) <= 0, иначе false.
     */
    private boolean checkUnlimited(int colIndex) {
        for (int i = 0; i < matrix.getRows() - 1; i++) {
            if (!matrix.getElement(i, colIndex).isGreaterThan(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Находит все подходящие Id элементов для опорного элемента.
     *
     * @param colIndex Вектор столбцов, в которых есть опорный элемент.
     * @return Возвращает Id возможных опорных элементов
     */
    public List<int[]> getSupportElement(List<Integer> colIndex) {
        if (colIndex == null || colIndex.isEmpty()) {
            return null;
        }

        List<int[]> supElementIndices = new ArrayList<>(); // Список для хранения строк и столбцов
        Fraction[] minValueVector = new Fraction[colIndex.size()]; // Массив для хранения максимальных значений

        for (int i = 0; i < minValueVector.length; i++) {
            minValueVector[i] = new Fraction(Integer.MAX_VALUE / 100);
        }

        for (int i = 0; i < colIndex.size(); i++) {
            int columnIndex = colIndex.get(i);
            for (int j = 0; j < matrix.getRows() - 1; j++) {
                Fraction denominator = matrix.getElement(j, columnIndex);
                if (!denominator.isEqualTo(Fraction.ZERO) && !denominator.isLessThan(Fraction.ZERO)) {
                    Fraction ratio = matrix.getElement(j, matrix.getCols() - 1).divide(denominator);
                    if (ratio.isLessThan(minValueVector[i]) && !ratio.isLessThan(Fraction.ZERO)) {
                        minValueVector[i] = ratio;
                    }
                }
            }

            for (int j = 0; j < matrix.getRows() - 1; j++) {
                Fraction denominator = matrix.getElement(j, columnIndex);
                if (!denominator.isEqualTo(Fraction.ZERO)) {
                    Fraction ratio = matrix.getElement(j, matrix.getCols() - 1).divide(denominator);
                    if (ratio.isEqualTo(minValueVector[i])) {
                        supElementIndices.add(new int[]{j, columnIndex});
                    }
                }
            }
        }

        if (supElementIndices.isEmpty()) {
            return null;
        }

        return supElementIndices;
    }

    /**
     * Находит лучший опорный элемент
     *
     * @return Опорный элемент
     */
    public int[] getBestSupportElement() {
        List<Integer> negativeIndices = getNegativeVariableIndices();
        if (negativeIndices.isEmpty()) {
            throw new IllegalStateException("Нет отрицательных переменных для обработки.");
        }

        List<int[]> supElementIndices = getSupportElement(negativeIndices);
        if (supElementIndices.isEmpty()) {
            throw new IllegalStateException("Опорные элементы не найдены.");
        }

        int[] bestElement = null;
        Fraction minValue = Fraction.ONE;

        for (int[] element : supElementIndices) {
            int rowIndex = matrix.getRows() - 1; // Id строки
            int colIndex = element[1]; // Id столбца
            Fraction value = matrix.getElement(rowIndex, colIndex);
            if (value.isLessThan(minValue)) {
                minValue = value;
                bestElement = element;
            }
        }

        if (bestElement == null) {
            throw new IllegalStateException("Не удалось найти лучший опорный элемент.");
        }

        return bestElement; // Возвращаем массив int[]
    }


    /**
     * Вывод значения целевой функции и вектора x̅
     */
    public void printAnswer() {
        Fraction[] vector = new Fraction[matrix.getRows() - 1 + matrix.getCols() - 1];
        Arrays.fill(vector, Fraction.ZERO);

        for (int i = 0; i < matrix.getRows() - 1; i++) {
            vector[isBasicVariable.get(i)] = matrix.getElement(i, matrix.getCols() - 1);
        }

        System.out.println("f' = " + matrix.getElement(matrix.getRows() - 1, matrix.getCols() - 1).multiply(Fraction.NEGATIVE_ONE));
        System.out.print("x̅ = (");
        for (int i = 0; i < vector.length; i++) {
            if (i == vector.length - 1) {
                System.out.print(vector[i] + ")");
            } else System.out.print(vector[i] + ",");
        }
    }

    /**
     * Преобразует матрицу так, чтобы значения в стоке матрицы были не отрицательными
     */
    public void updateTable() {
        for (int i = 0; i < matrix.getRows() - 1; i++) {
            if (matrix.getElement(i, matrix.getCols() - 1).isLessThan(Fraction.ZERO)) {
                Fraction[] updatedRow = matrix.multiplyVectorByNumber(matrix.getRowFromMatrix(i), Fraction.NEGATIVE_ONE);
                matrix.setRowInMatrix(i, updatedRow);
            }
        }
    }

    /**
     * Проверяет состоит ли весь столбец из отрицательных элементов.
     *
     * @return Возвращает boolean значение. true - весь столбец отрицательный, false - иначе.
     */
    public boolean unlimitedVerification() {
        for (int col = 0; col < matrix.getCols() - 1; col++) {
            boolean isUnlimited = true;

            for (int row = 0; row < matrix.getRows(); row++) {
                if (matrix.getElement(row, col).isGreaterThan(Fraction.ZERO)) {
                    isUnlimited = false;
                    break;
                }
            }

            if (isUnlimited) {
                return true;
            }
        }

        return false;
    }

    public boolean isNonDegenerate () {
        for (int i = 0; i < getIsBasic().size(); i++) {
            if (getIsBasic().get(i) > matrix.getCols() + matrix.getRows() - 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Удаляет элемент из вектора
     *
     * @param vector Вектор
     * @param index  Id элемента, который надо удалить
     * @return Изменённый вектор
     */
    public List<Integer> removeElement(List<Integer> vector, int index) {
        if (index < 0 || index >= vector.size()) {
            throw new IndexOutOfBoundsException("Id вне диапазона: " + index);
        }

        List<Integer> updatedVector = new ArrayList<>(vector);
        updatedVector.remove(index);

        return updatedVector;
    }

    /**
     * Выводит красивенько табличку.
     */
    public void printTable() {
        System.out.print("   ");
        for (int i = 0; i < isFreeVariable.toArray().length; i++) {
            System.out.print("x" + (isFreeVariable.get(i) + 1) + "\t");
        }
        System.out.print("\n");

        for (int i = 0; i < matrix.getRows() - 1; i++) {
            System.out.print("x" + (isBasicVariable.get(i) + 1) + " ");
            matrix.printRow(i);
        }
        System.out.print("   ");
        matrix.printRow(matrix.getRows() - 1);
    }

    /**
     * Преобразует текущую матрицу в список списков строк.
     *
     * @return Матрица как List<List<String>>.
     */
    public List<List<String>> getMatrixAsListOfStrings() {
        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < matrix.getRows(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < matrix.getCols(); j++) {
                row.add(matrix.getElement(i, j).toString());
            }
            result.add(row);
        }

        return result;
    }

    /**
     * Переводит вектор целых значений в строковые.
     *
     * @param integerList Вектор целых значений.
     * @return Вектор строковых значений.
     */
    public List<String> convertToStringList(List<Integer> integerList) {
        if (integerList == null) {
            return new ArrayList<>();
        }
        return integerList.stream()
                .map(num -> "x" + (num + 1))
                .collect(Collectors.toList());
    }

}
