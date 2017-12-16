/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.util;

import android.graphics.Color;

import org.akvo.caddisfly.helper.SwatchHelper;
import org.akvo.caddisfly.model.ResultDetail;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import timber.log.Timber;

import static junit.framework.Assert.assertEquals;

@SuppressWarnings("unused")
public class SwatchHelperTest {

    @Before
    public void setUp() {
        try {
            ClassUtil.assertUtilityClassWellDefined(SwatchHelper.class);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            Timber.e(e);
        }
    }

    private ResultDetail createNewResult(double value) {
        return createNewResult(value, 0);
    }

    private ResultDetail createNewResult(double value, int color) {
        return new ResultDetail(value, color);
    }

    @Test
    public void testGetAverageResult() {

        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.5));
        results.add(createNewResult(0.0));
        results.add(createNewResult(1.7));
        results.add(createNewResult(0.0));
        results.add(createNewResult(1.8));
        results.add(createNewResult(0.0));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.16, result, 0);
    }

    @Test
    public void testAverage2() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.61, result, 0);
    }

    @Test
    public void testAverage3() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.5));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.63, result, 0);
    }

    @Test
    public void testAverage4() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.6, result, 0);
    }

    @Test
    public void testAverage5() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.7));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.62, result, 0);
    }

    public void testAverage6() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.7));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(-1, result, 0);
    }

    @Test
    public void testAverage7() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.63, result, 0);
    }

    @Test
    public void testAverage8() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6));
        results.add(createNewResult(1.8));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.7));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.5));
        results.add(createNewResult(1.6));

        double result = SwatchHelper.getAverageResult(results);
        assertEquals(1.63, result, 0);
    }

    @Test
    public void testGetAverageColor1() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6, Color.rgb(255, 255, 255)));
        results.add(createNewResult(1.8, Color.rgb(250, 250, 250)));
        results.add(createNewResult(1.7, Color.rgb(245, 245, 245)));
        results.add(createNewResult(1.7, Color.rgb(240, 240, 240)));
        results.add(createNewResult(1.5, Color.rgb(235, 235, 235)));
        results.add(createNewResult(1.5, Color.rgb(235, 235, 235)));
        results.add(createNewResult(1.6, Color.rgb(230, 230, 230)));

        int color = SwatchHelper.getAverageColor(results);
        assertEquals(0, color);
    }

    @Test
    public void testGetAverageColor2() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6, Color.rgb(255, 255, 255)));
        results.add(createNewResult(1.8, Color.rgb(250, 250, 250)));
        results.add(createNewResult(1.7, Color.rgb(245, 245, 245)));
        results.add(createNewResult(1.7, Color.rgb(240, 240, 240)));

        int color = SwatchHelper.getAverageColor(results);
        assertEquals(0, color);
    }

    @Test
    public void testGetAverageColor3() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6, Color.rgb(255, 255, 255)));
        results.add(createNewResult(1.8, Color.rgb(250, 250, 250)));
        results.add(createNewResult(1.7, Color.rgb(245, 245, 245)));
        results.add(createNewResult(1.7, Color.rgb(240, 240, 240)));
        results.add(createNewResult(1.5, Color.rgb(235, 235, 235)));
        results.add(createNewResult(1.5, Color.rgb(210, 230, 210)));

        int color = SwatchHelper.getAverageColor(results);
        assertEquals(0, color);
    }

    @Test
    public void testGetAverageColor4() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.8, Color.rgb(250, 254, 250)));
        results.add(createNewResult(1.8, Color.rgb(251, 253, 250)));
        results.add(createNewResult(1.8, Color.rgb(252, 252, 250)));
        results.add(createNewResult(1.8, Color.rgb(253, 251, 250)));
        results.add(createNewResult(1.8, Color.rgb(254, 250, 250)));

        int color = SwatchHelper.getAverageColor(results);

        assertEquals(Color.rgb(252, 252, 250), color);
    }

    @Test
    public void testGetAverageColor5() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6, Color.rgb(225, 1, 1)));
        results.add(createNewResult(1.8, Color.rgb(225, 1, 2)));
        results.add(createNewResult(1.8, Color.rgb(215, 1, 1)));
        results.add(createNewResult(1.8, Color.rgb(225, 1, 1)));
        results.add(createNewResult(1.8, Color.rgb(225, 1, 1)));
        results.add(createNewResult(1.8, Color.rgb(225, 1, 3)));

        int color = SwatchHelper.getAverageColor(results);
        assertEquals(0, color);
    }

    @Test
    public void testGetAverageColor6() {
        ArrayList<ResultDetail> results = new ArrayList<>();
        results.add(createNewResult(1.6, Color.rgb(179, 128, 81)));
        results.add(createNewResult(1.8, Color.rgb(176, 126, 77)));
        results.add(createNewResult(1.8, Color.rgb(177, 125, 77)));
        results.add(createNewResult(1.8, Color.rgb(177, 125, 77)));
        results.add(createNewResult(1.8, Color.rgb(175, 125, 76)));
        results.add(createNewResult(1.8, Color.rgb(175, 124, 77)));

        int color = SwatchHelper.getAverageColor(results);
        assertEquals(Color.rgb(176, 125, 77), color);

    }
}
