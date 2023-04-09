package dev.demon.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Buffer {

    private final double limit;
    private double value;

    public double add(final double amount) {
        return this.value = Math.min(this.limit, this.value + amount);
    }

    public void set(final double amount){
        this.value = amount;
    }

    public void reduce(final double amount) {
        this.value = Math.max(0.0, this.value - amount);
    }

    public double add() {
        return add(1.0);
    }

    public void reduce() {
        reduce(1.0);
    }

    public void reset() {
        set(0.0);
    }

}
