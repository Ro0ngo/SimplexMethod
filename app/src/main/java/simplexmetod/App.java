package simplexmetod;

import java.util.Arrays;
import java.util.HashMap;

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
        boolean[] basisVector = { false, true, true, true };
        boolean[] vectorCopy = Arrays.copyOf(basisVector, basisVector.length);
        Fraction[] targetFunction = {new Fraction(2), new Fraction(0), new Fraction(1), new Fraction(1), new Fraction(3)};

        Matrix matrix = new Matrix(new Fraction[][] {
                { new Fraction(9, 155), new Fraction(0), new Fraction(0), new Fraction(1), new Fraction(6, 31) },
                { new Fraction(-42, 155), new Fraction(1), new Fraction(0), new Fraction(0), new Fraction(34, 31) },
                { new Fraction(194, 155), new Fraction(0), new Fraction(1), new Fraction(0), new Fraction(150, 31) }
        });

        matrix.rendererColumn(basisVector);
        matrix.gauss();
        matrix.restoreColumnOrderWithVector();
        matrix.addRow(matrix.solution(targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy)));
        matrix.removeColumns(matrix.isBasicVector(vectorCopy));
        matrix.printMatrix();

        SimplexMethod table = new SimplexMethod(matrix, targetFunction, matrix.isBasicVector(vectorCopy), matrix.isFreeVector(vectorCopy));
        System.out.println();
        table.simplexMove(1, 0);
        table.printTable();
    }
}
