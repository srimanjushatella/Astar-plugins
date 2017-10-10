// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   fdline.java

package astar.aes.fractal;

import java.awt.Graphics;
import java.awt.Point;
import java.io.PrintStream;

public class fdline
{

    public void Append()
    {
        if(last == null || first == null)
            first = this;
        else
            last.next = this;
        last = this;
        next = null;
    }

    public static void Deconstruct()
    {
        fdline fdline2;
        for(fdline fdline1 = first; fdline1 != null; fdline1 = fdline2)
        {
            fdline2 = fdline1.next;
            fdline1.next = null;
        }

        last = null;
    }

    public double Distance(fdpoint fdpoint1)
    {
        double d = start.getY() - end.getY();
        double d1 = end.getX() - start.getX();
        double d2 = start.getX() * end.getY() - end.getX() * start.getY();
        double d3 = Math.abs(d * fdpoint1.getX() + d1 * fdpoint1.getY() + d2) / Length();
        return d3;
    }

    public void draw(Graphics g)
    {
        g.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
    }

    public fdpoint End()
    {
        return end;
    }

    public static void Extract()
    {
        if(first != null)
            first = first.next;
        last = null;
    }

    public fdline(fdpoint fdpoint1, fdpoint fdpoint2)
    {
        start = null;
        end = null;
        next = null;
        start = new fdpoint(fdpoint1);
        end = new fdpoint(fdpoint2);
        next = null;
        if(first == null)
            first = this;
    }

    public static fdline First()
    {
        return first;
    }

    public void Insert()
    {
        if(last == null || first == null)
        {
            last = this;
            next = null;
        } else
        {
            next = first;
        }
        first = this;
    }

    public static fdline Last()
    {
        return last;
    }

    public double Length()
    {
        return start.Euclidean(end);
    }

    public static void main(String args[])
    {
        fdpoint fdpoint1 = new fdpoint(0, 0);
        fdpoint fdpoint2 = new fdpoint(1, 2);
        fdpoint fdpoint3 = new fdpoint(2, 1);
        fdpoint fdpoint4 = new fdpoint(1, 4);
        fdline fdline1 = new fdline(fdpoint2, fdpoint3);
        fdline fdline2 = new fdline(fdpoint3, fdpoint4);
        fdline fdline3 = new fdline(fdpoint4, fdpoint2);
        System.out.println("A line " + fdline1.toString() + " is " + fdline1.Length() + " long, and " + fdline1.Distance(fdpoint1) + " from the origin");
        System.out.println("A line " + fdline2.toString() + " is " + fdline2.Length() + " long, and " + fdline2.Distance(fdpoint1) + " from the origin");
        System.out.println("A line " + fdline3.toString() + " is " + fdline3.Length() + " long, and " + fdline3.Distance(fdpoint1) + " from the origin");
        first = null;
        last = null;
        fdline1.Insert();
        fdline2.Insert();
        fdline3.Insert();
        fdline fdline4 = first;
        System.out.println("List of the lines after inserting:");
        for(; fdline4 != null; fdline4 = fdline4.next)
            System.out.println(fdline4.toString());

        Deconstruct();
        first = null;
        last = null;
        fdline1.Append();
        fdline2.Append();
        fdline3.Append();
        fdline4 = first;
        System.out.println("List of the lines after appending:");
        for(; fdline4 != null; fdline4 = fdline4.next)
            System.out.println(fdline4.toString());

    }

    public fdline Next()
    {
        return next;
    }

    public fdpoint Start()
    {
        return start;
    }

    public String toString()
    {
        return "(" + start.getX() + ", " + start.getY() + ") to (" + end.getX() + ", " + end.getY() + ")";
    }

    private fdpoint start;
    private fdpoint end;
    private fdline next;
    private static fdline first = null;
    private static fdline last = null;

}

