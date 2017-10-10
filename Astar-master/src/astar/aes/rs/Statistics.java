// Source is from Selfis at http://www.cs.ucr.edu/~tkarag/index.html
// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 10/12/2008 10:30:12 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Statistics.java

package astar.aes.rs;

import java.io.PrintStream;
import java.util.Vector;

import cern.jet.stat.Gamma;

class Statistics
{

    Statistics()
    {
    }

//    public static void main(String args[])
//    {
//        FileUtils fileutils = new FileUtils();
//        FileUtils _tmp = fileutils;
//        if(FileUtils.openFile(args[0]) != -1)
//        {
//            FileUtils _tmp1 = fileutils;
//            Vector vector = new Vector(FileUtils.signal);
//            Statistics statistics = new Statistics();
//            System.out.println(vector.size());
//            Statistics _tmp2 = statistics;
//            Vector vector1 = fft(vector);
//            System.out.println(vector1.size());
//            for(int i = 0; i < vector1.size(); i++)
//                System.out.println((Double)vector1.elementAt(i));
//
//        }
//    }

    public static double avg(Vector vector)
    {
        double d = 0.0D;
        for(int i = 0; i < vector.size(); i++)
        {
            Double double1 = (Double)vector.elementAt(i);
            d += double1.doubleValue();
        }

        return d / (double)vector.size();
    }

    public static double rs(Vector vector)
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = avg(vector);
        Double double1 = (Double)vector.elementAt(0);
        double d1 = double1.doubleValue();
        double d2 = d1;
        double d3 = 0.0D;
        for(int i = 0; i < vector.size(); i++)
        {
            Double double2 = (Double)vector.elementAt(i);
            d3 += double2.doubleValue() - d;
            if(d3 > d1)
                d1 = d3;
            else
            if(d3 < d2)
                d2 = d3;
        }

        return d1 - d2;
    }

    public static double[] calcSlope(Vector xs, Vector ys)
    {
        double sumx = 0.0;
        double sumy = 0.0;
        double sumx2 = 0.0;
        double sumy2 = 0.0;
        double sumxy = 0.0;
        double n = xs.size();
        for(int i = 0; i < n; i++)
        {
            double x = ((Double)xs.elementAt(i)).doubleValue();
            double y = ((Double)ys.elementAt(i)).doubleValue();

            sumx += x;
            sumy += y;
            sumx2 += x * x;
            sumy2 += y * y;
            sumxy += x * y;
        }

        // The methods below for computing the T values were found in PHP at:
        // http://www.ibm.com/developerworks/web/library/wa-linphp/
        double meanx = sumx / n;
        double meany = sumy / n;
        
        double varx = sumx2 / (n-1) - meanx * meanx;
        double vary = sumy2 / (n-1) - meany * meany;

        double slope = (sumxy - (sumx * sumy) / n) / (sumx2 - (sumx * sumx) / n);
        double intc = (sumy - slope * sumx) / n;
        double corr = (sumxy - (sumx * sumy) / n) / Math.sqrt(sumx2 - (sumx * sumx) / n) / Math.sqrt(sumy2 - (sumy * sumy) / n);

        
        double sumErr2 = 0.0;
        double sumxx2 = 0.0;
        double sumyy2 = 0.0;
        double sumxy2 = 0.0;
        for(int i=0; i < n; i++) {
        	double x = ((Double)xs.elementAt(i)).doubleValue();
        	double y = ((Double)ys.elementAt(i)).doubleValue();
        	
        	double predY = intc + slope * x;
        	
        	double err = y - predY;
        	       	
        	sumErr2 += (err * err);
        	
        	sumxx2 += (x - meanx) * (x - meanx);
        	sumyy2 += (y - meany) * (y - meany);
        	sumxy2 += (x - meanx) * (y - meany);
        }
        
        double errVar = sumErr2 / (n - 2);
        double stdErr = Math.sqrt(errVar);
        double slopeStdErr = stdErr / Math.sqrt(sumxx2);
        double intcStdErr = stdErr * Math.sqrt(1.0 / n + meanx * meanx / sumxx2);
        double slopeT = slope / slopeStdErr;
        double intcT = intc / intcStdErr;
        
//    	avevar(data1,n1,&ave1,&var1);
//    	avevar(data2,n2,&ave2,&var2);
//    	*t=(ave1-ave2)/sqrt(var1/n1+var2/n2);
    	double df=SQR(varx/n+vary/n)/(SQR(varx/n)/(n-1)+SQR(vary/n)/(n-1));
    	double prob = Gamma.incompleteBeta(0.5*df,0.5,df/(df+SQR(slopeT)));

        double ad[] = {
                slope, intc, corr, slopeT, intcT, prob
            };        
        
        return ad;
    }

    public static double SQR(double x) {
    	return x * x;
    }
    
    public static double var(Vector vector)
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = avg(vector);
        double d1 = 0.0D;
        double d2 = 0.0D;
        for(int i = 0; i < vector.size(); i++)
        {
            Double double1 = (Double)vector.elementAt(i);
            d1 += Math.pow(double1.doubleValue() - d, 2D);
        }

        if(vector.size() > 1)
            d2 = d1 / (double)(vector.size() - 1);
        return d2;
    }

    public static double std(Vector vector)
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = var(vector);
        if(d > 0.0D)
            return Math.sqrt(d);
        else
            return 1.0D;
    }

    public static double skewness(Vector vector)
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = avg(vector);
        double d1 = 0.0D;
        for(int i = 0; i < vector.size(); i++)
        {
            Double double1 = (Double)vector.elementAt(i);
            d1 += Math.pow(double1.doubleValue() - d, 3D);
        }

        d1 /= vector.size();
        Statistics _tmp1 = statistics;
        double d2 = Math.pow(std(vector), 3D);
        return d1 / d2;
    }

    public static double kurtosis(Vector vector)
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = avg(vector);
        double d1 = 0.0D;
        for(int i = 0; i < vector.size(); i++)
        {
            Double double1 = (Double)vector.elementAt(i);
            d1 += Math.pow(double1.doubleValue() - d, 4D);
        }

        d1 /= vector.size();
        Statistics _tmp1 = statistics;
        double d2 = Math.pow(std(vector), 4D);
        return d1 / d2;
    }

    public static Vector Acf(Vector vector, int i)
    {
        Vector vector1 = new Vector();
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        double d = avg(vector);
        for(int j = 0; j <= i; j++)
        {
            double d1 = 0.0D;
            double d2 = 0.0D;
            for(int k = 1; k < vector.size() - j; k++)
            {
                double d3 = ((Double)vector.elementAt(k)).doubleValue() - d;
                double d4 = ((Double)vector.elementAt(k + j)).doubleValue() - d;
                d1 += d3 * d4;
                d2 += d3 * d3;
            }

            vector1.add(new Double(d1 / d2));
        }

        return vector1;
    }

    public static Vector fft(Vector vector)
    {
        double d = 1.0D;
        int i = 0;
        Vector vector1;
        for(vector1 = (Vector)vector.clone(); d < (double)vector1.size();)
        {
            d *= 2D;
            i++;
        }

        i--;
        d /= 2D;
        for(int i2 = (int)d + 1; i2 < vector.size() + 1; i2++)
            vector1.removeElementAt(vector1.size() - 1);

        int j = i << 1;
        int j1 = 1;
        for(int j2 = 1; j2 < j; j2 += 2)
        {
            if(j1 > j2)
            {
                Double double1 = (Double)vector1.elementAt(j2);
                vector1.setElementAt(vector1.elementAt(j1), j2);
                vector1.setElementAt(double1, j1);
                double1 = (Double)vector1.elementAt(j2 + 1);
                vector1.setElementAt(vector1.elementAt(j1 + 1), j2 + 1);
                vector1.setElementAt(double1, j1 + 1);
            }
            int l;
            for(l = j >> 1; l >= 2 && j1 > l; l >>= 1)
                j1 -= l;

            j1 += l;
        }

        int l1;
        for(int k = 2; j > k; k = l1)
        {
            l1 = 2 * k;
            double d5 = 6.2831853071795898D / (double)k;
            double d6 = Math.sin(0.5D * d5);
            double d2 = -2D * d6 * d6;
            double d3 = Math.sin(d5);
            double d1 = 1.0D;
            double d4 = 0.0D;
            for(int i1 = 1; i1 < k; i1 += 2)
            {
                for(int k2 = i1; k2 <= j; k2 += l1)
                {
                    int k1 = k2 + k;
                    double d8 = d1 * ((Double)vector1.elementAt(k1)).doubleValue();
                    d8 -= d4 * ((Double)vector1.elementAt(k1 + 1)).doubleValue();
                    double d9 = d1 * ((Double)vector1.elementAt(k1 + 1)).doubleValue();
                    d9 += d4 * ((Double)vector1.elementAt(k1)).doubleValue();
                    Double double2 = new Double(((Double)vector1.elementAt(k2)).doubleValue() - d8);
                    vector1.setElementAt(double2, k1);
                    double2 = new Double(((Double)vector1.elementAt(k2 + 1)).doubleValue() - d9);
                    vector1.setElementAt(double2, k1 + 1);
                    double2 = new Double(((Double)vector1.elementAt(k2)).doubleValue() + d8);
                    vector1.setElementAt(double2, k2);
                    double2 = new Double(((Double)vector1.elementAt(k2 + 1)).doubleValue() + d9);
                    vector1.setElementAt(double2, k2 + 1);
                }

                double d7;
                d1 = ((d7 = d1) * d2 - d4 * d3) + d1;
                d4 = d4 * d2 + d7 * d3 + d4;
            }

        }

        return vector1;
    }
}