package simplexmetod;

import java.util.ArrayList;
import java.util.Arrays;
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

        for (int i = 0; i < matrix.getRows(); i++) {
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

        swapVariables(colIndex, rowIndex);

        for (int i = 0; i < matrix.getRows(); i++) {
            if (i != rowIndex) {
                Fraction[] oldLine = matrix.getRowFromMatrix(i);
                Fraction[] newLine = matrix.addVectors(matrix.multiplyVectorByNumber(referenceLine, matrix.getElement(i, colIndex)), oldLine);
                matrix.setRowInMatrix(i, newLine, colIndex);
            }
        }
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
     * @return true, если все элементы в столбце (кроме последнего) отрицательные, иначе false.
     */
    private boolean checkUnlimited(int colIndex) {
        for (int i = 0; i < matrix.getRows() - 1; i++) {
            if (!matrix.getElement(i, colIndex).isLessThan(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Находит все подходящие индексы элементов для опорного элемента.
     *
     * @param colIndex Вектор столбцов, в которых есть опорный элемент.
     * @return Возвращает индексы возможных опорных элементов
     */
    public List<int[]> getSupportElement(List<Integer> colIndex) {
        if (colIndex == null || colIndex.isEmpty()) {
            throw new IllegalArgumentException("Список colIndex не может быть null или пустым.");
        }

        List<int[]> supElementIndices = new ArrayList<>(); // Список для хранения строк и столбцов
        Fraction[] minValueVector = new Fraction[colIndex.size()]; // Массив для хранения максимальных значений

        for (int i = 0; i < minValueVector.length; i++) {
            minValueVector[i] = new Fraction(Integer.MAX_VALUE);
        }

        for (int i = 0; i < colIndex.size(); i++) {
            int columnIndex = colIndex.get(i);
            for (int j = 0; j < matrix.getRows() - 1; j++) {
                Fraction denominator = matrix.getElement(j, columnIndex);
                if (!denominator.isEqualTo(Fraction.ZERO)) {
                    Fraction ratio = matrix.getElement(j, matrix.getCols() - 1).divide(denominator);
                    if (ratio.isLessThan(minValueVector[i]) && ratio.isGreaterThan(Fraction.ZERO)) {
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
            throw new IllegalStateException("Опорные элементы не найдены.");
        }

        return supElementIndices;
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
}
