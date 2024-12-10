package tech.konata.opai.halo.rendering.animation;

import lombok.Getter;

import java.util.function.Function;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

@Getter
public enum Easing {
    LINEAR(x -> x),
    EASE_IN_QUAD(x -> x * x),
    EASE_OUT_QUAD(x -> x * (2 - x)),
    EASE_IN_OUT_QUAD(x -> x < 0.5 ? 2 * x * x : -1 + (4 - 2 * x) * x),
    EASE_IN_CUBIC(x -> x * x * x),
    EASE_OUT_CUBIC(x -> (--x) * x * x + 1),
    EASE_IN_OUT_CUBIC(x -> x < 0.5 ? 4 * x * x * x : (x - 1) * (2 * x - 2) * (2 * x - 2) + 1),
    EASE_IN_QUART(x -> x * x * x * x),
    EASE_OUT_QUART(x -> 1 - (--x) * x * x * x),
    EASE_IN_OUT_QUART(x -> x < 0.5 ? 8 * x * x * x * x : 1 - 8 * (--x) * x * x * x),
    EASE_IN_QUINT(x -> x * x * x * x * x),
    EASE_OUT_QUINT(x -> 1 + (--x) * x * x * x * x),
    EASE_IN_OUT_QUINT(x -> x < 0.5 ? 16 * x * x * x * x * x : 1 + 16 * (--x) * x * x * x * x),
    EASE_IN_SINE(x -> 1 - Math.cos(x * Math.PI / 2)),
    EASE_OUT_SINE(x -> sin(x * Math.PI / 2)),
    EASE_IN_OUT_SINE(x -> 1 - Math.cos(Math.PI * x / 2)),
    EASE_IN_EXPO(x -> x == 0 ? 0 : pow(2, 10 * x - 10)),
    EASE_OUT_EXPO(x -> x == 1 ? 1 : 1 - pow(2, -10 * x)),
    EASE_IN_OUT_EXPO(x -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? pow(2, 20 * x - 10) / 2 : (2 - pow(2, -20 * x + 10)) / 2),
    EASE_IN_CIRC(x -> 1 - Math.sqrt(1 - x * x)),
    EASE_OUT_CIRC(x -> Math.sqrt(1 - (--x) * x)),
    EASE_IN_OUT_CIRC(x -> x < 0.5 ? (1 - Math.sqrt(1 - 4 * x * x)) / 2 : (Math.sqrt(1 - 4 * (x - 1) * x) + 1) / 2),
    SIGMOID(x -> 1 / (1 + Math.exp(-x))),
    EASE_OUT_ELASTIC(x -> x == 0 ? 0 : x == 1 ? 1 : pow(2, -10 * x) * sin((x * 10 - 0.75) * ((2 * Math.PI) / 3)) * 0.5 + 1),
    EASE_IN_BACK(x -> (1.70158 + 1) * x * x * x - 1.70158 * x * x),
    BEZIER(x -> {
        double v = x < 0.2 ? 3.125 * StrictMath.pow(x, 2) : x > 0.8f ? -3.125 * StrictMath.pow(x, 2) + 6.25 * x - 2.125 : 1.25 * (x - 0.1);

        return (v < 0.0d) ? 0.0d : Math.min(v, 1.0d);
    });

    private final Function<Double, Double> function;

    Easing(final Function<Double, Double> function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", " ");
    }

}