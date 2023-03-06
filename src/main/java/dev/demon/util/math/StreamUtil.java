package dev.demon.util.math;

import cc.funkemunky.api.utils.Tuple;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.var;

import java.util.*;
import java.util.function.Predicate;

public class StreamUtil {

    public static <T> Collection<T> filter(Collection<T> data, Predicate<T> filter) {

        List<T> list = new LinkedList<>();

        if (filter == null || data.isEmpty()) return list;

        for (T object : data) {

            if (filter.test(object)) list.add(object);
        }

        return list;
    }

    public static double mean(Collection<? extends Number> samples) {
        double sum = 0D;

        for (Number val : samples) sum += val.doubleValue();

        return sum / samples.size();
    }

    public static Number getMode(Collection<? extends Number> samples) {
        Map<Number, Integer> frequencies = new HashMap<>();

        samples.forEach(i -> frequencies.put(i, frequencies.getOrDefault(i, 0) + 1));

        Number mode = null;
        int highest = 0;

        for (var entry : frequencies.entrySet()) {
            if (entry.getValue() > highest) {
                mode = entry.getKey();
                highest = entry.getValue();
            }
        }

        return mode;
    }

    public static double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
    }


    /**
     * @param - collection The collection of the numbers you want to get the duplicates from
     * @return - The duplicate amount
     */
    public static int getDuplicates(final Collection<? extends Number> collection) {
        return collection.size() - getDistinct(collection);
    }


    public static double getKurtosis(Collection<? extends Number> values) {
        double n = values.size();

        if (n < 3)
            return Double.NaN;

        double average = getAverage(values);
        double stDev = getStandardDeviation(values);

        AtomicDouble accum = new AtomicDouble(0D);

        values.forEach(delay -> accum.getAndAdd(Math.pow(delay.doubleValue() - average, 4D)));

        return n * (n + 1) / ((n - 1) * (n - 2) * (n - 3)) *
                (accum.get() / Math.pow(stDev, 4D)) - 3 *
                Math.pow(n - 1, 2D) / ((n - 2) * (n - 3));
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }


    public static double getSkewness(Iterable<? extends Number> iterable) {
        double sum = 0;
        int buffer = 0;

        List<Double> numberList = new ArrayList<>();

        for (Number num : iterable) {
            sum += num.doubleValue();
            buffer++;

            numberList.add(num.doubleValue());
        }

        Collections.sort(numberList);

        double mean = sum / buffer;
        double median = (buffer % 2 != 0) ? numberList.get(buffer / 2) : (numberList.get((buffer - 1) / 2) + numberList.get(buffer / 2)) / 2;

        return 3 * (mean - median) / deviationSquared(iterable);
    }

    public static double deviationSquared(Iterable<? extends Number> iterable) {
        double n = 0.0;
        int n2 = 0;

        for (Number anIterable : iterable) {
            n += (anIterable).doubleValue();
            ++n2;
        }

        double n3 = n / n2;
        double n4 = 0.0;

        for (Number anIterable : iterable) {
            n4 += Math.pow(anIterable.doubleValue() - n3, 2.0);
        }

        return (n4 == 0.0) ? 0.0 : (n4 / (n2 - 1));
    }

    public static double getMedian(Iterable<? extends Number> iterable) {
        List<Double> data = new ArrayList<>();

        for (Number number : iterable) {
            data.add(number.doubleValue());
        }

        return getMedian(data);
    }

    public static double getDeviation(final Collection<? extends Number> nums) {
        if (nums.isEmpty()) return 0D;

        return Math.sqrt((getVariance(nums) / (nums.size() - 1)));
    }

    public static double getVariance(final Collection<? extends Number> data) {
        if (data.isEmpty()) return 0D;

        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        double average;

        // Increase the sum and the count to find the average and the standard deviation
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        // Run the standard deviation formula
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static int getDistinct(final Collection<? extends Number> collection) {
        if (collection.isEmpty()) return 0;

        return new HashSet<>(collection).size();
    }

    public static double getMaximumDouble(final Collection<Double> nums) {
        if (nums.isEmpty()) return 0.0d;

        double max = Double.MIN_VALUE;

        for (final double val : nums) {
            if (val > max) max = val;
        }

        return max;
    }

    public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }

    public static double getMedian(List<Double> data) {
        if (data.size() > 1) {
            if (data.size() % 2 == 0)
                return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
            else
                return data.get(Math.round(data.size() / 2f));
        }
        return 0;
    }

    public static Tuple<List<Double>, List<Double>> getOutliers(Collection<? extends Number> collection) {
        List<Double> values = new ArrayList<>();

        for (Number number : collection) {
            values.add(number.doubleValue());
        }

        if (values.size() < 4) return new Tuple<>(new ArrayList<>(), new ArrayList<>());

        double q1 = getMedian(values.subList(0, values.size() / 2)),
                q3 = getMedian(values.subList(values.size() / 2, values.size()));
        double iqr = Math.abs(q1 - q3);

        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        Tuple<List<Double>, List<Double>> tuple = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        for (Double value : values) {
            if (value < lowThreshold) tuple.one.add(value);
            else if (value > highThreshold) tuple.two.add(value);
        }

        return tuple;
    }

    public static int getMaximumInt(final Collection<Integer> nums) {
        if (nums.isEmpty()) return 0;

        int max = Integer.MIN_VALUE;

        for (final int val : nums) {
            if (val > max) max = val;
        }

        return max;
    }

    public static long getMaximumLong(final Collection<Long> nums) {
        if (nums.isEmpty()) return 0L;

        long max = Long.MIN_VALUE;

        for (final long val : nums) {

            if (val > max) max = val;
        }

        return max;
    }

    public static float getMaximumFloat(final Collection<Float> nums) {
        if (nums.isEmpty()) return 0.0f;

        float max = Float.MIN_VALUE;

        for (final float val : nums) {

            if (val > max) max = val;
        }

        return max;
    }

    public static double getMinimumDouble(final Collection<Double> nums) {
        if (nums.isEmpty()) return 0.0d;

        double min = Double.MAX_VALUE;

        for (final double val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static int getMinimumInt(final Collection<Integer> nums) {
        if (nums.isEmpty()) return 0;

        int min = Integer.MAX_VALUE;

        for (final int val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static long getMinimumLong(final Collection<Long> nums) {
        if (nums.isEmpty()) return 0L;

        long min = Long.MAX_VALUE;

        for (final long val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static float getMinimumFloat(final Collection<Float> nums) {
        if (nums.isEmpty()) return 0.0f;

        float min = Float.MAX_VALUE;

        for (final float val : nums) {

            if (val < min) min = val;
        }

        return min;
    }

    public static <T> boolean anyMatch(final List<T> objects, final Predicate<T> condition) {
        if (condition == null) return false;

        for (final T object : objects) {

            if (condition.test(object)) return true;
        }

        return false;
    }

    public static <T> boolean allMatch(final Collection<T> collection, final Predicate<T> condition) {
        if (condition == null) return false;

        for (final T object : collection) {

            if (!condition.test(object)) return false;
        }

        return true;
    }

    public static <T> List<T> getFiltered(final Collection<T> data, final Predicate<T> filter) {

        final List<T> list = new LinkedList<>();

        if (filter == null || data.isEmpty()) return list;

        for (final T object : data) {

            if (filter.test(object)) list.add(object);
        }

        return list;
    }

    public static int filteredCount(final Collection<? extends Number> data, final Predicate<Number> filter) {
        if (filter == null || data.isEmpty()) return 0;

        int count = 0;

        for (final Number num : data) {

            if (filter.test(num)) count++;
        }

        return count;
    }
}
