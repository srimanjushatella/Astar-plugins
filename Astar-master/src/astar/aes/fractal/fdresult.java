// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   fdresult.java

package astar.aes.fractal;

import java.io.*;
import java.util.Vector;

public class fdresult
{

    public void AddTotal(int i, int j)
    {
        TotalList[i] += j;
    }

    public void addResult(double d, double d1)
    {
        NormList[currentIndex] = d;
        CumuList[currentIndex] = d1;
        currentIndex++;
    }

    public void calcBoxCountLogs()
    {
        for(int i = 0; i < NormList.length; i++)
        {
            RadiusLog[i] = Math.log(NormList[i]);
            CumuLog[i] = Math.log(1.0D / CumuList[i]);
        }

        Logmin = 0;
        Logmax = RadiusLog.length - 1;
    }

    public void CalcLogCumu()
    {
        boolean flag = false;
        for(int i = 1; i < CumuList.length; i++)
        {
            if(CumuList[i] > 0.0D)
            {
                if(!flag)
                {
                    flag = true;
                    Logmin = i;
                }
                RadiusLog[i] = Math.log(i);
                CumuLog[i] = Math.log(CumuList[i]);
            }
            if(NormList[i] > 0.0D)
                Logmax = i;
        }

    }

    public void CalcNormList(int i)
    {
        for(int j = 0; j < NormList.length; j++)
            NormList[j] = (double)TotalList[j] / (double)i;

    }

    public void CalcCumuList()
    {
        CumuList[0] = NormList[0];
        for(int i = 1; i < CumuList.length; i++)
            CumuList[i] = NormList[i] + CumuList[i - 1];

    }

    public double CalcLogSlope()
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        int i = (Logmax - Logmin) + 1;
        for(int j = Logmin; j <= Logmax; j++)
        {
            d += RadiusLog[j];
            d1 += CumuLog[j];
            d2 += RadiusLog[j] * CumuLog[j];
            d3 += RadiusLog[j] * RadiusLog[j];
        }

        d4 = ((double)i * d2 - d * d1) / ((double)i * d3 - d * d);
        if(Verbose)
        {
            System.out.println("CalcLogSlope from " + Logmin + " to " + Logmax);
            System.out.println(d + " " + d1 + " " + d2 + " " + d3);
        }
        return d4;
    }

    public void CreateArrays(int i)
    {
        currentIndex = 0;
        CumuList = new double[i];
        CumuLog = new double[i];
        NormList = new double[i];
        RadiusLog = new double[i];
        TotalList = new int[i];
        for(int j = 0; j < i; j++)
        {
            CumuList[j] = 0.0D;
            CumuLog[j] = 0.0D;
            NormList[j] = 0.0D;
            RadiusLog[j] = 0.0D;
            TotalList[j] = 0;
        }

    }

    public fdresult(boolean flag)
    {
        CumuList = null;
        CumuLog = null;
        Logmin = 0;
        Logmax = 0;
        NormList = null;
        RadiusLog = null;
        TotalList = null;
        Verbose = false;
        CumuList = null;
        CumuLog = null;
        NormList = null;
        RadiusLog = null;
        TotalList = null;
        Verbose = flag;
    }

    public double[] getCumuList()
    {
        return CumuList;
    }

    public double[] getCumuLog()
    {
        return CumuLog;
    }

    public String getHeading()
    {
        return "ndx TotalList NormList CumuList log(R) log(cusum)";
    }

    public int getLength()
    {
        int i = 0;
        if(CumuList != null)
            i = CumuList.length;
        return i;
    }

    public int getLogmin()
    {
        return Logmin;
    }

    public int getLogmax()
    {
        return Logmax;
    }

    public double[] getNormList()
    {
        return NormList;
    }

    public double[] getRadiusLog()
    {
        return RadiusLog;
    }

    public Vector getRecord(int i)
    {
        if(TotalList != null && i < TotalList.length)
        {
            Vector vector = new Vector();
            vector.add(0, new Integer(i));
            vector.add(1, new Double(TotalList[i]));
            vector.add(2, new Double(NormList[i]));
            vector.add(3, new Double(CumuList[i]));
            vector.add(4, new Double(RadiusLog[i]));
            vector.add(5, new Double(CumuLog[i]));
            return vector;
        } else
        {
            return null;
        }
    }

    public int[] getTotalList()
    {
        return TotalList;
    }

    public void Save(String s)
    {
        try
        {
            PrintWriter printwriter = new PrintWriter(new BufferedWriter(new FileWriter(s)));
            boolean flag = false;
            if(CumuList != null && NormList != null && TotalList != null)
            {
                int i = CumuList.length;
                printwriter.println("ndx NormList CumuList log(R) log(cusum)");
                for(int j = 0; j < i; j++)
                    printwriter.println(j + " " + NormList[j] + " " + CumuList[j] + " " + RadiusLog[j] + " " + CumuLog[j]);
                	//printwriter.println(RadiusLog[j] + " " + CumuLog[j]);


            } else
            {
                printwriter.println("No results to display.");
            }
            printwriter.close();
        }
        catch(Exception exception)
        {
            System.err.println("Error in opening file for writing" + exception);
        }
    }

    public int showResult()
    {
        int i = 0;
        if(CumuList != null && NormList != null && TotalList != null)
        {
            i = CumuList.length;
            System.out.println("ndx TotalList NormList CumuList log(R) log(cusum)");
            for(int j = 0; j < i; j++)
                System.out.println(j + " " + TotalList[j] + " " + NormList[j] + " " + CumuList[j] + " " + RadiusLog[j] + " " + CumuLog[j]);

        } else
        {
            System.out.println("No results to display.");
        }
        return i;
    }

    private double CumuList[];
    private double CumuLog[];
    private int Logmin;
    private int Logmax;
    private double NormList[];
    private double RadiusLog[];
    private int TotalList[];
    private boolean Verbose;
    private static int currentIndex;
}


