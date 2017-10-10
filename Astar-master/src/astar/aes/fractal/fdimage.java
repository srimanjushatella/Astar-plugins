package astar.aes.fractal;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   fdimage.java

import astar.util.Node;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;

import javax.imageio.ImageIO;

public class fdimage extends BufferedImage
{

    public void DrawVectors(Graphics g)
    {
        for(fdline fdline1 = fdline.First(); fdline1 != null; fdline1 = fdline1.Next())
            fdline1.draw(g);

    }

    public void EncodeNeighbours()
    {
        neighbours = new short[Width][Height];
        for(int j = 0; j < Width; j++)
        {
            for(int k = 0; k < Height; k++)
            {
                if(isPixel(j, k))
                    neighbours[j][k] = 256;
                else
                    neighbours[j][k] = 0;
                for(int i = 0; i < 8; i++)
                    neighbours[j][k] |= Mask(j, k, i);

            }

        }

    }

    public fdimage(int i, int j, boolean flag)
    {
        super(i, j, 10);
        neighbours = null;
        tnode_counter = 0;
        pnode = null;
        Verbose = false;
        Width = i;
        Height = j;
        neighbours = null;
        neighbours = null;
        tnode_counter = 0;
        pnode = null;
        Verbose = flag;
    }

    public final static int SIZE = 10;
    
    public fdimage(BufferedImage img) {
    	this(img.getWidth(),img.getHeight(),true);
    	
    	for(int x=0; x < img.getWidth(); x++) {
    		for(int y=0; y < img.getHeight(); y++)
    			this.setRGB(x, y, img.getRGB(x, y));
    	}
    }
    
    public fdimage(Node anode,char[][] tileMap,char obstacle) {
    	this(tileMap.length*SIZE,tileMap.length*SIZE,true);
    	
		Graphics2D g2d = createGraphics();

		// Fill the background with white
		g2d.setColor(new Color(255,255,255));
		g2d.fillRect(0,0,Width,Height);

		// Draw the path
		g2d.setColor(new Color(0,0,0));		
		while(anode != null) {
			int x = anode.getCol();
			int y = anode.getRow();
			
			// Correct broken links (there's a bug in straightLong()?)
			Node parent = anode.getParent();
			Node child = anode.getChild();
			
			if(parent != null) {
				Node self = parent.getChild();
				if(self != anode)
					parent.setChild(anode);
			}
			
			// Render the labels on start / goal configurations
			g2d.setColor(new Color(0,0,0));
			if(child == null && true)
				g2d.drawString("goal", x*10-8, y*10-4);
			
			else if(parent == null && true)
				g2d.drawString("start", x*10-8, y*10-4);			
//			g2d.fillRect(x*SIZE, y*SIZE, SIZE, SIZE);

			g2d.fillOval(x*SIZE, y*SIZE, SIZE, SIZE);
			anode = anode.getParent();
		}
//		// Draw the rooms and hallways
//		///*
//		for(int row=0; row < tileMap.length; row++) {
//			for(int col=0; col < tileMap.length; col++) {
//				if(tileMap[row][col] == obstacle)
//					g2d.fillRect(col*SIZE,row*SIZE,SIZE,SIZE);
////					g2d.fillOval(col*SIZE,row*SIZE,SIZE,SIZE);
//			}
//		}
		//*/
    }
    
    public int getBrightness(int i)
    {
        Color color = new Color(i);
        int j = color.getRed();
        int k = color.getGreen();
        int l = color.getBlue();
        int i1 = (j + k + l) / 3;
        return i1;
    }

    public int getWidth()
    {
        return Width;
    }

    public int getHeight()
    {
        return Height;
    }

    public boolean isPixel(int i, int j)
    {
        return getRGB(i, j) != -1;
    }

    public static void main(String args[])
    {
        System.out.println("Sorry, this class cannot be tested on its own. Try fdpanel.class.");
    }

    public int Mask(int i, int j, int k)
    {
        short word0 = 0;
        int l = i + DX[k];
        int i1 = j + DY[k];
        if(l < 0)
            word0 = 0;
        else
        if(i1 < 0)
            word0 = 0;
        else
        if(l >= Width)
            word0 = 0;
        else
        if(i1 >= Height)
            word0 = 0;
        else
        if(!isPixel(l, i1))
            word0 = 0;
        else
            word0 = mask[k];
        return word0;
    }

    public void RasMod(int i, int j, int k)
    {
        if(k > 0)
        {
            if(i > 0 && i < Width - 1 && j > 0 && j < Height - 1)
            {
                neighbours[i - 1][j - 1] |= 4;
                neighbours[i][j - 1] |= 8;
                neighbours[i + 1][j - 1] |= 0x10;
                neighbours[i - 1][j] |= 2;
                neighbours[i + 1][j] |= 0x20;
                neighbours[i - 1][j + 1] |= 1;
                neighbours[i][j + 1] |= 0x80;
                neighbours[i + 1][j + 1] |= 0x40;
            } else
            {
                if(i != 0 && j != 0)
                    neighbours[i - 1][j - 1] |= 4;
                if(j != 0)
                    neighbours[i][j - 1] |= 8;
                if(i < Width - 1 && j != 0)
                    neighbours[i + 1][j - 1] |= 0x10;
                if(i != 0)
                    neighbours[i - 1][j] |= 2;
                if(i < Width - 1)
                    neighbours[i + 1][j] |= 0x20;
                if(j < Height - 1 && i != 0)
                    neighbours[i - 1][j + 1] |= 1;
                if(j < Height - 1)
                    neighbours[i][j + 1] |= 0x80;
                if(j < Height - 1 && i < Width - 1)
                    neighbours[i + 1][j + 1] |= 0x40;
            }
        } else
        if(i > 0 && i < Width - 1 && j > 0 && j < Height - 1)
        {
            neighbours[i - 1][j - 1] &= 0xfffb;
            neighbours[i][j - 1] &= 0xfff7;
            neighbours[i + 1][j - 1] &= 0xffef;
            neighbours[i - 1][j] &= 0xfffd;
            neighbours[i + 1][j] &= 0xffdf;
            neighbours[i - 1][j + 1] &= 0xfffe;
            neighbours[i][j + 1] &= 0xff7f;
            neighbours[i + 1][j + 1] &= 0xffbf;
        } else
        {
            if(i != 0 && j != 0)
                neighbours[i - 1][j - 1] &= 0xfffb;
            if(j != 0)
                neighbours[i][j - 1] &= 0xfff7;
            if(i < Width - 1 && j != 0)
                neighbours[i + 1][j - 1] &= 0xffef;
            if(i != 0)
                neighbours[i - 1][j] &= 0xfffd;
            if(i < Width - 1)
                neighbours[i + 1][j] &= 0xffdf;
            if(j < Height - 1 && i != 0)
                neighbours[i - 1][j + 1] &= 0xfffe;
            if(j < Height - 1)
                neighbours[i][j + 1] &= 0xff7f;
            if(j < Height - 1 && i < Width - 1)
                neighbours[i + 1][j + 1] &= 0xffbf;
        }
    }

    public void RasOp(int i)
    {
        int i1 = 1;
        boolean flag;
        do
        {
            int j1 = i1 <= 0 ? Width - 1 : 0;
            int k1 = i1 <= 0 ? Height - 1 : 0;
            int l1 = i1 <= 0 ? -1 : Width;
            int i2 = i1 <= 0 ? -1 : Height;
            flag = false;
            for(int l = k1; l != i2; l += i1)
            {
                for(int k = j1; k != l1; k += i1)
                    switch(i)
                    {
                    default:
                        break;

                    case 0: // '\0'
                        if(neighbours[k][l] < 256 && Fill_Table[neighbours[k][l]] != 0)
                        {
                            neighbours[k][l] += 256;
                            RasMod(k, l, 1);
                            flag = true;
                        }
                        break;

                    case 1: // '\001'
                        if(neighbours[k][l] > 255 && Thin1_Table[neighbours[k][l] & 0xff] != 0)
                        {
                            neighbours[k][l] &= 0xff;
                            RasMod(k, l, -1);
                            flag = true;
                        }
                        break;

                    case 2: // '\002'
                        if(neighbours[k][l] > 255 && Thin2_Table[neighbours[k][l] & 0xff] != 0)
                        {
                            neighbours[k][l] &= 0xff;
                            RasMod(k, l, -1);
                            flag = true;
                        }
                        break;

                    case 3: // '\003'
                        if((neighbours[k][l] & 0x100) == 0)
                            break;
                        if(CNode_Table[neighbours[k][l] & 0xff] != 0)
                        {
                            neighbours[k][l] |= 0x400;
                            break;
                        }
                        if(TNode_Table[neighbours[k][l] & 0xff] != 0)
                            neighbours[k][l] |= 0x200;
                        break;

                    case 6: // '\006'
                        if((neighbours[k][l] & 0x100) == 0 || TNode_Table[neighbours[k][l] & 0xff] == 0);
                        break;

                    case 4: // '\004'
                        if((neighbours[k][l] & 0x400) == 0)
                            break;
                        for(int j = 1; j < 8; j += 2)
                            if((neighbours[k + DX[j]][l + DY[j]] & 0x700) == 256 && (neighbours[k + DX[j]][l + DY[j]] & severmask[j]) != 0)
                                Sever(k, l, j);

                        break;

                    case 5: // '\005'
                        while((neighbours[k][l] & 0x600) != 0) 
                        {
                            TraceLine(k, l);
                            flag = true;
                        }
                        break;
                    }

            }

            i1 = -i1;
        } while(flag);
    }

    public void Sever(int i, int j, int k)
    {
        int l = k - 1;
        int i1 = k != 7 ? k + 1 : 0;
        int j1 = k != 7 ? k + 2 : 1;
        int k1 = k != 1 ? k - 2 : 7;
        int l1 = j1;
        int i2 = k <= 4 ? k + 3 : k - 5;
        if((neighbours[i + DX[l]][j + DY[l]] & 0x700) == 256)
        {
            neighbours[i + DX[l]][j + DY[l]] &= ~revmask[k1];
            neighbours[i + DX[k]][j + DY[k]] &= ~mask[k1];
        }
        if((neighbours[i + DX[i1]][j + DY[i1]] & 0x700) == 256)
        {
            neighbours[i + DX[i1]][j + DY[i1]] &= ~revmask[l1];
            neighbours[i + DX[k]][j + DY[k]] &= ~mask[l1];
        }
        if((neighbours[i + DX[j1]][j + DY[j1]] & 0x700) == 256)
        {
            neighbours[i + DX[j1]][j + DY[j1]] &= ~revmask[i2];
            neighbours[i + DX[k]][j + DY[k]] &= ~mask[i2];
        }
    }

    public void showNodes(int i, int j, int k, int l)
    {
        for(int i1 = j; i1 < l; i1++)
        {
            for(int j1 = i; j1 < k; j1++)
            {
                char c = '0';
                int k1 = neighbours[j1][i1] & 0x600;
                if(k1 == 1024)
                    c = 'c';
                else
                if(k1 == 512)
                    c = 't';
                else
                if(k1 == 1536)
                    c = 'n';
                System.out.print(c);
            }

            System.out.println();
        }

    }

    public void showRaster(int i, int j, int k, int l)
    {
        for(int i1 = j; i1 < l; i1++)
        {
            for(int j1 = i; j1 < k; j1++)
                if(isPixel(j1, i1))
                    System.out.print("1");
                else
                    System.out.print("0");

            System.out.println();
        }

    }

    public void showNeigh(int i, int j, int k, int l)
    {
        for(int i1 = j; i1 < l; i1++)
        {
            for(int j1 = i; j1 < k; j1++)
                if(neighbours[j1][i1] > 255)
                    System.out.print("1");
                else
                    System.out.print("0");

            System.out.println();
        }

    }

    public void showNeighbours(int i, int j, int k, int l)
    {
        for(int i1 = j; i1 < l; i1++)
        {
            for(int j1 = i; j1 < k; j1++)
                System.out.print(neighbours[j1][i1] + " ");

            System.out.println();
        }

    }

    public void test()
    {
        int i = Width / 2;
        int j = Height / 2;
        int k;
        int l;
        if(Width > 15)
        {
            k = i - 3;
            l = i + 3;
        } else
        {
            k = 0;
            l = Width;
        }
        int i1;
        int j1;
        if(Height > 15)
        {
            i1 = j - 3;
            j1 = j + 3;
        } else
        {
            i1 = 0;
            j1 = Height;
        }
        System.out.println();
        System.out.println("Testing fdimage class...");
        fdline.Deconstruct();
        EncodeNeighbours();
        showRaster(k, i1, l, j1);
        showNeighbours(k, i1, l, j1);
        System.out.println("Remove a pixel");
        setRGB(i, j, -1);
        neighbours[i][j] &= 0xff;
        RasMod(i, j, -1);
        showRaster(k, i1, l, j1);
        System.out.println("Add a pixel");
        setRGB(i, j, 0);
        neighbours[i][j] |= 0x100;
        RasMod(i, j, 1);
        showRaster(k, i1, l, j1);
        System.out.println("RasOp (FILL)");
        RasOp(0);
        showNeigh(k, i1, l, j1);
        System.out.println("RasOp (THIN1)");
        RasOp(1);
        showNeigh(k, i1, l, j1);
        System.out.println("RasOp (THIN2)");
        RasOp(2);
        showNeigh(k, i1, l, j1);
        showNeighbours(k, i1, l, j1);
        System.out.println("RasOp (NODES)");
        RasOp(3);
        showNodes(k, i1, l, j1);
        System.out.println("RasOp (SEVER)");
        RasOp(4);
        showNeigh(k, i1, l, j1);
        System.out.println("RasOp (LINE)");
        RasOp(5);
        showNeigh(k, i1, l, j1);
        fdline fdline1 = fdline.First();
        System.out.println("List of the lines:");
        for(; fdline1 != null; fdline1 = fdline1.Next())
            System.out.println(fdline1.toString());

    }

    public void Threshold()
    {
        double d = 0.0D;
        for(int i = 0; i < Width; i++)
        {
            for(int j = 0; j < Height; j++)
                d += getBrightness(getRGB(i, j));

        }

        System.out.println("SUM: " + d);
        System.out.println("DIV: " + Width * Height);
        double d1 = d / (double)(Width * Height);
        System.out.println("MEAN " + d1);
        if(Verbose)
            System.out.println("Image is " + Width + " x " + Height + ": sum = " + d + ", mean = " + d1 + ", back = " + getRGB(0, 0));
        int k = 0;
        int l = 0;
        for(int i1 = 0; i1 < Width; i1++)
        {
            for(int j1 = 0; j1 < Height; j1++)
                if(getBrightness(getRGB(i1, j1)) < 127)
                {
                    Color color = Color.black;
                    setRGB(i1, j1, color.getRGB());
                    k++;
                } else
                {
                    Color color1 = Color.white;
                    setRGB(i1, j1, color1.getRGB());
                    l++;
                }

        }

        System.out.println("CHANGED TO FG: " + k);
        System.out.println("CHANGED TO BG: " + l);
        System.out.println("TOTAL PIXELS : " + Width * Height);
    }

    public void TraceLine(int i, int j)
    {
        int j1 = i;
        int k1 = j;
        fdpoint fdpoint1 = new fdpoint(i, j);
        fdpoint fdpoint2 = new fdpoint(0, 0);
        if((neighbours[i][j] & 0xff) == 0)
        {
            neighbours[i][j] = 0;
            fdline fdline1 = new fdline(fdpoint1, fdpoint1);
            fdline1.Insert();
            return;
        }
        int i1;
        do
        {
            if(neighbours[j1][k1] < 256)
            {
                System.err.println("Found zero");
                System.exit(0);
            }
            int l = neighbours[j1][k1] & 0xff;
            int k = 0;
            i1 = -1;
            while(l != 0) 
            {
                while((l & 1) == 0) 
                {
                    l /= 2;
                    k++;
                }
                if(i1 < 0)
                    i1 = k;
                else
                if(neighbours[j1 + DX[k]][k1 + DY[k]] > neighbours[j1 + DX[i1]][k1 + DY[i1]])
                    i1 = k;
                l /= 2;
                k++;
            }
            if(i1 < 0)
                i1 = k;
            if((neighbours[j1][k1] & 0x400) != 0)
            {
                neighbours[j1 + DX[i1]][k1 + DY[i1]] &= ~revmask[i1];
            } else
            {
                RasMod(j1, k1, -1);
                neighbours[j1][k1] &= 0xff;
            }
            if(i1 < 0)
            {
                System.err.println("We have n[" + j1 + "][" + k1 + "] = " + neighbours[j1][k1]);
                System.exit(0);
            }
            j1 += DX[i1];
            k1 += DY[i1];
        } while((neighbours[j1][k1] & 0x700) == 256);
        if((neighbours[j1][k1] & 0x400) != 0)
        {
            neighbours[j1 - DX[i1]][k1 - DY[i1]] &= ~mask[i1];
            if(TNode_Table[neighbours[j1][k1] & 0xff] != 0)
            {
                neighbours[j1][k1] |= 0x200;
                neighbours[j1][k1] &= 0xfbff;
            }
        } else
        {
            RasMod(j1, k1, -1);
            neighbours[j1][k1] &= 0xff;
        }
        if((neighbours[i][j] & 0x400) != 0 && TNode_Table[neighbours[i][j] & 0xff] != 0)
        {
            neighbours[i][j] |= 0x200;
            neighbours[i][j] &= 0xfbff;
        }
        fdpoint2.setpoint(j1, k1);
        fdline fdline2 = new fdline(fdpoint1, fdpoint2);
        fdline2.Insert();
        neighbours[i][j] &= 0xf9ff;
    }

    public void Vectorise(fdline fdline1)
    {
        fdline.Deconstruct();
        EncodeNeighbours();
        RasOp(0);
        RasOp(1);
        RasOp(2);
        RasOp(3);
        RasOp(4);
        RasOp(5);
    }

    private int Height;
    private int Width;
    private short neighbours[][];
    private int tnode_counter;
    private fdpoint pnode[];
    private boolean Verbose;
    private static final int Foreground = 0;
    private static final int Background = -1;
    private static final String dir[] = {
        "topright", "right", "botright", "bottom", "botleft", "left", "topleft", "top"
    };
    private static final int DX[] = {
        1, 1, 1, 0, -1, -1, -1, 0
    };
    private static final int DY[] = {
        -1, 0, 1, 1, 1, 0, -1, -1
    };
    private static final short mask[] = {
        1, 2, 4, 8, 16, 32, 64, 128
    };
    private static final short revmask[] = {
        16, 32, 64, 128, 1, 2, 4, 8
    };
    private static final short severmask[] = {
        40, 248, 160, 99, 130, 141, 10, 54
    };
    private static final short Fill_Table[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 
        1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 
        0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1
    };
    private static final short Thin1_Table[] = {
        1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 
        0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 
        1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 
        1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
        1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 
        0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 
        0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 
        0, 0, 0, 0, 0, 0
    };
    private static final short Thin2_Table[] = {
        1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 
        1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 
        1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 
        1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 
        1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 
        1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 
        1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 
        1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 
        0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 
        0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 
        0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 
        0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 
        1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 
        1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 
        0, 0, 1, 0, 0, 0
    };
    private static final short CNode_Table[] = {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 
        0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 
        0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 
        0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 
        0, 0, 0, 1, 1, 1, 0, 1, 1, 1, 
        0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 
        1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 
        0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 
        0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 
        0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 
        0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 
        1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 
        0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 
        1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 
        1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 
        1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
        1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 
        0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 
        0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 
        1, 0, 0, 0, 0, 0
    };
    private static final short TNode_Table[] = {
        1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 
        0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 
        0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 
        0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0
    };
    private static final short NotNode_Table[] = {
        0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 
        1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 
        1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 
        1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 
        1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 
        1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 
        1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 
        1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 
        1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 
        0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 
        1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 
        1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 
        1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 
        1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 
        1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 
        0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 
        1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 
        0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 
        0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 
        0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 
        1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 
        0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 
        1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 
        1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 
        1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 
        0, 1, 1, 1, 1, 1
    };
    private static final int FILL = 0;
    private static final int THIN1 = 1;
    private static final int THIN2 = 2;
    private static final int NODES = 3;
    private static final int SEVER = 4;
    private static final int LINE = 5;
    private static final int TNODES = 6;
    private static final int ON = 256;
    private static final int TNODE = 512;
    private static final int CNODE = 1024;
    private static final int NODE = 1536;
    private static final int INFO = 1792;

}


