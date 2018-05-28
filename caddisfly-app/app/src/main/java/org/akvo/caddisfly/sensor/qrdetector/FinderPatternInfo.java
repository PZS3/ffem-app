/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akvo.caddisfly.sensor.qrdetector;

/**
 * <p>Encapsulates information about finder patterns in an image, including the location of
 * the three finder patterns, and their estimated module size.</p>
 *
 * @author Sean Owen
 */
public final class FinderPatternInfo {

    private final FinderPattern bottomLeft;
    private final FinderPattern topLeft;
    private final FinderPattern topRight;
    private final FinderPattern bottomRight;

    public FinderPatternInfo(FinderPattern[] patternCenters) {
        if (Math.abs(patternCenters[0].getX() - patternCenters[1].getX()) < 5 ||
                Math.abs(patternCenters[0].getX() - patternCenters[2].getX()) < 5 ||
                Math.abs(patternCenters[1].getX() - patternCenters[2].getX()) < 5) {
            this.bottomLeft = patternCenters[0];
            this.topLeft = patternCenters[1];
            this.topRight = patternCenters[2];
            this.bottomRight = patternCenters[3];

        } else {
            topLeft = new FinderPattern(0, 0, 0);
            bottomRight = new FinderPattern(0, 0, 0);
            topRight = new FinderPattern(0, 0, 0);
            bottomLeft = new FinderPattern(0, 0, 0);
        }

    }

    public FinderPattern getBottomLeft() {
        return bottomLeft;
    }

    public FinderPattern getTopLeft() {
        return topLeft;
    }

    public FinderPattern getTopRight() {
        return topRight;
    }

    public FinderPattern getBottomRight() {
        return bottomRight;
    }
}
