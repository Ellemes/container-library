package ellemes.container_library.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class ListBuilder<T> {
    private final IntFunction<T> generator;
    private final List<Integer> values = new ArrayList<>();

    public ListBuilder(IntFunction<T> generator) {
        this.generator = generator;
    }

    public ListBuilder<T> range(int min, int max, int step) {
        for (int i = min; i <= max; i += step) {
            values.add(i);
        }
        return this;
    }

    public ListBuilder<T> single(int value) {
        values.add(value);
        return this;
    }

    public List<T> build() {
        values.sort(null);
        return values.stream().map(generator::apply).collect(Collectors.toList());
    }
}
