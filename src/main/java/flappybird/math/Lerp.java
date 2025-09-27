package flappybird.math;

public class Lerp {
    public static float lerp(float start, float end, float t) {
        return start * (1 - t) + end * t;
    }
}
