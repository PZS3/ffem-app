package org.akvo.caddisfly.preference;

import android.content.Context;

import org.akvo.caddisfly.R;
import org.akvo.caddisfly.sensor.colorimetry.liquid.ColorimetryLiquidConfig;
import org.akvo.caddisfly.util.ColorUtil;
import org.akvo.caddisfly.util.PreferencesUtil;

/**
 * Static functions to get or set values of various preferences
 */
public class AppPreferences {

    public static boolean isDiagnosticMode(Context context) {
        return PreferencesUtil.getBoolean(context, R.string.diagnosticModeKey, false);
    }

    public static void enableDiagnosticMode(Context context) {
        PreferencesUtil.setBoolean(context, R.string.diagnosticModeKey, true);
    }

    public static void disableDiagnosticMode(Context context) {
        PreferencesUtil.setBoolean(context, R.string.diagnosticModeKey, false);
    }

    public static int getSamplingTimes(Context context) {
        int samplingTimes;
        if (isDiagnosticMode(context)) {
            samplingTimes = Integer.parseInt(PreferencesUtil.getString(context,
                    R.string.samplingsTimeKey, String.valueOf(ColorimetryLiquidConfig.SAMPLING_COUNT_DEFAULT)));
        } else {
            samplingTimes = ColorimetryLiquidConfig.SAMPLING_COUNT_DEFAULT;
        }
        //Add 1 because first sample is always discarded during analysis
        return samplingTimes + 1;
    }

    public static int getColorDistanceTolerance(Context context) {
        if (isDiagnosticMode(context)) {
            return Integer.parseInt(PreferencesUtil.getString(context,
                    R.string.colorDistanceToleranceKey, String.valueOf(ColorUtil.MAX_COLOR_DISTANCE_RGB)));
        } else {
            return ColorUtil.MAX_COLOR_DISTANCE_RGB;
        }
    }

    public static boolean isSoundOff(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.noSoundKey, false);
    }

    public static boolean getAutoFocus(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.autoFocusKey, false);
    }

    public static boolean getUseFlashMode(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.useFlashModeKey, false);
    }

    public static boolean getIgnoreShake(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.ignoreShakeKey, false);
    }

    public static boolean getShowDebugMessages(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.showDebugMessagesKey, false);
    }

    public static boolean getUseCamera2Api(Context context) {
        return isDiagnosticMode(context) &&
                PreferencesUtil.getBoolean(context, R.string.useCamera2Key, false);
    }

}