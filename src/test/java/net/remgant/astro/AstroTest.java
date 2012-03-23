package net.remgant.astro;


import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class AstroTest {
    @Test
    public void testTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);

        double expected = -3543.0;
        double actual = net.remgant.astro.Time.getDayNumber(cal);
        assertEquals(expected, actual, 0.001);

        cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
        expected = 1.0;
        actual = net.remgant.astro.Time.getDayNumber(cal);
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void testSunRA() {
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);

        double expectedRA = 26.6497;
        double actualRA = sun.getRA(d);
        assertEquals(expectedRA, actualRA, 0.01);
    }

    @Test
    public void testSunDecl() {
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);

        double expectedDecl = 11.0061;
        double actualDecl = sun.getDecl(d);
        assertEquals(expectedDecl, actualDecl, 0.01);
    }

    @Test
    public void testSunAzm() {
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // cal.set(1990,Calendar.APRIL,19,0,0,0);
        cal.set(2003, Calendar.FEBRUARY, 28, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        // double lat = 60.0;
        // double lon = 15.0;
        double lat = 42.0867;
        double lon = -71.4750;

        // double expectedAzm = 15.6767;
        double expectedAzm = 274.1767;
        double actualAzm = sun.getAzimuth(d, 0.0, lon, lat);
        assertEquals(expectedAzm, actualAzm, 0.01);
    }

    @Test
    public void testSunrise() {
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2010, Calendar.DECEMBER, 21, 12, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        double lat = 42.3;
        double lon = -71.1;
        double expected = 12.1667;
        double actual = sun.computeRiseTime(lon, lat, d, -5.0);
        System.out.println(actual);
        assertEquals(expected, actual, 0.1);
    }

    @Test
    public void testSunAlt() {
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        double lat = 60.0;
        double lon = 15.0;

        double expectedAlt = -17.9570;
        double actualAlt = sun.getAltitude(d, 0.0, lon, lat);
        assertEquals(expectedAlt, actualAlt, 0.01);
    }

    @Test
    public void testSiriusRA() {
        Star sirius = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);

        double expectedRA = 101.2871;
        double actualRA = sirius.getRA(d);
        assertEquals(expectedRA, actualRA, 0.0001);
    }

    @Test
    public void testSiriusDecl() {
        Star sirius = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1990, Calendar.APRIL, 19, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);

        double expectedDecl = -15.2839;
        double actualDecl = sirius.getDecl(d);
        assertEquals(expectedDecl, actualDecl, 0.0001);
    }

    @Test
    public void testStarRA() {
        Star star = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        double expectedRA = 101.2871;
        double actualRA = star.getRA(0.0);
        assertEquals(expectedRA, actualRA, 0.0001);
    }

    @Test
    public void testStarDecl() {
        Star star = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        double expectedDecl = -15.2839;
        double actualDecl = star.getDecl(0.0);
        assertEquals(expectedDecl, actualDecl, 0.0001);
    }

    @Test
    public void testSiriusAzm() {
        Star sirius = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2003, Calendar.FEBRUARY, 28, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        double lat = 42.0867;
        double lon = -71.4750;

        double expectedAzm = 163.0152;
        double actualAzm = sirius.getAzimuth(d, 0.0, lon, lat);
        assertEquals(expectedAzm, actualAzm, 0.5);
    }

    @Test
    public void testSiriusAlt() {
        Star sirius = new Star("101.2871", "-15.2839", "CMa", "9", "Alp", "Sirius",
                "-1.46", "A");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2003, Calendar.FEBRUARY, 28, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        double lat = 42.0867;
        double lon = -71.4750;

        double expectedAlt = 29.4976;
        double actualAlt = sirius.getAltitude(d, 0.0, lon, lat);
        assertEquals(expectedAlt, actualAlt, 1.5);
    }

    @Test
    public void testEcliptic() {
        double expectedRA[] = {281.2755, 282.3786, 283.4803, 284.5806, 285.6793,
                286.7764, 287.8716, 288.9650, 290.0563, 291.1455,
                292.2324, 293.3169, 294.3990, 295.4785, 296.5552,
                297.6292, 298.7003, 299.7685, 300.8336, 301.8956,
                302.9544, 304.0100, 305.0624, 306.1114, 307.1571,
                308.1994, 309.2383, 310.2738, 311.3059, 312.3347,
                313.3601, 314.3821, 315.4008, 316.4161, 317.4282,
                318.4370, 319.4424, 320.4446, 321.4436, 322.4393,
                323.4317, 324.4210, 325.4070, 326.3899, 327.3696,
                328.3462, 329.3198, 330.2903, 331.2578, 332.2224,
                333.1841, 334.1429, 335.0990, 336.0524, 337.0031,
                337.9512, 338.8969, 339.8402, 340.7812, 341.7200,
                342.6566, 343.5913, 344.5240, 345.4549, 346.3841,
                347.3116, 348.2374, 349.1617, 350.0846, 351.0061,
                351.9263, 352.8452, 353.7630, 354.6797, 355.5954,
                356.5101, 357.4239, 358.3369, 359.2492, 0.1609,
                1.0720, 1.9826, 2.8928, 3.8027, 4.7123, 5.6219,
                6.5314, 7.4410, 8.3508, 9.2608, 10.1713, 11.0822,
                11.9937, 12.9058, 13.8187, 14.7323, 15.6469, 16.5623,
                17.4788, 18.3964, 19.3151, 20.2350, 21.1561, 22.0786,
                23.0024, 23.9276, 24.8543, 25.7825, 26.7123, 27.6437,
                28.5767, 29.5115, 30.4481, 31.3864, 32.3267, 33.2689,
                34.2131, 35.1594, 36.1079, 37.0585, 38.0114, 38.9666,
                39.9241, 40.8839, 41.8461, 42.8108, 43.7778, 44.7472,
                45.7191, 46.6934, 47.6701, 48.6492, 49.6308, 50.6147,
                51.6009, 52.5895, 53.5804, 54.5735, 55.5689, 56.5665,
                57.5663, 58.5683, 59.5723, 60.5785, 61.5867, 62.5970,
                63.6093, 64.6236, 65.6398, 66.6580, 67.6781, 68.6999,
                69.7235, 70.7489, 71.7758, 72.8043, 73.8342, 74.8655,
                75.8982, 76.9320, 77.9669, 79.0028, 80.0396, 81.0772,
                82.1155, 83.1543, 84.1935, 85.2331, 86.2730, 87.3129,
                88.3529, 89.3928, 90.4326, 91.4721, 92.5113, 93.5502,
                94.5886, 95.6264, 96.6636, 97.7001, 98.7359, 99.7707,
                100.8046, 101.8375, 102.8692, 103.8997, 104.9289,
                105.9567, 106.9830, 108.0077, 109.0308, 110.0521,
                111.0716, 112.0892, 113.1047, 114.1182, 115.1295,
                116.1386, 117.1454, 118.1499, 119.1520, 120.1518,
                121.1491, 122.1441, 123.1366, 124.1267, 125.1143,
                126.0995, 127.0822, 128.0624, 129.0402, 130.0156,
                130.9885, 131.9589, 132.9269, 133.8924, 134.8555,
                135.8161, 136.7743, 137.7300, 138.6833, 139.6342,
                140.5827, 141.5287, 142.4723, 143.4135, 144.3524,
                145.2889, 146.2232, 147.1552, 148.0851, 149.0129,
                149.9386, 150.8623, 151.7842, 152.7042, 153.6224,
                154.5389, 155.4538, 156.3671, 157.2790, 158.1894,
                159.0985, 160.0064, 160.9130, 161.8185, 162.7229,
                163.6263, 164.5288, 165.4304, 166.3312, 167.2311,
                168.1304, 169.0290, 169.9270, 170.8245, 171.7216,
                172.6183, 173.5147, 174.4110, 175.3072, 176.2034,
                177.0998, 177.9963, 178.8931, 179.7904, 180.6881,
                181.5864, 182.4855, 183.3853, 184.2860, 185.1877,
                186.0904, 186.9943, 187.8994, 188.8058, 189.7136,
                190.6229, 191.5337, 192.4461, 193.3602, 194.2759,
                195.1935, 196.1129, 197.0341, 197.9574, 198.8828,
                199.8103, 200.7400, 201.6721, 202.6065, 203.5435,
                204.4829, 205.4250, 206.3698, 207.3175, 208.2679,
                209.2213, 210.1778, 211.1373, 212.0999, 213.0657,
                214.0348, 215.0072, 215.9829, 216.9621, 217.9446,
                218.9305, 219.9199, 220.9127, 221.9090, 222.9087,
                223.9118, 224.9184, 225.9285, 226.9420, 227.9590,
                228.9794, 230.0034, 231.0308, 232.0616, 233.0959,
                234.1336, 235.1747, 236.2193, 237.2672, 238.3185,
                239.3731, 240.4309, 241.4920, 242.5563, 243.6238,
                244.6942, 245.7677, 246.8441, 247.9233, 249.0051,
                250.0896, 251.1765, 252.2657, 253.3572, 254.4508,
                255.5463, 256.6436, 257.7426, 258.8433, 259.9453,
                261.0487, 262.1533, 263.2590, 264.3656, 265.4731,
                266.5812, 267.6899, 268.7991, 269.9085, 271.0182,
                272.1280, 273.2376, 274.3471, 275.4563, 276.5651,
                277.6732, 278.7806, 279.8871};
        double expectedDecl[] = {-23.0340, -22.9509, -22.8602, -22.7620, -22.6562,
                -22.5429, -22.4222, -22.2941, -22.1587, -22.0161, -21.8663, -21.7094, -21.5455,
                -21.3746, -21.1968, -21.0123, -20.8211, -20.6232, -20.4189, -20.2081, -19.9910,
                -19.7677, -19.5383, -19.3029, -19.0615, -18.8144, -18.5615, -18.3031, -18.0392,
                -17.7699, -17.4953, -17.2155, -16.9307, -16.6409, -16.3463, -16.0469, -15.7429,
                -15.4344, -15.1215, -14.8043, -14.4829, -14.1575, -13.8282, -13.4950, -13.1582,
                -12.8178, -12.4740, -12.1268, -11.7764, -11.4229, -11.0665, -10.7072, -10.3452,
                -9.9805, -9.6133, -9.2438, -8.8720, -8.4979, -8.1219, -7.7438, -7.3639, -6.9822,
                -6.5988, -6.2139, -5.8275, -5.4398, -5.0508, -4.6607, -4.2695, -3.8775, -3.4846,
                -3.0910, -2.6969, -2.3022, -1.9072, -1.5119, -1.1164, -0.7209, -0.3255, 0.0698,
                0.4648, 0.8594, 1.2535, 1.6471, 2.0400, 2.4321, 2.8234, 3.2137, 3.6030,
                3.9912, 4.3781, 4.7638, 5.1480, 5.5308, 5.9120, 6.2915, 6.6692, 7.0451,
                7.4190, 7.7908, 8.1605, 8.5279, 8.8929, 9.2555, 9.6155, 9.9728, 10.3274,
                10.6791, 11.0279, 11.3736, 11.7161, 12.0554, 12.3913, 12.7239, 13.0529, 13.3783,
                13.7000, 14.0179, 14.3321, 14.6423, 14.9485, 15.2506, 15.5485, 15.8421, 16.1315,
                16.4163, 16.6967, 16.9724, 17.2435, 17.5098, 17.7712, 18.0276, 18.2790, 18.5253,
                18.7663, 19.0021, 19.2325, 19.4574, 19.6768, 19.8906, 20.0987, 20.3010, 20.4976,
                20.6882, 20.8730, 21.0517, 21.2244, 21.3910, 21.5514, 21.7056, 21.8535, 21.9952,
                22.1304, 22.2593, 22.3817, 22.4976, 22.6069, 22.7096, 22.8057, 22.8952, 22.9779,
                23.0539, 23.1231, 23.1855, 23.2411, 23.2898, 23.3317, 23.3667, 23.3949, 23.4161,
                23.4304, 23.4379, 23.4384, 23.4321, 23.4189, 23.3988, 23.3718, 23.3380, 23.2974,
                23.2500, 23.1958, 23.1348, 23.0671, 22.9926, 22.9115, 22.8237, 22.7293, 22.6284,
                22.5208, 22.4068, 22.2863, 22.1594, 22.0262, 21.8866, 21.7408, 21.5888, 21.4307,
                21.2665, 21.0963, 20.9202, 20.7382, 20.5503, 20.3568, 20.1576, 19.9527, 19.7424,
                19.5266, 19.3054, 19.0788, 18.8471, 18.6101, 18.3681, 18.1210, 17.8690, 17.6121,
                17.3505, 17.0841, 16.8131, 16.5375, 16.2575, 15.9732, 15.6845, 15.3917, 15.0947,
                14.7938, 14.4889, 14.1802, 13.8678, 13.5517, 13.2321, 12.9091, 12.5826, 12.2528,
                11.9199, 11.5838, 11.2446, 10.9025, 10.5575, 10.2098, 9.8593, 9.5062, 9.1505,
                8.7924, 8.4319, 8.0692, 7.7042, 7.3372, 6.9681, 6.5971, 6.2243, 5.8498,
                5.4736, 5.0959, 4.7168, 4.3364, 3.9547, 3.5719, 3.1881, 2.8033, 2.4177,
                2.0313, 1.6442, 1.2565, 0.8683, 0.4797, 0.0907, -0.2984, -0.6878, -1.0772,
                -1.4665, -1.8558, -2.2448, -2.6335, -3.0219, -3.4097, -3.7970, -4.1835, -4.5693,
                -4.9542, -5.3380, -5.7207, -6.1022, -6.4823, -6.8610, -7.2381, -7.6135, -7.9871,
                -8.3589, -8.7287, -9.0964, -9.4619, -9.8250, -10.1858, -10.5441, -10.8998,
                -11.2528, -11.6030, -11.9503, -12.2945, -12.6357, -12.9736, -13.3081, -13.6392,
                -13.9668, -14.2907, -14.6108, -14.9270, -15.2391, -15.5472, -15.8509, -16.1503,
                -16.4451, -16.7353, -17.0208, -17.3014, -17.5771, -17.8476, -18.1130, -18.3731,
                -18.6278, -18.8769, -19.1205, -19.3584, -19.5904, -19.8166, -20.0368, -20.2508,
                -20.4587, -20.6603, -20.8555, -21.0443, -21.2265, -21.4021, -21.5709, -21.7329,
                -21.8881, -22.0362, -22.1772, -22.3112, -22.4378, -22.5572, -22.6693, -22.7739,
                -22.8710, -22.9606, -23.0427, -23.1171, -23.1838, -23.2429, -23.2942, -23.3378,
                -23.3736, -23.4016, -23.4218, -23.4341, -23.4387, -23.4354, -23.4243, -23.4053,
                -23.3785, -23.3439, -23.3014, -23.2512, -23.1933, -23.1275};

        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2002, Calendar.JANUARY, 1, 0, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);

        double actualRA;
        double actualDecl;

        for (int i = 0; i < 365; i++) {
            actualRA = sun.getRA(d);
            assertEquals(expectedRA[i], actualRA, 0.05);
            actualDecl = sun.getDecl(d);
            assertEquals(("i = " + i), expectedDecl[i], actualDecl, 0.05);
            d += 1.0;
        }
    }
}

