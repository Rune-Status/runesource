package com.rs.net;
/*
 * This file is part of RuneSource.
 *
 * RuneSource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RuneSource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RuneSource.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * <p>
 * An implementation of an ISAAC cipher. See <a href="http://en.wikipedia.org/wiki/ISAAC_(cipher)">
 * http://en.wikipedia.org/wiki/ISAAC_(cipher)</a> for more information.
 * </p>
 * <p>
 * This implementation is based on the one written by Bob Jenkins, which is available at
 * <a href="http://www.burtleburtle.net/bob/java/rand/Rand.java">
 * http://www.burtleburtle.net/bob/java/rand/Rand.java</a>.
 * </p>
 *
 * @author Graham Edgecombe
 */
public final class ISAACCipher {

    /**
     * The golden ratio.
     */
    private static final int RATIO = 0x9e3779b9;

    /**
     * The log of the size of the results and memory arrays.
     */
    private static final int SIZE_LOG = 8;

    /**
     * The size of the results and memory arrays.
     */
    private static final int SIZE = 1 << SIZE_LOG;

    /**
     * For pseudo-random lookup.
     */
    private static final int MASK = (SIZE - 1) << 2;

    /**
     * The count through the results.
     */
    private int count = 0;

    /**
     * The results.
     */
    private final int[] results = new int[SIZE];

    /**
     * The internal memory state.
     */
    private final int[] memory = new int[SIZE];

    /**
     * The accumulator.
     */
    private int a;

    /**
     * The last result.
     */
    private int b;

    /**
     * The counter.
     */
    private int c;

    /**
     * Creates the ISAAC cipher.
     *
     * @param seed The seed.
     */
    public ISAACCipher(int[] seed) {
        System.arraycopy(seed, 0, results, 0, seed.length);
        init(true);
    }

    /**
     * Gets the next value.
     *
     * @return The next value.
     */
    public int getNextValue() {
        if (count-- == 0) {
            isaac();
            count = SIZE - 1;
        }
        return results[count];
    }

    /**
     * Generates 256 results.
     */
    private void isaac() {
        int i, j, x, y;
        b += ++c;

        for (i = 0, j = SIZE / 2; i < SIZE / 2; ) {
            x = memory[i];
            a ^= a << 13;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 6;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a << 2;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 16;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;
        }

        for (j = 0; j < SIZE / 2; ) {
            x = memory[i];
            a ^= a << 13;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 6;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a << 2;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;

            x = memory[i];
            a ^= a >>> 16;
            a += memory[j++];
            memory[i] = y = memory[(x & MASK) >> 2] + a + b;
            results[i++] = b = memory[((y >> SIZE_LOG) & MASK) >> 2] + x;
        }
    }

    /**
     * Initialises the ISAAC.
     *
     * @param secondPass Flag indicating if we should perform a second pass.
     */
    private void init(boolean secondPass) {
        int i;
        int a, b, c, d, e, f, g, h;
        a = b = c = d = e = f = g = h = RATIO;

        for (i = 0; i < 4; ++i) {
            a ^= b << 11;
            d += a;
            b += c;
            b ^= c >>> 2;
            e += b;
            c += d;
            c ^= d << 8;
            f += c;
            d += e;
            d ^= e >>> 16;
            g += d;
            e += f;
            e ^= f << 10;
            h += e;
            f += g;
            f ^= g >>> 4;
            a += f;
            g += h;
            g ^= h << 8;
            b += g;
            h += a;
            h ^= a >>> 9;
            c += h;
            a += b;
        }

        for (i = 0; i < SIZE; i += 8) {
            if (secondPass) {
                a += results[i];
                b += results[i + 1];
                c += results[i + 2];
                d += results[i + 3];
                e += results[i + 4];
                f += results[i + 5];
                g += results[i + 6];
                h += results[i + 7];
            }
            a ^= b << 11;
            d += a;
            b += c;
            b ^= c >>> 2;
            e += b;
            c += d;
            c ^= d << 8;
            f += c;
            d += e;
            d ^= e >>> 16;
            g += d;
            e += f;
            e ^= f << 10;
            h += e;
            f += g;
            f ^= g >>> 4;
            a += f;
            g += h;
            g ^= h << 8;
            b += g;
            h += a;
            h ^= a >>> 9;
            c += h;
            a += b;
            memory[i] = a;
            memory[i + 1] = b;
            memory[i + 2] = c;
            memory[i + 3] = d;
            memory[i + 4] = e;
            memory[i + 5] = f;
            memory[i + 6] = g;
            memory[i + 7] = h;
        }

        if (secondPass) {
            for (i = 0; i < SIZE; i += 8) {
                a += memory[i];
                b += memory[i + 1];
                c += memory[i + 2];
                d += memory[i + 3];
                e += memory[i + 4];
                f += memory[i + 5];
                g += memory[i + 6];
                h += memory[i + 7];
                a ^= b << 11;
                d += a;
                b += c;
                b ^= c >>> 2;
                e += b;
                c += d;
                c ^= d << 8;
                f += c;
                d += e;
                d ^= e >>> 16;
                g += d;
                e += f;
                e ^= f << 10;
                h += e;
                f += g;
                f ^= g >>> 4;
                a += f;
                g += h;
                g ^= h << 8;
                b += g;
                h += a;
                h ^= a >>> 9;
                c += h;
                a += b;
                memory[i] = a;
                memory[i + 1] = b;
                memory[i + 2] = c;
                memory[i + 3] = d;
                memory[i + 4] = e;
                memory[i + 5] = f;
                memory[i + 6] = g;
                memory[i + 7] = h;
            }
        }
        isaac();
        count = SIZE;
    }

}