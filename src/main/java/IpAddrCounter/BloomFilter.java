package IpAddrCounter;

import java.util.BitSet;

public class BloomFilter {
    private BitSet bitset;
    private int bitsetSize;
    private int hashFunctions;

    public BloomFilter(int size, double falsePositiveProbability) {
        this.bitsetSize = (int) (-size * Math.log(falsePositiveProbability) / (Math.log(2) * Math.log(2)));
        this.hashFunctions = Math.max(1, (int) Math.round((double) this.bitsetSize / size * Math.log(2)));
        this.bitset = new BitSet(bitsetSize);
    }

    public void add(String value) {
        int[] hashCodes = getHashCodes(value);
        for (int code : hashCodes) {
            bitset.set(Math.abs(code % bitsetSize), true);
        }
    }

    public boolean mightContain(String value) {
        int[] hashCodes = getHashCodes(value);
        for (int code : hashCodes) {
            if (!bitset.get(Math.abs(code % bitsetSize))) {
                return false;
            }
        }
        return true;
    }

    private int[] getHashCodes(String value) {
        int[] result = new int[hashFunctions];
        for (int i = 0; i < hashFunctions; i++) {
            result[i] = value.hashCode() ^ (i * 31);
        }
        return result;
    }
}
