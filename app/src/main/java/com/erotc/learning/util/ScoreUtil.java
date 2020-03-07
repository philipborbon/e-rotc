package com.erotc.learning.util;

/**
 * Created on 11/19/2018.
 */
public class ScoreUtil {
    private static final int[][] LEVEL_SCORES = {
            {300, 280, 260, 240, 220, 200, 180, 160, 140, 120},
            {400, 380, 360, 340, 320, 300, 280, 260, 240, 220},
            {500, 480, 460, 440, 420, 400, 380, 360, 340, 320}
    };

    public static int getScore(int level, int seconds) throws Exception {
        if ( seconds < 0 ){
            throw new Exception("Seconds should be greater than or equal to zero");
        } else if ( level < 1 || level > 3 ){
            throw new Exception("Levels should be greater than or equal to 1 and less than or equal to 3");
        }

        if ( seconds > 9 ) {
            return 0;
        }

        int levelIndex = level - 1;
        int secondsIndex = seconds;

        return LEVEL_SCORES[levelIndex][secondsIndex];
    }
}
