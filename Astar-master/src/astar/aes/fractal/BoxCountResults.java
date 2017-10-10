// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BoxCountResults.java

package astar.aes.fractal;

import java.util.Hashtable;
import java.util.Vector;

public class BoxCountResults
{

    public BoxCountResults()
    {
    }

    public static void addHashEntry(int i, int j)
    {
        String s = i + "+" + j;
        if(hash.containsKey(s))
        {
            int k = ((Integer)hash.get(s)).intValue();
            k++;
            hash.put(s, new Integer(k));
        } else
        {
            hash.put(s, new Integer(1));
        }
    }

    public static void calcMultifractal(int ai[], int i, int j, int k)
    {
        results = new double[2][j - i];
        double d = 0.0D;
        double d2 = 0.0D;
        double ad[][] = new double[(j - i) + 1][ai.length];
        double ad1[][] = new double[(j - i) + 1][ai.length];
        for(int l = 0; l < ai.length; l++)
            if(ai[l] > 0)
            {
                int i1 = ai[l];
                double d3 = 0.0D;
                double d1 = 0.0D;
                for(int j1 = 1; j1 <= i1 * i1; j1++)
                {
                    String s = i1 + "+" + j1;
                    if(hash.containsKey(s))
                    {
                        int i2 = ((Integer)hash.get(s)).intValue();
                        Vector vector = new Vector(3);
                        vector.add(0, new Integer(i1));
                        vector.add(1, new Integer(j1));
                        vector.add(2, new Integer(i2));
                        multiFractalData.add(vector);
                        d1 += j1 * i2;
                    }
                }

                int k1 = 0;
                for(int j2 = i; j2 <= j; j2++)
                {
                    double d4 = 0.0D;
                    for(int k2 = 1; k2 <= i1 * i1; k2++)
                    {
                        String s1 = i1 + "+" + k2;
                        if(hash.containsKey(s1))
                        {
                            int l2 = ((Integer)hash.get(s1)).intValue();
                            d4 += Math.pow((double)k2 / d1, j2) * (double)l2;
                        }
                    }

                    ad1[k1][l] = Math.log(d4);
                    ad[k1][l] = Math.log(i1);
                    k1++;
                }

            }

        double ad2[] = new double[(j - i) + 1];
        double ad3[] = new double[(j - i) + 1];
        for(int l1 = 0; l1 < ad1.length; l1++)
        {
            ad2[l1] = i + l1;
            double d5 = CalcLogSlope(ad[l1], ad1[l1]);
            ad3[l1] = d5 / (ad2[l1] - 1.0D);
        }

        results[0] = ad2;
        results[1] = ad3;
    }

    public static double CalcLogSlope(double ad[], double ad1[])
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        int i = ad.length;
        for(int j = 0; j < ad.length; j++)
        {
            d += ad[j];
            d1 += ad1[j];
            d2 += ad[j] * ad1[j];
            d3 += ad[j] * ad[j];
        }

        d4 = ((double)i * d2 - d * d1) / ((double)i * d3 - d * d);
        return d4;
    }

    public static Vector GetFractalData()
    {
        return multiFractalData;
    }

    public static double[][] getDqResults()
    {
        return results;
    }

    private static Hashtable hash = new Hashtable();
    private static double results[][];
    private static Vector multiFractalData = new Vector();

}


