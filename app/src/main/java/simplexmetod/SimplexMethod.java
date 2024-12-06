package simplexmetod;

import java.util.ArrayList;
import java.util.List;

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
     * @param isBasic  Вектор индексов базисных переменных.
     * @param isFree   Вектор индексов свободных переменных.
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
     * @return Вектор индексов базисных переменных.
     */
    public List<Integer> getIsBasic() {
        return isBasicVariable;
    }

    /**
     * Возвращает вектор свободных переменных.
     *
     * @return Вектор индексов свободных переменных.
     */
    public List<Integer> getIsFree() {
        return isFreeVariable;
    }

    /**
     * Симлекс-ход
     *
     * @param rowIndex Id строки опорного элемента.
     * @param colIndex Id столбца опорного элемента.
     */
    public void simplexMove(int rowIndex, int colIndex) {

        if (rowIndex < 0 || rowIndex >= matrix.getRows()) {
            throw new IllegalArgumentException("Id строки " + rowIndex + " выходит за пределы допустимого диапазона.");
        }
        if (colIndex < 0 || colIndex >= matrix.getCols()) {
            throw new IllegalArgumentException("Id столбца " + colIndex + " выходит за пределы допустимого диапазона.");
        }

        Fraction supElement = matrix.getElement(rowIndex, colIndex);
        supElement = (new Fraction(1)).divide(supElement);

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
                matrix.setElement(i, colIndex, matrix.getElement(i, colIndex).multiply(supElement).multiply(new Fraction(-1)));
            }
        }

        swapVariables(colIndex, rowIndex);

    }

    /**
     * Метод для перемещения двух значений между собой (В данном контексте для смены базисных и свободных переменных местами).
     *
     * @param freeIndex  Id в векторе свободных переменных.
     * @param basicIndex Id в векторе базисных переменных.
     */
    private void swapVariables(int freeIndex, int basicIndex) {
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
    private List<Integer> getNegativeVariableIndices() {
        List<Integer> negativeIndices = new ArrayList<>();

        for (int i = 0; i < matrix.getCols() - 1; i++) {
            Fraction value = matrix.getElement(matrix.getRows() - 1, i);
            if (value.isLessThan(new Fraction(0))) {
                negativeIndices.add(i);
            }
        }

        return negativeIndices;
    }

    /**
     * Выводит красивенько табличку.
     */
    public void printTable() {
        System.out.print("   ");
        for (int i = 0; i < isFreeVariable.toArray().length; i++) {
            System.out.print("x" + isFreeVariable.get(i));
        }
        System.out.print("\n");

        for (int i = 0; i < matrix.getRows() - 1; i++) {
            System.out.print("x" + isBasicVariable.get(i) + " ");
            matrix.printRow(i);
        }
        System.out.print("   ");
        matrix.printRow(matrix.getRows() - 1);
    }
}
