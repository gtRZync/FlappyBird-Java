package flappybird.math;

public class Vector2<T extends Number>{
    public T x;
    public T y;
    public Vector2(T x, T y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2() {}

    @Override
    public String toString()
    {
        return "vector ( "+ this.x + ", " + this.y + " )";
    }
}