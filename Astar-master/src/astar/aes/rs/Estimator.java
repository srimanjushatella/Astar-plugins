// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 10/12/2008 10:35:10 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Estimator.java

package astar.aes.rs;

import java.util.Vector;

abstract class Estimator
{

    Estimator()
    {
        logX = new Vector();
        logY = new Vector();
    }

    public double[] calcSlope()
    {
        Statistics statistics = new Statistics();
        Statistics _tmp = statistics;
        int szx = logX.size();
        int szy = logY.size();
        double ad[] = Statistics.calcSlope(logX, logY);
        slopeH = ad[0];
        return ad;
    }

    public String calcH(String s)
    {
        if(s == "var")
            return String.valueOf((slopeH + 2D) / 2D);
        if(s == "rs")
            return String.valueOf(Math.abs(slopeH));
        if(s == "per")
            return String.valueOf(Math.abs((1.0D - slopeH) / 2D));
        if(s == "abs")
            return String.valueOf(Math.abs(slopeH + 1.0D));
        if(s == "res")
            return String.valueOf(Math.abs(slopeH / 2D));
        if(s == "wh")
            return String.valueOf(Math.abs(whittleH));
        if(s == "av")
            return String.valueOf(Math.abs(slopeH));
        else
            return null;
    }

    public abstract void estimate(Vector vector);

    Vector logX;
    Vector logY;
    double slopeH;
    double whittleH;
    double avH;
    public double whittleCi[];
    public double ci[];
}