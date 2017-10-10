package astar.aes.fractal;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BoxCountingMethod.java


public class BoxCountingMethod
{

    public BoxCountingMethod()
    {
    }

    public static void setBoxOptions(int ai[], boolean flag, int i, int j, int k)
    {
        boxes = ai;
        doMultiFractal = flag;
        minQ = i;
        maxQ = j;
        incQ = k;
    }

    public static void BoxCountingMethod(fdresult fdresult1, fdimage fdimage1)
    {
        fdresult1.CreateArrays(boxes.length);
        for(int i = 0; i < boxes.length; i++)
            if(boxes[i] > 0)
                BoxCount(fdresult1, fdimage1, boxes[i], doMultiFractal);

        fdresult1.calcBoxCountLogs();
        if(doMultiFractal)
            BoxCountResults.calcMultifractal(boxes, minQ, maxQ, incQ);
    }

    private static void BoxCount(fdresult fdresult1, fdimage fdimage1, int i, boolean flag)
    {
        int ai[] = findImageBounds(fdimage1);
        boolean flag1 = false;
        int j = ai[0];
        int k = ai[1];
        int l = 0;
        int j1 = 0;
        while(!flag1) 
        {
            int i1 = 0;
            for(int k1 = 0; k1 < i; k1++)
            {
                for(int l1 = 0; l1 < i; l1++)
                    try
                    {
                        if(fdimage1.isPixel(j + l1, k + k1))
                            i1++;
                    }
                    catch(Exception exception) { }

            }

            if(i1 > 0)
            {
                if(flag)
                    BoxCountResults.addHashEntry(i, i1);
                l += i1;
                j1++;
            }
            j += i;
            if(j > ai[2])
            {
                j = ai[0];
                k += i;
                flag1 = k > ai[3];
            }
        }
        fdresult1.addResult(i, j1);
    }

    private static int[] findImageBounds(fdimage fdimage1)
    {
        int ai[] = new int[4];
        boolean flag = false;
        int i = -1;
        while(!flag) 
        {
            i++;
            for(int i1 = 0; i1 < fdimage1.getHeight(); i1++)
            {
                if(!fdimage1.isPixel(i, i1))
                    continue;
                flag = true;
                break;
            }

        }
        flag = false;
        int j = fdimage1.getWidth();
        while(!flag) 
        {
            j--;
            for(int j1 = 0; j1 < fdimage1.getHeight(); j1++)
            {
                if(!fdimage1.isPixel(j, j1))
                    continue;
                flag = true;
                break;
            }

        }
        flag = false;
        int k = -1;
        while(!flag) 
        {
            k++;
            for(int k1 = 0; k1 < fdimage1.getWidth(); k1++)
            {
                if(!fdimage1.isPixel(k1, k))
                    continue;
                flag = true;
                break;
            }

        }
        flag = false;
        int l = fdimage1.getHeight();
        while(!flag) 
        {
            l--;
            for(int l1 = 0; l1 < fdimage1.getWidth(); l1++)
            {
                if(!fdimage1.isPixel(l1, l))
                    continue;
                flag = true;
                break;
            }

        }
        ai[0] = i;
        ai[1] = k;
        ai[2] = j;
        ai[3] = l;
        return ai;
    }

    private static int boxes[];
    private static boolean doMultiFractal;
    private static int minQ;
    private static int maxQ;
    private static int incQ;
}

