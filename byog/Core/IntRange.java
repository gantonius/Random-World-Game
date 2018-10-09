package byog.Core;

import java.io.Serializable;
import java.util.Random;

public class IntRange implements Serializable {
    int mMin;
    int mMax;
    Random rand;

    public IntRange(int min, int max, MapGenerator m1) {
        mMin = min;
        mMax = max;
        rand = new Random(m1.SEED);
    }

    public int getRandom() {
        return RandomUtils.uniform(rand, mMin, mMax + 1);
    }
}
