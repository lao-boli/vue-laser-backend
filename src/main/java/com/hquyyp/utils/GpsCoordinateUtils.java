package com.hquyyp.utils;

import java.util.ArrayList;
import java.util.List;

public class GpsCoordinateUtils {
    private static final double PI = 3.141592653589793D;
    private static final double A = 6378245.0D;
    private static final double EE = 0.006693421622965943D;

    public static double[] calWGS84toGCJ02(double latitude, double longitude) {
        Point dev = calDev(latitude, longitude);
        double retLat = latitude + dev.getX();
        double retLon = longitude + dev.getY();
        return new double[]{retLat, retLon};
    }


    public static double[] calWGS84toBD09(double latitude, double longitude) {
        Point dev = calDev(latitude, longitude);
        double retLat = latitude + dev.getX();
        double retLon = longitude + dev.getY();
        return calGCJ02toBD09(retLat, retLon);
    }


    public static double[] calGCJ02toWGS84(double latitude, double longitude) {
        Point dev = calDev(latitude, longitude);
        double retLat = latitude - dev.getX();
        double retLon = longitude - dev.getY();
        dev = calDev(retLat, retLon);
        retLat = latitude - dev.getX();
        retLon = longitude - dev.getY();
        return new double[]{retLat, retLon};
    }


    public static double[] calBD09toWGS84(double latitude, double longitude) {
        double[] gcj = calBD09toGCJ02(latitude, longitude);
        return calGCJ02toWGS84(gcj[0], gcj[1]);
    }

    private static Point calDev(double latitude, double longitude) {
        if (isOutOfChina(latitude, longitude, false)) {
            return new Point(latitude, latitude);
        }
        double dLat = calLat(longitude - 105.0D, latitude - 35.0D);
        double dLon = calLon(longitude - 105.0D, latitude - 35.0D);
        double radLat = latitude / 180.0D * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1.0D - 0.006693421622965943D * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = dLat * 180.0D / 6335552.717000426D / magic * sqrtMagic * Math.PI;
        dLon = dLon * 180.0D / 6378245.0D / sqrtMagic * Math.cos(radLat) * Math.PI;
        return new Point(dLat, dLon);
    }


    private static double calLat(double x, double y) {
        double ret = -100.0D + 2.0D * x + 3.0D * y + 0.2D * y * y + 0.1D * x * y + 0.2D * Math.sqrt(Math.abs(x));
        ret += (20.0D * Math.sin(6.0D * x * Math.PI) + 20.0D * Math.sin(2.0D * x * Math.PI)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(y * Math.PI) + 40.0D * Math.sin(y / 3.0D * Math.PI)) * 2.0D / 3.0D;
        ret += (160.0D * Math.sin(y / 12.0D * Math.PI) + 320.0D * Math.sin(y * Math.PI / 30.0D)) * 2.0D / 3.0D;
        return ret;
    }


    private static double calLon(double x, double y) {
        double ret = 300.0D + x + 2.0D * y + 0.1D * x * x + 0.1D * x * y + 0.1D * Math.sqrt(Math.abs(x));
        ret += (20.0D * Math.sin(6.0D * x * Math.PI) + 20.0D * Math.sin(2.0D * x * Math.PI)) * 2.0D / 3.0D;
        ret += (20.0D * Math.sin(x * Math.PI) + 40.0D * Math.sin(x / 3.0D * Math.PI)) * 2.0D / 3.0D;
        ret += (150.0D * Math.sin(x / 12.0D * Math.PI) + 300.0D * Math.sin(x / 30.0D * Math.PI)) * 2.0D / 3.0D;
        return ret;
    }


    public static double[] calGCJ02toBD09(double latitude, double longitude) {
        double x = longitude, y = latitude;
        double z = Math.sqrt(x * x + y * y) + 2.0E-5D * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 3.0E-6D * Math.cos(x * Math.PI);
        double retLat = z * Math.sin(theta) + 0.006D;
        double retLon = z * Math.cos(theta) + 0.0065D;
        return new double[]{retLat, retLon};
    }


    public static double[] calBD09toGCJ02(double latitude, double longitude) {
        double x = longitude - 0.0065D, y = latitude - 0.006D;
        double z = Math.sqrt(x * x + y * y) - 2.0E-5D * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 3.0E-6D * Math.cos(x * Math.PI);
        double retLat = z * Math.sin(theta);
        double retLon = z * Math.cos(theta);
        return new double[]{retLat, retLon};
    }


    public static boolean isOutOfChina(double latitude, double longitude, boolean precision) {
        if (precision) {
            return CHINA_POLYGON.stream().noneMatch(point -> pointInPolygon(point, latitude, longitude));
        }
        if (longitude < 72.004D || longitude > 137.8347D) {
            return true;
        }
        if (latitude < 0.8293D || latitude > 55.8271D) {
            return true;
        }
        return false;
    }


    private static boolean pointInPolygon(Point[] polygon, double latitude, double longitude) {
        int j = polygon.length - 1;
        boolean oddNodes = false;
        for (int i = 0; i < polygon.length; i++) {
            if (((polygon[i].getX() < latitude &&
                    polygon[j].getX() >= latitude) ||
                    (polygon[j].getX() < latitude &&
                    polygon[i].getX() >= latitude)) &&
                    (polygon[i].getY() <= longitude ||
                    polygon[j].getY() <= longitude) &&
                    polygon[i].getY() + (latitude - polygon[i].getX()) / (polygon[j].getX() - polygon[i].getX()) * (polygon[j].getX() - polygon[i].getY()) < longitude) {
                oddNodes = !oddNodes;
            }
            j = i;
        }
        return oddNodes;
    }


      private static final Point[] MAINLAND = new Point[]{new Point(27.32083D, 88.91693D), new Point(27.54243D, 88.76464D), new Point(28.00805D, 88.83575D), new Point(28.1168D, 88.62435D), new Point(27.86605D, 88.14279D), new Point(27.82305D, 87.19275D), new Point(28.11166D, 86.69527D), new Point(27.90888D, 86.45137D), new Point(28.15805D, 86.19769D), new Point(27.88625D, 86.0054D), new Point(28.27916D, 85.72137D), new Point(28.30666D, 85.11095D), new Point(28.59104D, 85.19518D), new Point(28.54444D, 84.84665D), new Point(28.73402D, 84.48623D), new Point(29.26097D, 84.11651D), new Point(29.18902D, 83.5479D), new Point(29.63166D, 83.19109D), new Point(30.06923D, 82.17525D), new Point(30.33444D, 82.11123D), new Point(30.385D, 81.42623D), new Point(30.01194D, 81.23221D), new Point(30.20435D, 81.02536D), new Point(30.57552D, 80.207D), new Point(30.73374D, 80.25423D), new Point(30.96583D, 79.86304D), new Point(30.95708D, 79.55429D), new Point(31.43729D, 79.08082D), new Point(31.30895D, 78.76825D), new Point(31.96847D, 78.77075D), new Point(32.24304D, 78.47594D), new Point(32.5561D, 78.40595D), new Point(32.63902D, 78.74623D), new Point(32.35083D, 78.9711D), new Point(32.75666D, 79.52874D), new Point(33.09944D, 79.37511D), new Point(33.42863D, 78.93623D), new Point(33.52041D, 78.81387D), new Point(34.06833D, 78.73581D), new Point(34.35001D, 78.98535D), new Point(34.6118D, 78.33707D), new Point(35.28069D, 78.02305D), new Point(35.49902D, 78.0718D), new Point(35.50133D, 77.82393D), new Point(35.6125D, 76.89526D), new Point(35.90665D, 76.55304D), new Point(35.81458D, 76.18061D), new Point(36.07082D, 75.92887D), new Point(36.23751D, 76.04166D), new Point(36.66343D, 75.85984D), new Point(36.73169D, 75.45179D), new Point(36.91156D, 75.39902D), new Point(36.99719D, 75.14787D), new Point(37.02782D, 74.56543D), new Point(37.17D, 74.39089D), new Point(37.23733D, 74.91574D), new Point(37.40659D, 75.18748D), new Point(37.65243D, 74.9036D), new Point(38.47256D, 74.85442D), new Point(38.67438D, 74.35471D), new Point(38.61271D, 73.81401D), new Point(38.88653D, 73.70818D), new Point(38.97256D, 73.85235D), new Point(39.23569D, 73.62005D), new Point(39.45483D, 73.65569D), new Point(39.59965D, 73.95471D), new Point(39.76896D, 73.8429D), new Point(40.04202D, 73.99096D), new Point(40.32792D, 74.88089D), new Point(40.51723D, 74.8588D), new Point(40.45042D, 75.23394D), new Point(40.64452D, 75.58284D), new Point(40.298D, 75.70374D), new Point(40.35324D, 76.3344D), new Point(41.01258D, 76.87067D), new Point(41.04079D, 78.08083D), new Point(41.39286D, 78.39554D), new Point(42.03954D, 80.24513D), new Point(42.19622D, 80.23402D), new Point(42.63245D, 80.15804D), new Point(42.81565D, 80.25796D), new Point(42.88545D, 80.57226D), new Point(43.02906D, 80.38405D), new Point(43.1683D, 80.81526D), new Point(44.11378D, 80.36887D), new Point(44.6358D, 80.38499D), new Point(44.73408D, 80.51589D), new Point(44.90282D, 79.87106D), new Point(45.3497D, 81.67928D), new Point(45.15748D, 81.94803D), new Point(45.13303D, 82.56638D), new Point(45.43581D, 82.64624D), new Point(45.5831D, 82.32179D), new Point(47.20061D, 83.03443D), new Point(46.97332D, 83.93026D), new Point(46.99361D, 84.67804D), new Point(46.8277D, 84.80318D), new Point(47.0591D, 85.52257D), new Point(47.26221D, 85.70139D), new Point(47.93721D, 85.53707D), new Point(48.39333D, 85.76596D), new Point(48.54277D, 86.59791D), new Point(49.1102D, 86.87602D), new Point(49.09262D, 87.34821D), new Point(49.17295D, 87.8407D), new Point(48.98304D, 87.89291D), new Point(48.88103D, 87.7611D), new Point(48.73499D, 88.05942D), new Point(48.56541D, 87.99194D), new Point(48.40582D, 88.51679D), new Point(48.21193D, 88.61179D), new Point(47.99374D, 89.08514D), new Point(47.88791D, 90.07096D), new Point(46.95221D, 90.9136D), new Point(46.57735D, 91.07027D), new Point(46.29694D, 90.92151D), new Point(46.01735D, 91.02651D), new Point(45.57972D, 90.68193D), new Point(45.25305D, 90.89694D), new Point(45.07729D, 91.56088D), new Point(44.95721D, 93.5547D), new Point(44.35499D, 94.71735D), new Point(44.29416D, 95.41061D), new Point(44.01937D, 95.34109D), new Point(43.99311D, 95.53339D), new Point(43.28388D, 95.87901D), new Point(42.73499D, 96.38206D), new Point(42.79583D, 97.1654D), new Point(42.57194D, 99.51012D), new Point(42.67707D, 100.8425D), new Point(42.50972D, 101.8147D), new Point(42.23333D, 102.0772D), new Point(41.88721D, 103.4164D), new Point(41.87721D, 104.5267D), new Point(41.67068D, 104.5237D), new Point(41.58666D, 105.0065D), new Point(42.46624D, 107.4758D), new Point(42.42999D, 109.3107D), new Point(42.64576D, 110.1064D), new Point(43.31694D, 110.9897D), new Point(43.69221D, 111.9583D), new Point(44.37527D, 111.4214D), new Point(45.04944D, 111.873D), new Point(45.08055D, 112.4272D), new Point(44.8461D, 112.853D), new Point(44.74527D, 113.638D), new Point(45.38943D, 114.5453D), new Point(45.4586D, 115.7019D), new Point(45.72193D, 116.2104D), new Point(46.29583D, 116.5855D), new Point(46.41888D, 117.3755D), new Point(46.57069D, 117.425D), new Point(46.53645D, 117.8455D), new Point(46.73638D, 118.3147D), new Point(46.59895D, 119.7068D), new Point(46.71513D, 119.9315D), new Point(46.90221D, 119.9225D), new Point(47.66499D, 119.125D), new Point(47.99475D, 118.5393D), new Point(48.01125D, 117.8046D), new Point(47.65741D, 117.3827D), new Point(47.88805D, 116.8747D), new Point(47.87819D, 116.2624D), new Point(47.69186D, 115.9231D), new Point(47.91749D, 115.5944D), new Point(48.14353D, 115.5491D), new Point(48.25249D, 115.8358D), new Point(48.52055D, 115.8111D), new Point(49.83047D, 116.7114D), new Point(49.52058D, 117.8747D), new Point(49.92263D, 118.5746D), new Point(50.09631D, 119.321D), new Point(50.33028D, 119.36D), new Point(50.39027D, 119.1386D), new Point(51.62083D, 120.0641D), new Point(52.115D, 120.7767D), new Point(52.34423D, 120.6259D), new Point(52.54267D, 120.7122D), new Point(52.58805D, 120.0819D), new Point(52.76819D, 120.0314D), new Point(53.26374D, 120.8307D), new Point(53.54361D, 123.6147D), new Point(53.18832D, 124.4933D), new Point(53.05027D, 125.62D), new Point(52.8752D, 125.6573D), new Point(52.75722D, 126.0968D), new Point(52.5761D, 125.9943D), new Point(52.12694D, 126.555D), new Point(51.99437D, 126.4412D), new Point(51.38138D, 126.9139D), new Point(51.26555D, 126.8176D), new Point(51.31923D, 126.9689D), new Point(51.05825D, 126.9331D), new Point(50.74138D, 127.2919D), new Point(50.31472D, 127.334D), new Point(50.20856D, 127.5861D), new Point(49.80588D, 127.515D), new Point(49.58665D, 127.838D), new Point(49.58443D, 128.7119D), new Point(49.34676D, 129.1118D), new Point(49.4158D, 129.4902D), new Point(48.86464D, 130.2246D), new Point(48.86041D, 130.674D), new Point(48.60576D, 130.5236D), new Point(48.3268D, 130.824D), new Point(48.10839D, 130.6598D), new Point(47.68721D, 130.9922D), new Point(47.71027D, 132.5211D), new Point(48.09888D, 133.0827D), new Point(48.06888D, 133.4843D), new Point(48.39112D, 134.4153D), new Point(48.26713D, 134.7408D), new Point(47.99207D, 134.5576D), new Point(47.70027D, 134.7608D), new Point(47.32333D, 134.1825D), new Point(46.64017D, 133.9977D), new Point(46.47888D, 133.8472D), new Point(46.25363D, 133.9016D), new Point(45.82347D, 133.4761D), new Point(45.62458D, 133.4702D), new Point(45.45083D, 133.1491D), new Point(45.05694D, 133.0253D), new Point(45.34582D, 131.8684D), new Point(44.97388D, 131.4691D), new Point(44.83649D, 130.953D), new Point(44.05193D, 131.298D), new Point(43.53624D, 131.1912D), new Point(43.38958D, 131.3104D), new Point(42.91645D, 131.1285D), new Point(42.74485D, 130.4327D), new Point(42.42186D, 130.6044D), new Point(42.71416D, 130.2468D), new Point(42.88794D, 130.2514D), new Point(43.00457D, 129.9046D), new Point(42.43582D, 129.6955D), new Point(42.44624D, 129.3493D), new Point(42.02736D, 128.9269D), new Point(42.00124D, 128.0566D), new Point(41.58284D, 128.3002D), new Point(41.38124D, 128.1529D), new Point(41.47249D, 127.2708D), new Point(41.79222D, 126.9047D), new Point(41.61176D, 126.5661D), new Point(40.89694D, 126.0118D), new Point(40.47037D, 124.8851D), new Point(40.09362D, 124.3736D), new Point(39.82777D, 124.128D), new Point(39.8143D, 123.2422D), new Point(39.67388D, 123.2167D), new Point(38.99638D, 121.648D), new Point(38.8611D, 121.6982D), new Point(38.71909D, 121.1873D), new Point(38.91221D, 121.0887D), new Point(39.09013D, 121.6794D), new Point(39.2186D, 121.5994D), new Point(39.35166D, 121.7511D), new Point(39.52847D, 121.2283D), new Point(39.62322D, 121.533D), new Point(39.81138D, 121.4683D), new Point(40.00305D, 121.881D), new Point(40.50562D, 122.2987D), new Point(40.73874D, 122.0521D), new Point(40.92194D, 121.1775D), new Point(40.1961D, 120.4468D), new Point(39.87242D, 119.5264D), new Point(39.15693D, 118.9715D), new Point(39.04083D, 118.3273D), new Point(39.19846D, 117.889D), new Point(38.67555D, 117.5364D), new Point(38.38666D, 117.6722D), new Point(38.16721D, 118.0281D), new Point(38.1529D, 118.8378D), new Point(37.87832D, 119.0355D), new Point(37.30054D, 118.9566D), new Point(37.14361D, 119.2328D), new Point(37.15138D, 119.7672D), new Point(37.35228D, 119.8529D), new Point(37.83499D, 120.7371D), new Point(37.42458D, 121.58D), new Point(37.55256D, 122.1282D), new Point(37.41833D, 122.1814D), new Point(37.39624D, 122.5586D), new Point(37.20999D, 122.5972D), new Point(37.02583D, 122.4005D), new Point(37.01978D, 122.5392D), new Point(36.89361D, 122.5047D), new Point(36.84298D, 122.1923D), new Point(37.00027D, 121.9566D), new Point(36.75889D, 121.5944D), new Point(36.61666D, 120.7764D), new Point(36.52638D, 120.96D), new Point(36.37582D, 120.8753D), new Point(36.42277D, 120.7062D), new Point(36.14075D, 120.6956D), new Point(36.0419D, 120.3436D), new Point(36.26345D, 120.3078D), new Point(36.19998D, 120.0889D), new Point(35.95943D, 120.2378D), new Point(35.57893D, 119.6475D), new Point(34.88499D, 119.1761D), new Point(34.31145D, 120.2487D), new Point(32.97499D, 120.8858D), new Point(32.63889D, 120.8375D), new Point(32.42958D, 121.3348D), new Point(32.11333D, 121.4412D), new Point(32.02166D, 121.7066D), new Point(31.67833D, 121.8275D), new Point(31.86639D, 120.9444D), new Point(32.09361D, 120.6019D), new Point(31.94555D, 120.099D), new Point(32.30638D, 119.8267D), new Point(32.26277D, 119.6317D), new Point(31.90388D, 120.1364D), new Point(31.98833D, 120.7026D), new Point(31.81944D, 120.7196D), new Point(31.30889D, 121.6681D), new Point(30.97986D, 121.8828D), new Point(30.85305D, 121.8469D), new Point(30.56889D, 120.9915D), new Point(30.33555D, 120.8144D), new Point(30.39298D, 120.4586D), new Point(30.19694D, 120.15D), new Point(30.31027D, 120.5082D), new Point(30.06465D, 120.7916D), new Point(30.30458D, 121.2808D), new Point(29.96305D, 121.6778D), new Point(29.88211D, 122.1196D), new Point(29.51167D, 121.4483D), new Point(29.58916D, 121.9744D), new Point(29.19527D, 121.9336D), new Point(29.18388D, 121.8119D), new Point(29.37236D, 121.7969D), new Point(29.19729D, 121.7444D), new Point(29.29111D, 121.5611D), new Point(29.1634D, 121.4135D), new Point(29.02194D, 121.6914D), new Point(28.9359D, 121.4908D), new Point(28.72798D, 121.6113D), new Point(28.84215D, 121.1464D), new Point(28.66993D, 121.4844D), new Point(28.34722D, 121.6417D), new Point(28.13889D, 121.3419D), new Point(28.38277D, 121.1651D), new Point(27.98222D, 120.9353D), new Point(28.07944D, 120.5908D), new Point(27.87229D, 120.84D), new Point(27.59319D, 120.5812D), new Point(27.45083D, 120.6655D), new Point(27.20777D, 120.5075D), new Point(27.28278D, 120.1896D), new Point(27.14764D, 120.4211D), new Point(26.89805D, 120.0332D), new Point(26.64465D, 120.128D), new Point(26.51778D, 119.8603D), new Point(26.78823D, 120.0733D), new Point(26.64888D, 119.8668D), new Point(26.79611D, 119.7879D), new Point(26.75625D, 119.5503D), new Point(26.44222D, 119.8204D), new Point(26.47388D, 119.5775D), new Point(26.33861D, 119.658D), new Point(26.36777D, 119.9489D), new Point(25.99694D, 119.4253D), new Point(26.14041D, 119.0975D), new Point(25.93788D, 119.354D), new Point(25.99069D, 119.7058D), new Point(25.67996D, 119.5807D), new Point(25.68222D, 119.4522D), new Point(25.35333D, 119.6454D), new Point(25.60649D, 119.3149D), new Point(25.42097D, 119.1053D), new Point(25.25319D, 119.3526D), new Point(25.17208D, 119.2726D), new Point(25.2426D, 118.8749D), new Point(24.97194D, 118.9866D), new Point(24.88291D, 118.5729D), new Point(24.75673D, 118.7631D), new Point(24.52861D, 118.5953D), new Point(24.53638D, 118.2397D), new Point(24.68194D, 118.1688D), new Point(24.44024D, 118.0199D), new Point(24.46019D, 117.7947D), new Point(24.25875D, 118.1237D), new Point(23.62437D, 117.1957D), new Point(23.65919D, 116.9179D), new Point(23.355D, 116.7603D), new Point(23.42024D, 116.5322D), new Point(23.23666D, 116.7871D), new Point(23.21083D, 116.5139D), new Point(22.93902D, 116.4817D), new Point(22.73916D, 115.7978D), new Point(22.88416D, 115.6403D), new Point(22.65889D, 115.5367D), new Point(22.80833D, 115.1614D), new Point(22.70277D, 114.8889D), new Point(22.53305D, 114.8722D), new Point(22.64027D, 114.718D), new Point(22.81402D, 114.7782D), new Point(22.69972D, 114.5208D), new Point(22.50423D, 114.6136D), new Point(22.55004D, 114.2223D), new Point(22.42993D, 114.3885D), new Point(22.26056D, 114.2961D), new Point(22.36736D, 113.9056D), new Point(22.50874D, 114.0337D), new Point(22.47444D, 113.8608D), new Point(22.83458D, 113.606D), new Point(23.05027D, 113.5253D), new Point(23.11724D, 113.8219D), new Point(23.05083D, 113.4793D), new Point(22.87986D, 113.3629D), new Point(22.54944D, 113.5648D), new Point(22.18701D, 113.5527D), new Point(22.56701D, 113.1687D), new Point(22.17965D, 113.3868D), new Point(22.04069D, 113.2226D), new Point(22.20485D, 113.0848D), new Point(21.8693D, 112.94D), new Point(21.96472D, 112.824D), new Point(21.70139D, 112.2819D), new Point(21.91611D, 111.8921D), new Point(21.75139D, 111.9669D), new Point(21.77819D, 111.6762D), new Point(21.61264D, 111.7832D), new Point(21.5268D, 111.644D), new Point(21.52528D, 111.0285D), new Point(21.21138D, 110.5328D), new Point(21.37322D, 110.3944D), new Point(20.84381D, 110.1594D), new Point(20.84083D, 110.3755D), new Point(20.64D, 110.3239D), new Point(20.48618D, 110.5274D), new Point(20.24611D, 110.2789D), new Point(20.2336D, 109.9244D), new Point(20.4318D, 110.0069D), new Point(20.92416D, 109.6629D), new Point(21.44694D, 109.9411D), new Point(21.50569D, 109.6605D), new Point(21.72333D, 109.5733D), new Point(21.49499D, 109.5344D), new Point(21.39666D, 109.1428D), new Point(21.58305D, 109.1375D), new Point(21.61611D, 108.911D), new Point(21.79889D, 108.8702D), new Point(21.59888D, 108.7403D), new Point(21.93562D, 108.4692D), new Point(21.59014D, 108.5125D), new Point(21.68999D, 108.3336D), new Point(21.51444D, 108.2447D), new Point(21.54241D, 107.99D), new Point(21.66694D, 107.7831D), new Point(21.60526D, 107.3627D), new Point(22.03083D, 106.6933D), new Point(22.45682D, 106.5517D), new Point(22.76389D, 106.7875D), new Point(22.86694D, 106.7029D), new Point(22.91253D, 105.8771D), new Point(23.32416D, 105.3587D), new Point(23.18027D, 104.9075D), new Point(22.81805D, 104.7319D), new Point(22.6875D, 104.3747D), new Point(22.79812D, 104.1113D), new Point(22.50387D, 103.9687D), new Point(22.78287D, 103.6538D), new Point(22.58436D, 103.5224D), new Point(22.79451D, 103.3337D), new Point(22.43652D, 103.0304D), new Point(22.77187D, 102.4744D), new Point(22.39629D, 102.1407D), new Point(22.49777D, 101.7415D), new Point(22.20916D, 101.5744D), new Point(21.83444D, 101.7653D), new Point(21.14451D, 101.786D), new Point(21.17687D, 101.2919D), new Point(21.57264D, 101.1482D), new Point(21.76903D, 101.099D), new Point(21.47694D, 100.6397D), new Point(21.43546D, 100.2057D), new Point(21.72555D, 99.97763D), new Point(22.05018D, 99.95741D), new Point(22.15592D, 99.16785D), new Point(22.93659D, 99.56484D), new Point(23.08204D, 99.5113D), new Point(23.18916D, 98.92747D), new Point(23.97076D, 98.67991D), new Point(24.16007D, 98.89073D), new Point(23.92999D, 97.54762D), new Point(24.26055D, 97.7593D), new Point(24.47666D, 97.54305D), new Point(24.73992D, 97.55255D), new Point(25.61527D, 98.19109D), new Point(25.56944D, 98.36137D), new Point(25.85597D, 98.7104D), new Point(26.12527D, 98.56944D), new Point(26.18472D, 98.73109D), new Point(26.79166D, 98.77777D), new Point(27.52972D, 98.69699D), new Point(27.6725D, 98.45888D), new Point(27.54014D, 98.31992D), new Point(28.14889D, 98.14499D), new Point(28.54652D, 97.55887D), new Point(28.22277D, 97.34888D), new Point(28.46749D, 96.65387D), new Point(28.35111D, 96.40193D), new Point(28.525D, 96.34027D), new Point(28.79569D, 96.61373D), new Point(29.05666D, 96.47083D), new Point(28.90138D, 96.17532D), new Point(29.05972D, 96.14888D), new Point(29.25757D, 96.39172D), new Point(29.46444D, 96.08315D), new Point(29.03527D, 95.38777D), new Point(29.33346D, 94.64751D), new Point(29.07348D, 94.23456D), new Point(28.6692D, 93.96172D), new Point(28.61876D, 93.35194D), new Point(28.3193D, 93.22205D), new Point(28.1419D, 92.71044D), new Point(27.86194D, 92.54498D), new Point(27.76472D, 91.65776D), new Point(27.945D, 91.66277D), new Point(28.08111D, 91.30138D), new Point(27.96999D, 91.08693D), new Point(28.07958D, 90.3765D), new Point(28.24257D, 90.38898D), new Point(28.32369D, 89.99819D), new Point(28.05777D, 89.48749D), new Point(27.32083D, 88.91693D)};

      private static final Point[] TAIWAN = new Point[]{new Point(25.13474D, 121.4441D), new Point(25.28361D, 121.5632D), new Point(25.00722D, 122.0004D), new Point(24.85028D, 121.8182D), new Point(24.47638D, 121.8397D), new Point(23.0875D, 121.3556D), new Point(21.92791D, 120.7196D), new Point(22.31277D, 120.6103D), new Point(22.54044D, 120.3071D), new Point(23.04437D, 120.0539D), new Point(23.61708D, 120.1112D), new Point(25.00166D, 121.0017D), new Point(25.13474D, 121.4441D)};

      private static final Point[] HAINAN = new Point[]{new Point(19.52888D, 110.855D), new Point(19.16761D, 110.4832D), new Point(18.80083D, 110.5255D), new Point(18.3852D, 110.0503D), new Point(18.39152D, 109.7594D), new Point(18.19777D, 109.7036D), new Point(18.50562D, 108.6871D), new Point(19.28028D, 108.6283D), new Point(19.76D, 109.2939D), new Point(19.7236D, 109.1653D), new Point(19.89972D, 109.2572D), new Point(19.82861D, 109.4658D), new Point(19.99389D, 109.6108D), new Point(20.13361D, 110.6655D), new Point(19.97861D, 110.9425D), new Point(19.63829D, 111.0215D), new Point(19.52888D, 110.855D)};

      private static final Point[] CHONGMING = new Point[]{new Point(31.80054D, 121.2039D), new Point(31.49972D, 121.8736D), new Point(31.53111D, 121.5464D), new Point(31.80054D, 121.2039D)};

      private static final List<Point[]> CHINA_POLYGON = (List) new ArrayList<>();

      static {
        CHINA_POLYGON.add(MAINLAND);
        CHINA_POLYGON.add(TAIWAN);
        CHINA_POLYGON.add(HAINAN);
        CHINA_POLYGON.add(CHONGMING);
    }
}