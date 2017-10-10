// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 10/12/2008 10:31:34 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RsPlot.java

package astar.aes.rs;

import java.util.Vector;

public class RsPlot extends Estimator
{

    public RsPlot()
    {
    }

    public void estimate(Vector vector)
    {
        int i = (int)Math.floor(vector.size() / 2);
        int j = 4;
        Statistics statistics = new Statistics();
        Vector vector1 = new Vector();
        boolean flag = false;
        for(; j <= i; j *= 2)
            vector1.addElement(new Integer(j));

        Vector vector2 = new Vector();
        for(int k = 0; k < vector1.size(); k++)
        {
            int i1 = ((Integer)vector1.elementAt(k)).intValue();
            int j1 = vector.size() / i1;
            int k1 = 0;
            boolean flag1 = false;
            Vector vector3 = new Vector();
            for(; k1 < j1; k1++)
            {
                int i2 = k1 * i1;
                Vector vector4 = new Vector();
                for(; i2 < (k1 + 1) * i1; i2++)
                    vector4.addElement(vector.elementAt(i2));

                Statistics _tmp = statistics;
                double d2 = Statistics.rs(vector4);
                Statistics _tmp1 = statistics;
                double d3 = Statistics.var(vector4);
                double d4 = 0.0D;
                if(d3 > 0.0D)
                    d4 = d2 / Math.sqrt(d3);
                else
                    d4 = (double)(vector4.size() - 1) / Math.sqrt(vector4.size());
                vector3.add(new Double(d4));
            }

            vector2.add(new Double(statistics.avg(vector3)));
        }

        for(int l = 0; l < vector2.size(); l++)
        {
            double d = ((Double)vector2.elementAt(l)).doubleValue();
            int l1 = ((Integer)vector1.elementAt(l)).intValue();
            double d1 = Math.log(d);
            logY.add(new Double(d1));
            d1 = Math.log(l1);
            logX.add(new Double(d1));
        }

    }
}