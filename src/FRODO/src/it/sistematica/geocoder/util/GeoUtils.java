//package octo.jspool.jobs.aos;
package it.sistematica.geocoder.util;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author FLoreti
 * @version 1.0
 */
public class GeoUtils
{
    private static final double M_SM_A             =   6378137.0;
    private static final double M_SM_B             =   6356752.314;
    private static final double M_SM_ECCSQUARED    =   6.69437999013e-03;
    private static final double M_UTMSCALEFACTOR   =   0.9996;
    private static final double M_EP2;
    private static final double M_N;
    private static final double M_ALPHA;
    private static final double M_BETA;
    private static final double M_GAMMA;
    private static final double M_DELTA;
    private static final double M_EPSILON;
    private static final double M_BETA_2;
    private static final double M_GAMMA_2;
    private static final double M_DELTA_2;
    private static final double M_EPSILON_2;

    static
    {
        M_EP2              =   (Math.pow (M_SM_A, 2.0) - Math.pow (M_SM_B, 2.0)) / Math.pow (M_SM_B, 2.0);
        M_N                =   (M_SM_A - M_SM_B) / (M_SM_A + M_SM_B);
        M_ALPHA            =   ((M_SM_A + M_SM_B) / 2.0) * (1.0 + (Math.pow(M_N, 2.0) / 4.0) + (Math.pow(M_N, 4.0) / 64.0));
        M_BETA             =   (-3.0 * M_N / 2.0) + (9.0 * Math.pow(M_N, 3.0) / 16.0) + (-3.0 * Math.pow(M_N, 5.0) / 32.0);
        M_GAMMA            =   (15.0 * Math.pow(M_N, 2.0) / 16.0) + (-15.0 * Math.pow(M_N, 4.0) / 32.0);
        M_DELTA            =   (-35.0 * Math.pow(M_N, 3.0) / 48.0) + (105.0 * Math.pow(M_N, 5.0) / 256.0);
        M_EPSILON          =   (315.0 * Math.pow(M_N, 4.0) / 512.0);
        M_BETA_2           =   (3.0 * M_N / 2.0) + (-27.0 * Math.pow (M_N, 3.0) / 32.0) + (269.0 * Math.pow (M_N, 5.0) / 512.0);
        M_GAMMA_2          =   (21.0 * Math.pow (M_N, 2.0) / 16.0) + (-55.0 * Math.pow (M_N, 4.0) / 32.0);
        M_DELTA_2          =   (151.0 * Math.pow (M_N, 3.0) / 96.0) + (-417.0 * Math.pow (M_N, 5.0) / 128.0);
        M_EPSILON_2        =   (1097.0 * Math.pow (M_N, 4.0) / 512.0);
    }

    private GeoUtils() {
    }


public static int getUTMZoneNumberFromLatLon(double lat, double lon)
{
    //Make sure the longitude is between -180.00 .. 179.9 :
    double lonTemp = (lon + 180) - (int)((lon + 180) / 360) * 360 - 180; // -180.00 .. 179.9;

    int zoneNumber  =  (int)((lonTemp + 180) / 6) + 1;// + 1;

    if( lat >= 56.0 && lat < 64.0 && lonTemp >= 3.0 && lonTemp < 12.0 )
    {
        zoneNumber = 32;
    }

    if( lat >= 72.0 && lat < 84.0 ) // Special zones for Svalbard
    {
        if(lonTemp >= 0.0  && lonTemp <  9.0 )
        {
            zoneNumber = 31;
        }
        else
        if( lonTemp >= 9.0  && lonTemp < 21.0 )
        {
            zoneNumber = 33;
        }
        else
        if( lonTemp >= 21.0 && lonTemp < 33.0 )
        {
            zoneNumber = 35;
        }
        else
        if( lonTemp >= 33.0 && lonTemp < 42.0)
        {
            zoneNumber = 37;
        }
    }

    return zoneNumber;
}

public static double [] wgsToUTM(double lat, double lon, int zoneNumber)
{
    double latRad = Math.toRadians(lat);
    double lonRad = Math.toRadians(lon);

    double nu2  = M_EP2 * Math.pow (Math.cos (latRad), 2.0);
    double N    = Math.pow (M_SM_A, 2.0) / (M_SM_B * Math.sqrt (1 + nu2));
    double t    = Math.tan (latRad);
    double t2   = t * t;
    double t4   = t2 * t2;
    double t6   = t4 * t2;

    double cMeridian = Math.toRadians( -183.0 + (zoneNumber * 6.0) );
    double elle      = lonRad - cMeridian;

    /* Precalculate coefficients for l**n in the equations below so a normal human being can read the expressions for easting
       and northing -- l**1 and l**2 have coefficients of 1.0 */
    double l3coef = 1.0 - t2 + nu2;
    double l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);
    double l5coef = 5.0 - 18.0   * t2 + t4 + 14.0  * nu2 - 58.0  * t2 * nu2;
    double l6coef = 61.0 - 58.0  * t2 + t4 + 270.0 * nu2 - 330.0 * t2 * nu2;
    double l7coef = 61.0 - 479.0 * t2 + 179.0 * t4 - t6;
    double l8coef = 1385.0 - 3111.0 * t2 + 543.0 * t4 - t6;

    double cosLatRad = Math.cos(latRad);

    double [] eastingAndNorthing = new double[2];

    eastingAndNorthing[0] = N * Math.cos(latRad) * elle
                          + (N / 6.0    * Math.pow( cosLatRad, 3.0) * l3coef * Math.pow(elle, 3.0) )
                          + (N / 120.0  * Math.pow( cosLatRad, 5.0) * l5coef * Math.pow(elle, 5.0) )
                          + (N / 5040.0 * Math.pow( cosLatRad, 7.0) * l7coef * Math.pow(elle, 7.0) );

    eastingAndNorthing[1] = arcLengthOfMeridian(latRad)
                          + ( t / 2.0     * N * Math.pow( cosLatRad, 2.0 ) * Math.pow(elle, 2.0)          )
                          + ( t / 24.0    * N * Math.pow( cosLatRad, 4.0 ) * l4coef * Math.pow(elle, 4.0) )
                          + ( t / 720.0   * N * Math.pow( cosLatRad, 6.0 ) * l6coef * Math.pow(elle, 6.0) )
                          + ( t / 40320.0 * N * Math.pow( cosLatRad, 8.0 ) * l8coef * Math.pow(elle, 8.0) );

    eastingAndNorthing[0] = eastingAndNorthing[0] * M_UTMSCALEFACTOR + 500000.0;

    eastingAndNorthing[1] = eastingAndNorthing[1] * M_UTMSCALEFACTOR;

    if (eastingAndNorthing[1] < 0.0)
        eastingAndNorthing[1]  = eastingAndNorthing[1] + 10000000.0;

    return eastingAndNorthing;
}



private static double arcLengthOfMeridian(double phi)
{
    return  M_ALPHA * ( phi + ( M_BETA    * Math.sin(2.0 * phi) )
                            + ( M_GAMMA   * Math.sin(4.0 * phi) )
                            + ( M_DELTA   * Math.sin(6.0 * phi) )
                            + ( M_EPSILON * Math.sin(8.0 * phi) )  );
}


public static double [] utmToWgs84(double easting, double northing, int zoneNumber)
{
    easting  -=  500000.0;
    easting  /=  M_UTMSCALEFACTOR;

    /* If in southern hemisphere, adjust y accordingly. */
    if (northing < 0)
    northing -= 10000000.0;

    northing /=  M_UTMSCALEFACTOR;

    double cMeridian = Math.toRadians( -183.0 + (zoneNumber * 6.0) );

    double phif = footpointLatitude(northing);

    double cf   = Math.cos (phif);
    double nuf2 = M_EP2 * Math.pow (cf, 2.0);

    double Nf    = Math.pow (M_SM_A, 2.0) / (M_SM_B * Math.sqrt (1 + nuf2));

    /* Precalculate tf */
    double tf = Math.tan (phif);
    double tf2 = tf * tf;
    double tf4 = tf2 * tf2;

    /* Precalculate fractional coefficients for x**n in the equations below to simplify the expressions for latitude and longitude. */
    double Nfpow = Nf;

    double x1frac = 1.0 / (Nfpow * cf);

    Nfpow *= Nf;   /* now equals Nf**2) */
    double x2frac = tf / (2.0 * Nfpow);

    Nfpow *= Nf;   /* now equals Nf**3) */
    double x3frac = 1.0 / (6.0 * Nfpow * cf);

    Nfpow *= Nf;   /* now equals Nf**4) */
    double x4frac = tf / (24.0 * Nfpow);

    Nfpow *= Nf;   /* now equals Nf**5) */
    double x5frac = 1.0 / (120.0 * Nfpow * cf);

    Nfpow *= Nf;   /* now equals Nf**6) */
    double x6frac = tf / (720.0 * Nfpow);

    Nfpow *= Nf;   /* now equals Nf**7) */
    double x7frac = 1.0 / (5040.0 * Nfpow * cf);

    Nfpow *= Nf;   /* now equals Nf**8) */
    double x8frac = tf / (40320.0 * Nfpow);

    /* Precalculate polynomial coefficients for x**n.
       -- x**1 does not have a polynomial coefficient. */
    double x2poly = -1.0 - nuf2;
    double x3poly = -1.0 - 2 * tf2 - nuf2;
    double x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2 - 3.0 * (nuf2 *nuf2) - 9.0 * tf2 * (nuf2 * nuf2);
    double x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;
    double x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2 + 162.0 * tf2 * nuf2;
    double x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);
    double x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);


    /* Calculate latitude */
    double latitude = phif
                    + x2frac * x2poly * (easting * easting)
                    + x4frac * x4poly * Math.pow(easting, 4.0)
                    + x6frac * x6poly * Math.pow(easting, 6.0)
                    + x8frac * x8poly * Math.pow(easting, 8.0);

    /* Calculate longitude */
    double longitude = cMeridian
                     + x1frac * easting
                     + x3frac * x3poly * Math.pow(easting, 3.0)
                     + x5frac * x5poly * Math.pow(easting, 5.0)
                     + x7frac * x7poly * Math.pow(easting, 7.0);


    return new double [] {Math.toDegrees(latitude), Math.toDegrees(longitude) };
}


private static double footpointLatitude(double y)
{
    double y_ = y / M_ALPHA;

    return   y_
           + ( M_BETA_2    * Math.sin(2.0 * y_) )
           + ( M_GAMMA_2   * Math.sin(4.0 * y_) )
           + ( M_DELTA_2   * Math.sin(6.0 * y_) )
           + ( M_EPSILON_2 * Math.sin(8.0 * y_) );
}

public static long getDistance(long lat0, long lon0, long lat2, long lon2)
{
    int utmZone = GeoUtils.getUTMZoneNumberFromLatLon((double)lat0 / 1000000.0d, (double)lon0 / 1000000.0d);

    double [] utm0 = GeoUtils.wgsToUTM((double)lat0 / 1000000.0d, (double)lon0 / 1000000.0d, utmZone);
    double [] utm2 = GeoUtils.wgsToUTM((double)lat2 / 1000000.0d, (double)lon2 / 1000000.0d, utmZone); //translating to the same UTM zone number

    double x2 = (utm0[0] - utm2[0]) * (utm0[0] - utm2[0]);
    double y2 = (utm0[1] - utm2[1]) * (utm0[1] - utm2[1]);

    return (long)Math.sqrt(x2 + y2);
}

}
