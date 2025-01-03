package simplexmetod;

import java.math.BigDecimal;

/**
 * Класс для дробей
 */
public class Fraction {

    /**
     * Дробь со значением 0/1.
     */
    public static final Fraction ZERO = new Fraction(0);
    /**
     * Дробь со значением 1/1.
     */
    public static final Fraction ONE = new Fraction(1);
    /**
     * Дробь со значением -1/1.
     */
    public static final Fraction NEGATIVE_ONE = new Fraction(-1);

    private int numerator;   // Числитель
    private int denominator;

    /**
     * Конструктор для создания дробей
     *
     * @param numerator   Числитель
     * @param denominator Знаменатель
     * @throws IllegalArgumentException Знаменатель не может быть равен 0
     */
    public Fraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Знаменатель не может быть равен нулю.");
        }

        this.numerator = numerator;
        this.denominator = denominator;

        simplify();
    }

    /**
     * Коструктор для создания дроби из целого числа
     *
     * @param numerator Целое число
     */
    public Fraction(int numerator) {

        this(numerator, 1);
    }

    /**
     * Конструктор для создания дроби из десятичного числа.
     *
     * @param decimal Десятичное число
     */
    public Fraction(double decimal) {
        if (decimal == 0) {
            this.numerator = 0;
            this.denominator = 1;
        } else {
            BigDecimal bd = new BigDecimal(decimal);
            int decimalPlaces = bd.scale();
            this.denominator = (int) Math.pow(10, decimalPlaces);
            this.numerator = bd.movePointRight(decimalPlaces).intValue();

            simplify();
        }
    }

    /**
     * Получение значения числителя.
     *
     * @return Числитель.
     */
    public int getNumerator() {
        return numerator;
    }

    /**
     * Получение значения знаменателя.
     *
     * @return Знаменатель.
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Упрощает дробь до несократимого вида
     */
    private void simplify() {
        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= gcd;
        denominator /= gcd;

        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    /**
     * Находит максимальное значение среди двух дробей.
     *
     * @param f1 Первая дробь.
     * @param f2 Вторая дробь.
     * @return Возвращает максимальное значение.
     * @throws IllegalArgumentException Дробь имеет значение null.
     */
    public static Fraction max(Fraction f1, Fraction f2) {
        if (f1 == null || f2 == null) {
            throw new IllegalArgumentException("Дроби не могут быть null.");
        }
        return f1.isGreaterThan(f2) ? f1 : f2;
    }

    /**
     * Нахождение НОД двух чисел
     *
     * @param firstNum  Первое число
     * @param secondNum Второе число
     * @return НОД
     * @throws IllegalArgumentException Оба числа должны быть неотрицательны
     */
    public int gcd(int firstNum, int secondNum) {
        if (firstNum < 0 || secondNum < 0) {
            throw new IllegalArgumentException("Числа должны быть неотрицательными.");
        }

        // Алгоритм Евклида
        while (secondNum != 0) {
            int temp = secondNum;
            secondNum = firstNum % secondNum;
            firstNum = temp;
        }

        return firstNum;
    }

    /**
     * Нахождение НОК для двух дробей
     *
     * @param secondFraction Вторая дробь
     * @return Новая дробь, представляющая НОК двух дробей
     */
    public Fraction lcm(Fraction secondFraction) {
        // Находим НОК числителей
        int numeratorLcm = lcm(this.numerator, secondFraction.numerator);

        // Находим НОД знаменателей
        int denominatorGcd = gcd(this.denominator, secondFraction.denominator);

        // Создаем новую дробь с НОК и НОД
        return new Fraction(numeratorLcm, denominatorGcd);
    }

    /**
     * Нахождение НОК двух целых чисел
     *
     * @param firstNum  Первое число
     * @param secondNum Второе число
     * @return НОК
     * @throws IllegalArgumentException Оба числа должны быть неотрицательны
     */
    private int lcm(int firstNum, int secondNum) {
        if (firstNum < 0 || secondNum < 0) {
            throw new IllegalArgumentException("Числа должны быть неотрицательными.");
        }
        return Math.abs(firstNum * secondNum) / gcd(firstNum, secondNum);
    }

    /**
     * Сложение дробей
     *
     * @param secondFraction Вторая дробь
     * @return Дробь
     */
    public Fraction add(Fraction secondFraction) {
        int newNumerator = this.numerator * secondFraction.denominator +
                secondFraction.numerator * this.denominator;
        int newDenominator = this.denominator * secondFraction.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Вычитание дробей
     *
     * @param secondFraction Вторая дробь
     * @return Результат вычитания
     */
    public Fraction subtract(Fraction secondFraction) {
        int newNumerator = this.numerator * secondFraction.denominator -
                secondFraction.numerator * this.denominator;
        int newDenominator = this.denominator * secondFraction.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Умножение дробей
     *
     * @param secondFraction Вторая дробь
     * @return Результат умножения
     */
    public Fraction multiply(Fraction secondFraction) {
        int newNumerator = this.numerator * secondFraction.numerator;
        int newDenominator = this.denominator * secondFraction.denominator;

        return new Fraction(newNumerator, newDenominator);
    }

    /**
     * Деление дробей
     *
     * @param secondFraction Вторая дробь
     * @return Результат деления
     * @throws IllegalArgumentException Деление на ноль
     */
    public Fraction divide(Fraction secondFraction) {
        if (secondFraction.numerator == 0) {
            throw new IllegalArgumentException("Деление на ноль.");
        }
        return multiply(new Fraction(secondFraction.denominator, secondFraction.numerator));
    }

    /**
     * Проверка на то, что число является целым.
     *
     * @param number Число для проверки
     * @return true, если число является целым; false в противном случае.
     */
    private boolean isInteger(double number) {
        return number == Math.floor(number);
    }

    /**
     * Преобразует дробь в десятичное число
     *
     * @return Десятичную дробь
     */
    public double toDecimal() {
        return (double) numerator / denominator;
    }

    /**
     * Проверяет, равны ли две дроби
     *
     * @param secondFraction Вторая дробь
     * @return true - дроби одинаковы; false - иначе
     */
    public boolean isEqualTo(Fraction secondFraction) {
        return this.numerator * secondFraction.denominator == secondFraction.numerator * this.denominator;
    }

    /**
     * Проверяет, больше ли текущая дробь второй.
     *
     * @param secondFraction Вторая дробь для сравнения.
     * @return true - текущая дробь больше; false - иначе.
     */
    public boolean isGreaterThan(Fraction secondFraction) {
        return this.numerator * secondFraction.denominator > secondFraction.numerator * this.denominator;
    }

    /**
     * Проверяет, меньше ли текущая дробь второй.
     *
     * @param secondFraction Вторая дробь для сравнения.
     * @return true - текущая дробь меньше; false - иначе.
     */
    public boolean isLessThan(Fraction secondFraction) {
        return this.numerator * secondFraction.denominator < secondFraction.numerator * this.denominator;
    }

    /**
     * Создает дробь из строкового представления вида "числитель/знаменатель".
     *
     * @param fractionStr Строка с представлением дроби.
     * @return Новый объект Fraction.
     * @throws IllegalArgumentException Если строка имеет неверный формат.
     */
    public static Fraction fromString(String fractionStr) {
        if (fractionStr == null || fractionStr.isBlank()) {
            throw new IllegalArgumentException("Строка дроби не должна быть пустой или null.");
        }

        String[] parts = fractionStr.split("/");
        if (parts.length == 1) {
            int num = Integer.parseInt(parts[0].trim());
            return new Fraction(num, 1);
        } else if (parts.length == 2) {
            int num = Integer.parseInt(parts[0].trim());
            int den = Integer.parseInt(parts[1].trim());
            if (den == 0) {
                throw new ArithmeticException("Знаменатель не может быть равен нулю.");
            }
            return new Fraction(num, den);
        } else {
            throw new IllegalArgumentException("Неверный формат дроби. Верный формат: 'числитель/знаменатель' или 'число'.");
        }
    }

    /**
     * Строковое представление дроби в формате числитель/знаменатель
     *
     * @return Строка с представлением дроби
     */
    @Override
    public String toString() {
        if (denominator == 1) {
            return Integer.toString(numerator);
        }

        return numerator + "/" + denominator;
    }

}
