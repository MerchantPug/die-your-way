/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package io.github.merchantpug.dieyourway.util;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum Shape {
    CUBE, CHEBYSHEV,
    STAR, MANHATTAN,
    SPHERE, EUCLIDEAN;

    public static Collection<BlockPos> getPositions(BlockPos center, Shape shape, int radius) {
        Set<BlockPos> positions = new HashSet<>();
        for(int i = -radius; i <= radius; i++) {
            for(int j = -radius; j <= radius; j++) {
                for(int k = -radius; k <= radius; k++) {
                    if(shape == Shape.CUBE || shape == Shape.CHEBYSHEV
                            || (shape == Shape.SPHERE || shape == Shape.EUCLIDEAN)
                            && i * i + j * j + k * k <= radius * radius
                            // The radius can't be negative here (the loops aren't even entered in that case)
                            // so there's no behavior change from testing that sqrt(i*i + j*j + k*k) <= radius
                            || (Math.abs(i) + Math.abs(j) + Math.abs(k)) <= radius) {
                        positions.add(new BlockPos(center.add(i, j, k)));
                    }
                }
            }
        }
        return positions;
    }

    public static double getDistance(Shape shape, double xDistance, double yDistance, double zDistance){
        return switch (shape){
            case SPHERE, EUCLIDEAN -> Math.sqrt(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
            case STAR, MANHATTAN -> xDistance + yDistance + zDistance;
            case CUBE, CHEBYSHEV -> Math.max(Math.max(xDistance, yDistance), zDistance);
        };
    }
}