// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   fdpoint.java

package astar.aes.fractal;

import java.awt.Point;
import java.io.PrintStream;

public class fdpoint extends Point
{

    public boolean Adjacent(fdpoint fdpoint1)
    {
        return Math.abs((double)super.x - fdpoint1.getX()) < 2D && Math.abs((double)super.y - fdpoint1.getY()) < 2D;
    }

    public int ccw(fdpoint fdpoint1, fdpoint fdpoint2)
    {
        fdpoint fdpoint3 = new fdpoint(fdpoint1);
        fdpoint1.toPolar(this);
        fdpoint fdpoint4 = new fdpoint(fdpoint2);
        fdpoint2.toPolar(this);
        double d = fdpoint2.Compare(fdpoint1, 'a');
        if(d < 0.0D)
            d += 6.2831853071795862D;
        byte byte0;
        if(d == 0.0D || d == 3.1415926535897931D)
            byte0 = 0;
        else
        if(d < 3.1415926535897931D)
            byte0 = 1;
        else
            byte0 = -1;
        return byte0;
    }

    public int ccw_old(fdpoint fdpoint1, fdpoint fdpoint2)
    {
        byte byte0 = 0;
        int i = (int)fdpoint1.getX() - super.x;
        int k = (int)fdpoint1.getY() - super.y;
        int j = (int)fdpoint2.getX() - super.x;
        int l = (int)fdpoint2.getY() - super.y;
        int i1 = i * l;
        int j1 = k * j;
        if(i1 > j1)
            byte0 = 1;
        else
        if(i1 < j1)
            byte0 = -1;
        else
        if(i * j < 0 || k * l < 0)
            byte0 = -1;
        else
        if(i * i + k * k < j * j + l * l)
            byte0 = 1;
        return byte0;
    }

    public double Compare(fdpoint fdpoint1, char c)
    {
        switch(c)
        {
        case 120: // 'x'
            return (double)super.x - fdpoint1.getX();

        case 121: // 'y'
            return (double)super.y - fdpoint1.getY();

        case 97: // 'a'
            return angle - fdpoint1.getAngle();

        case 114: // 'r'
            return radius - fdpoint1.getRadius();
        }
        return 0.0D;
    }

    public double Euclidean(fdpoint fdpoint1)
    {
        double d = (double)super.x - fdpoint1.getX();
        double d1 = (double)super.y - fdpoint1.getY();
        return Math.sqrt(d * d + d1 * d1);
    }

    public fdpoint(int i, int j)
    {
        super(i, j);
        status = 0;
    }

    public fdpoint(double d, double d1)
    {
        angle = d1;
        radius = d;
        if(radius < 0.0D)
            radius = 0.0D;
        status = 0;
    }

    public fdpoint(fdpoint fdpoint1)
    {
        super.x = (int)fdpoint1.getX();
        super.y = (int)fdpoint1.getY();
        angle = fdpoint1.getAngle();
        radius = fdpoint1.getRadius();
        status = 0;
    }

    public double getAngle()
    {
        return angle;
    }

    public double getRadius()
    {
        return radius;
    }

    public double getStatus()
    {
        return (double)status;
    }

    private static void insertionSort(fdpoint afdpoint[], char c, char c1, int i, int j)
    {
        for(int k = i + 1; k <= j; k++)
        {
            fdpoint fdpoint1 = afdpoint[k];
            int l;
            for(l = k; l > i; l--)
            {
                double d = fdpoint1.Compare(afdpoint[l - 1], c);
                if(d > 0.0D || d == 0.0D && fdpoint1.Compare(afdpoint[l - 1], c1) > 0.0D)
                    break;
                afdpoint[l] = afdpoint[l - 1];
            }

            afdpoint[l] = fdpoint1;
        }

    }

    public static void main(String args[])
    {
        byte byte0 = 10;
        fdpoint fdpoint1 = new fdpoint(9, 7);
        System.out.println("new point p0 is " + fdpoint1);
        fdpoint1.setpoint(0, 0);
        System.out.println("move p0 to 0,0: " + fdpoint1);
        fdpoint fdpoint2 = new fdpoint(1.4199999999999999D, 0.78539816339744828D);
        System.out.println("new point p1 is " + fdpoint2);
        fdpoint2.toCartesian(0.0D, 0.0D);
        System.out.println("Convert p1 to cartesian: " + fdpoint2);
        fdpoint fdpoint3 = new fdpoint(3, 3);
        System.out.println("new point p2 is " + fdpoint3);
        fdpoint fdpoint4 = new fdpoint(2, 4);
        System.out.println("new point p3 is " + fdpoint4);
        if(fdpoint1.Adjacent(fdpoint2))
            System.out.println("p0 and p1 are adjacent");
        else
            System.out.println("p0 and p1 not adjacent");
        if(fdpoint1.Adjacent(fdpoint3))
            System.out.println("p0 and p2 are adjacent");
        else
            System.out.println("p0 and p2 not adjacent");
        fdpoint fdpoint5 = new fdpoint(-3, 3);
        System.out.println("new point p4 is " + fdpoint5);
        fdpoint fdpoint6 = new fdpoint(-3, -3);
        System.out.println("new point p5 is " + fdpoint6);
        fdpoint fdpoint7 = new fdpoint(3, -3);
        System.out.println("new point p5 is " + fdpoint6);
        System.out.println("p1 to p2 ccw about p0? " + fdpoint1.ccw(fdpoint2, fdpoint3));
        System.out.println("p2 to p1 ccw about p0? " + fdpoint1.ccw(fdpoint3, fdpoint2));
        System.out.println("p2 to p4 ccw about p0? " + fdpoint1.ccw(fdpoint3, fdpoint5));
        System.out.println("p4 to p2 ccw about p0? " + fdpoint1.ccw(fdpoint5, fdpoint3));
        System.out.println("p4 to p5 ccw about p0? " + fdpoint1.ccw(fdpoint5, fdpoint6));
        System.out.println("p5 to p4 ccw about p0? " + fdpoint1.ccw(fdpoint6, fdpoint5));
        System.out.println("p5 to p6 ccw about p0? " + fdpoint1.ccw(fdpoint6, fdpoint7));
        System.out.println("p6 to p5 ccw about p0? " + fdpoint1.ccw(fdpoint7, fdpoint6));
        System.out.println("p6 to p2 ccw about p0? " + fdpoint1.ccw(fdpoint7, fdpoint3));
        System.out.println("p2 to p6 ccw about p0? " + fdpoint1.ccw(fdpoint3, fdpoint7));
        fdpoint2.toPolar(fdpoint1);
        fdpoint3.toPolar(fdpoint1);
        System.out.println("p1 to polar, origin p0: " + fdpoint1);
        System.out.println("p2 to polar, origin p0: " + fdpoint3);
        fdpoint fdpoint8 = fdpoint2;
        fdpoint2 = fdpoint3;
        fdpoint3 = fdpoint8;
        System.out.println("Swap p1 and p2, now " + fdpoint2 + " and " + fdpoint3);
        System.out.println("Compare p0 and p2: x " + fdpoint1.Compare(fdpoint3, 'x') + " y " + fdpoint1.Compare(fdpoint3, 'y') + " r " + fdpoint1.Compare(fdpoint3, 'r') + " a " + fdpoint1.Compare(fdpoint3, 'a'));
        System.out.println("Distance between p0 and p3 is " + fdpoint1.Euclidean(fdpoint4));
        fdpoint afdpoint[] = new fdpoint[byte0];
        for(int i = 0; i < byte0; i++)
            afdpoint[i] = new fdpoint(i / 2, 5 - i % 2);

        quicksort(afdpoint, 'r', 'a');
        System.out.println("Sorted in order of radius, then angle:");
        for(int j = 0; j < byte0; j++)
            System.out.println(afdpoint[j]);

        quicksort(afdpoint, 'a', 'r');
        System.out.println("Sorted in order of angle, then radius:");
        for(int k = 0; k < byte0; k++)
            System.out.println(afdpoint[k]);

    }

    public static void qsort(fdpoint afdpoint[], char c, char c1)
    {
    }

    private static fdpoint median3(fdpoint afdpoint[], char c, int i, int j)
    {
        int k = (i + j) / 2;
        int l = k;
        if(afdpoint[i].Compare(afdpoint[k], c) < 0.0D)
            swapReferences(afdpoint, i, k);
        if(afdpoint[i].Compare(afdpoint[j], c) > 0.0D)
            swapReferences(afdpoint, i, j);
        if(afdpoint[j].Compare(afdpoint[k], c) > 0.0D)
            swapReferences(afdpoint, j, k);
        swapReferences(afdpoint, k, j - 1);
        return afdpoint[j - 1];
    }

    public static void quicksort(fdpoint afdpoint[], char c, char c1)
    {
        qsrecursive(afdpoint, c, c1, 0, afdpoint.length - 1);
    }

    private static void qsrecursive(fdpoint afdpoint[], char c, char c1, int i, int j)
    {
        if(i + 10 <= j)
        {
            fdpoint fdpoint1 = median3(afdpoint, c, i, j);
            int k = i;
            int l = j - 1;
            do
            {
                for(double d = -1D; d < 0.0D;)
                {
                    k++;
                    d = afdpoint[k].Compare(fdpoint1, c);
                    if(d == 0.0D && afdpoint[k].Compare(fdpoint1, c1) >= 0.0D)
                        break;
                }

                for(double d1 = 1.0D; d1 > 0.0D;)
                {
                    l--;
                    d1 = afdpoint[l].Compare(fdpoint1, c);
                    if(d1 == 0.0D && afdpoint[l].Compare(fdpoint1, c1) <= 0.0D)
                        break;
                }

                if(k >= l)
                    break;
                swapReferences(afdpoint, k, l);
            } while(true);
            swapReferences(afdpoint, k, j - 1);
            qsrecursive(afdpoint, c, c1, i, k - 1);
            qsrecursive(afdpoint, c, c1, k + 1, j);
        } else
        {
            insertionSort(afdpoint, c, c1, i, j);
        }
    }

    public void setpoint(int i, int j)
    {
        super.x = i;
        super.y = j;
        status = 0;
    }

    public static void sort(fdpoint afdpoint[], char c, char c1)
    {
        int i = afdpoint.length;
        Object obj = null;
        for(int j = 0; j < i - 1; j++)
        {
            for(int k = j + 1; k < i; k++)
            {
                double d = afdpoint[j].Compare(afdpoint[k], c);
                if(d > 0.0D)
                {
                    fdpoint fdpoint1 = afdpoint[k];
                    afdpoint[k] = afdpoint[j];
                    afdpoint[j] = fdpoint1;
                } else
                if(d == 0.0D && afdpoint[j].Compare(afdpoint[k], c1) > 0.0D)
                {
                    fdpoint fdpoint2 = afdpoint[k];
                    afdpoint[k] = afdpoint[j];
                    afdpoint[j] = fdpoint2;
                }
            }

        }

    }

    public static final void swapReferences(fdpoint afdpoint[], int i, int j)
    {
        fdpoint fdpoint1 = afdpoint[i];
        afdpoint[i] = afdpoint[j];
        afdpoint[j] = fdpoint1;
    }

    public double Theta(fdpoint fdpoint1)
    {
        double d1 = (double)super.x - fdpoint1.getX();
        double d2 = (double)super.y - fdpoint1.getY();
        double d = Math.atan2(d2, d1);
        return d;
    }

    public void toCartesian(double d, double d1)
    {
        super.x = (int)(radius * Math.cos(angle) + d);
        super.y = (int)(radius * Math.sin(angle) + d1);
    }

    public void toPolar(fdpoint fdpoint1)
    {
        double d = (double)super.x - fdpoint1.getX();
        double d1 = (double)super.y - fdpoint1.getY();
        radius = Math.sqrt(d * d + d1 * d1);
        angle = Math.atan2(d1, d);
    }

    public void toPolar(double d, double d1)
    {
        double d2 = (double)super.x - d;
        double d3 = (double)super.y - d1;
        radius = Math.sqrt(d2 * d2 + d3 * d3);
        angle = Math.atan2(d3, d2);
    }

    public String toString()
    {
        return super.x + " " + super.y + " " + radius + " " + angle + " " + status;
    }

    private double angle;
    private double radius;
    private int status;
    public static final int CUTOFF = 10;
}

