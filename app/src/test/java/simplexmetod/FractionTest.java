package simplexmetod;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FractionTest {

    @Test
    public void testFractionFromDecimal() {
        Fraction fraction1 = new Fraction(0.75);
        assertThat(fraction1.toString()).isEqualTo("3/4");

        Fraction fraction2 = new Fraction(2.5);
        assertThat(fraction2.toString()).isEqualTo("5/2");

        Fraction fraction3 = new Fraction(-0.75);
        assertThat(fraction3.toString()).isEqualTo("-3/4");

        Fraction fraction5 = new Fraction(0);
        assertThat(fraction5.toString()).isEqualTo("0/1");
    }

    @Test
    public void testAddition() {
        Fraction f1 = new Fraction(1, 2); // 1/2
        Fraction f2 = new Fraction(1, 3); // 1/3
        Fraction result = f1.add(f2);
        assertThat(result.toString()).isEqualTo("5/6");
    }

    @Test
    public void testSubtraction() {
        Fraction f1 = new Fraction(3, 4); // 3/4
        Fraction f2 = new Fraction(1, 4); // 1/4
        Fraction result = f1.subtract(f2);
        assertThat(result.toString()).isEqualTo("1/2");
    }

    @Test
    public void testMultiplication() {
        Fraction f1 = new Fraction(2, 3); // 2/3
        Fraction f2 = new Fraction(3, 4); // 3/4
        Fraction result = f1.multiply(f2);
        assertThat(result.toString()).isEqualTo("1/2");
    }

    @Test
    public void testDivision() {
        Fraction f1 = new Fraction(1, 2); // 1/2
        Fraction f2 = new Fraction(3, 4); // 3/4
        Fraction result = f1.divide(f2);
        assertThat(result.toString()).isEqualTo("2/3");
    }

    @Test
    public void testComparison() {
        Fraction f1 = new Fraction(1, 2); // 1/2

        assertThat(f1.isGreaterThan(Fraction.fromString("3/4"))).isFalse();
        assertThat(f1.isEqualTo(new Fraction(50, 100))).isTrue();
    }


    @Test
    public void testFromString() {
        Fraction fraction = Fraction.fromString("3/5");
        assertThat(fraction.toString()).isEqualTo("3/5");

    }
}
