package org.jcb.tools;

import java.util.Calendar;

public class MoonPhase {
    /**
     * Calculate the phase of the moon for a given date.
     *
     * <p>Code heavily influenced by hacklib.c in <a
     * href="http://www.nethack.org/">Nethack</a></p>
     *
     * <p>The Algorithm:
     *
     * <pre>
     * moon period = 29.53058 days ~= 30, year = 365.2422 days
     *
     * days moon phase advances on first day of year compared to preceding year
     *  = 365.2422 - 12*29.53058 ~= 11
     *
     * years in Metonic cycle (time until same phases fall on the same days of
     *  the month) = 18.6 ~= 19
     *
     * moon phase on first day of year (epact) ~= (11*(year%19) + 18) % 30
     *  (18 as initial condition for 1900)
     *
     * current phase in days = first day phase + days elapsed in year
     *
     * 6 moons ~= 177 days
     * 177 ~= 8 reported phases * 22
     * + 11/22 for rounding
     * </pre>
     *
     * @param cal the calander.
     *
     * @return The phase of the moon as a number between 0 and 7 with
     *         0 meaning new moon and 4 meaning full moon.
     *
     * @since 1.2, Ant 1.5
     */
    public static int getPhaseOfMoon(Calendar cal) {
        int dayOfTheYear = cal.get(Calendar.DAY_OF_YEAR);
        int yearInMetonicCycle = ((cal.get(Calendar.YEAR) - 1900) % 19) + 1;
        int epact = (11 * yearInMetonicCycle + 18) % 30;
        if ((epact == 25 && yearInMetonicCycle > 11) || epact == 24) {
            epact++;
        }
        return (((((dayOfTheYear + epact) * 6) + 11) % 177) / 22) & 7;
    }
}
