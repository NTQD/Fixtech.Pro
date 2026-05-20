package vn.aeoc.base.util;


import java.util.function.Function;

public class Mapper {
    public static <T, R> R map(T value, Function<T, R> mapper) {
        return mapper.apply(value);
    }
}