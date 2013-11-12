import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ryanphung
 * Date: 12/11/13
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SampleVariance {
    public static final boolean LOGGING = false;
    public static final int POPULATION_SIZE = 10000;
    public static final int SAMPLE_SIZE = 10;
    public static final Double MAX_NUMBER = 1000000.0; // maximum number used in the calculation
    public static final int RUNS_COUNT = 1000; // number of time we test

    /**
     * Generate a random population given the population size
     * and the range of each value
     * @param size
     * @param min
     * @param max
     * @return
     */
    public static Double[] generatePopulation(int size, Double min, Double max) {
        Double[] population = new Double[size];
        for (int i = 0; i < size; i++) {
            population[i] = Math.random() * (max - min) + min;
        }
        return population;
    }

    /**
     * Pick a sample from a given population
     * @param population
     * @param sampleSize
     * @return
     * @throws Exception
     */
    public static Double[] pickSample(Double[] population, int sampleSize) throws Exception {
        if (sampleSize > population.length) {
            throw new Exception("Sample size must be smaller or equal to population size");
        }

        Set<Double> sample = new HashSet<Double>();
        int count = sampleSize;

        while (count > 0) {
            int i = (int)(Math.random() * population.length);
            if (!sample.contains(population[i])) {
                sample.add(population[i]);
                count--;
            }
        }

        return sample.toArray(new Double[0]);
    }

    /**
     * Calculate the mean of a set of values (could be population, or sample)
     * @param set
     * @return
     */
    public static Double mean(Double[] set) {
        Double sum = 0.0;
        for (int i = 0; i < set.length; i++) {
            sum += set[i];
        }

        return sum / set.length;
    }

    /**
     * Calculate the variance of a set of value (could be population, or sample - uncorrected)
     * @param set
     * @param mean
     * @return
     */
    public static Double variance(Double[] set, Double mean) {
        Double sum = 0.0;
        for (int i = 0; i < set.length; i++) {
            sum += (set[i] - mean) * (set[i] - mean);
        }

        return sum / set.length;
    }

    /**
     * Correct the sample variance from the variance previously calculated
     * @param variance
     * @param sampleSize
     * @return
     */
    public static Double varianceCorrected(Double variance, int sampleSize) {
        return variance * sampleSize / (sampleSize - 1);
    }

    /**
     * Print the content of a set of numbers to stdout
     * @param set
     */
    public static void printSet(Double[] set) {
        if (LOGGING) {
            for (int i = 0; i < set.length; i++)
                System.out.print(set[i] + ",");
            System.out.println();
        }
    }

    /**
     * Log a string to stdout
     * @param s
     */
    public static void log(String s) {
        if (LOGGING)
            System.out.println(s);
    }

    public static void main(String[] args) throws Exception {
        Double[] population, sample;
        int populationSize, sampleSize;
        Double min, max;
        Double populationMean, sampleMean, populationVariance, sampleVariance, sampleVarianceCorrected, comparison;
        Double accumulatedGoodCorrection = 0.0, accumulatedBadCorrection = 0.0;
        int goodCorrectionCount = 0, badCorrectionCount = 0;
        DecimalFormat df = new DecimalFormat("0.##");

        // initialize the variables with a set of fixed value
        // this could be changed in every run
        populationSize = POPULATION_SIZE;
        sampleSize = SAMPLE_SIZE;
        max = MAX_NUMBER;
        min = 0.0;

        for (int i = 0; i < RUNS_COUNT; i++) {
            // uncomment this part to randomize the variables in every run
            /*populationSize = (int)(Math.random() * (POPULATION_SIZE - 2)) + 2;
            sampleSize = (int)(Math.random() * (populationSize - 2)) + 2;
            max = Math.random() * MAX_NUMBER;
            min = Math.random() * max;*/

            log("TEST VARIABLES: ");
            log("Population Size = " + populationSize);
            log("Sample Size     = " + sampleSize);
            log("Max Number      = " + df.format(max));
            log("Min Number      = " + df.format(min));

            population = generatePopulation(populationSize, min, max);
            printSet(population);

            sample = pickSample(population, sampleSize);
            printSet(sample);

            populationMean = mean(population);
            populationVariance = variance(population, populationMean);

            sampleMean = mean(sample);
            sampleVariance = variance(sample, sampleMean);
            sampleVarianceCorrected = varianceCorrected(sampleVariance, sample.length);

            log("RAW RESULTS: ");
            log("Population Mean      = " + df.format(populationMean));
            log("Sample Mean          = " + df.format(sampleMean));
            log("Population Var       = " + df.format(populationVariance));
            log("Sample Var           = " + df.format(sampleVariance));
            log("Sample Var Corrected = " + df.format(sampleVarianceCorrected));

            // compare to see whether uncorrected sample variance or corrected sample variance
            // is closer to the actual population variance
            comparison = Math.abs(populationVariance - sampleVariance) - Math.abs(populationVariance - sampleVarianceCorrected);

            log("RESULTS: ");
            if (comparison > 0) {
                log("Good correction.");
                goodCorrectionCount++;
                accumulatedGoodCorrection += comparison;
            } else if (comparison < 0) {
                log("Bad correction.");
                badCorrectionCount++;
                accumulatedBadCorrection -= comparison;
            } else
                log("No effect!");
        }

        // Printing out results
        System.out.println("Bad corrections = " + badCorrectionCount +
                "; good corrections = " + goodCorrectionCount +
                " (" + (goodCorrectionCount*100/(goodCorrectionCount + badCorrectionCount)) + "%)");
        System.out.println("Average good correction / average bad correction = " +
                df.format((accumulatedGoodCorrection / goodCorrectionCount) /
                        (accumulatedBadCorrection / badCorrectionCount) * 100
                ) + "%");
    }
}