package simplexmetod;

import java.util.Arrays;
import java.util.List;

/**
 * Запуск симлекс-метода
 */
public class App {

    /**
     * Вывод на консоль
     *
     * @return Привет мир!
     */
    public String getGreeting() {
        return "Hello World!";
    }

    /**
     * Конструктор
     */
    public App() {

    }

    /**
     * точка входа
     *
     * @param args йцу
     */
    public static void main(String[] args) {
        boolean[] basisVector = {false, false, true, true};
        boolean[] vectorCopy = Arrays.copyOf(basisVector, basisVector.length);
        Fraction[] targetFunction = {new Fraction(-2), new Fraction(-1), new Fraction(-3), new Fraction(-1), new Fraction(0)};

        Matrix matrix = new Matrix(new Fraction[][]{
                {new Fraction(1), new Fraction(2), new Fraction(5), new Fraction(-1), new Fraction(4)},
                {new Fraction(1), new Fraction(-1), new Fraction(-1), new Fraction(2), new Fraction(1)},
        });

        matrix.rendererColumn(basisVector);
        matrix.gauss();
        matrix.restoreColumnOrderWithVector();
        System.out.println();
        matrix.printMatrix();
        System.out.println(Arrays.toString(matrix.solution(targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy))));
        matrix.addRow(matrix.solution(targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy)));
        matrix.removeColumns(matrix.isBasicVector(vectorCopy));
        matrix.printMatrix();

        SimplexMethod table = new SimplexMethod(matrix, targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy));
        System.out.println();


        if (table.getNegativeVariableIndices().isEmpty()) {
            System.out.println(matrix.getElement(matrix.getRows(), matrix.getCols()).multiply(Fraction.NEGATIVE_ONE));
            return;
        }

        table.printTable();

        List<int[]> supportElements = table.getSupportElement(table.getNegativeVariableIndices());

        System.out.println("Опорные элементы:");
        for (int[] element : supportElements) {
            System.out.println("Строка: " + (element[0] + 1) + ", Столбец: " + (element[1] + 1));
        }

        table.simplexMove(1, 0, table.getNegativeVariableIndices());

        if (table.getNegativeVariableIndices().isEmpty()) {
            table.printAnswer();
            return;
        }

        supportElements = table.getSupportElement(table.getNegativeVariableIndices());
        System.out.println("Опорные элементы:");
        for (int[] element : supportElements) {
            System.out.println("Строка: " + (element[0] + 1) + ", Столбец: " + (element[1] + 1));
        }

        table.printTable();
        table.simplexMove(0, 1, table.getNegativeVariableIndices());

        table.printTable();
        if (table.getNegativeVariableIndices().isEmpty()) {
            table.printAnswer();
            return;
        }

        table.printTable();
    }
}
