package utils;

public class vector2<T extends Number>{
    public T x;
    public T y;
    public vector2(T x, T y)
    {
        this.x = x;
        this.y = y;
    }

    public vector2() {}

    @Override
    public String toString()
    {
        return "vector ( "+ this.x + ", " + this.y + " )";
    }
    public boolean isNull() {
        return this.x == null && this.y == null;
    }
    public boolean isZeroOrNegative() {
        return this.x.doubleValue() <= 0 || this.y.doubleValue() <= 0;
    }
}