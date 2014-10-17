package jp.yuruga.common;

import android.location.Location;

/**
 * Created by maeda on 2014/10/16.
 */
public class Utils {
    public static float calcDistanceBetweenLocations(double[] locationa, double[] locationb)
    {
        Location locA = new Location("a");
        locA.setLatitude(locationa[0]);
        locA.setLongitude(locationa[1]);
        Location locB = new Location("b");
        locB.setLatitude(locationb[0]);
        locB.setLongitude(locationb[1]);
        return locA.distanceTo(locB);
    }
}
