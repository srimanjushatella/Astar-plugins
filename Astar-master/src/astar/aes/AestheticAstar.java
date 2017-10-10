/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package astar.aes;

import astar.pcg.Wells;
import astar.util.Node;
import astar.Astar;
import astar.aes.fractal.BoxCountingMethod;
import astar.aes.fractal.fdimage;
import astar.aes.fractal.fdresult;
import astar.aes.rs.RsPlot;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
/*import java.util.Vector;*/

import javax.imageio.ImageIO;

import jsc.correlation.KendallCorrelation;
import jsc.datastructures.PairedData;
import jsc.descriptive.MeanVar;
import jsc.independentsamples.SmirnovTest;

public class  AestheticAstar {
    public enum Heuristic {
        EUCLIDEAN,
        MANHATTAN,
        CHECKERS,
        SSE
    }
    
//    public enum Objective {
//        BASIC,
//        PRETTY,
//        STEALTHY
//    };
	/** Destination symbol */
	public final static char SYM_BASIC_DEST = 'd';

	/** Source symbol */
	public final static char SYM_BASIC_SRC = 's';

	/** Obstacle symbol */
	public final static char SYM_OBSTACLE = '#';

	/** Free (open) symbol */
	public final static char SYM_FREE = '.';
	
	public final static char SYM_BUG = '?';

	public final static char SYM_FULL_PATH = '+';

	public final static char SYM_FULL_DEST = 'D';

	public final static char SYM_FULL_SRC = 'S';

	public final static char SYM_BASIC_PATH = '=';

	private char[][] tileMap;

	private int width;

	private int height;
	
	private Node fullPath;

	private Node basicPath;

	private Node basicDest;

	private Node basicSrc;

	private BufferedReader reader;

	private Node fullSrc;

	private Node fullDest;

	private Vector<Node> path;
	
	private double r2;

	// Offsets relative to current position in map
	private int[][] xyOffsets = {
			{ -1, 0 }, // W
			{ -1, -1 }, // NW
			{ 0, -1 }, // N
			{ 1, -1 }, // NE
			{ 1, 0 }, // E
			{ 1, 1 }, // SE
			{ 0, 1 }, // S
			{ -1, 1 } }; // SW

	public static int boxes[] = { 2, 3, 4, 6, 8, 12, 16, 32, 64, 128 };
	//public static int boxes[] = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 16, 32, 64, 128 };
	//public static int boxes[] = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 64};

	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("usage: java AestheticPath [s|a|e] n r2");
			System.exit(1);
		}
		
		String type = args[0];
//		
//		Objective objective = Objective.NONE;
//		
//		if(type.equals("a"))
//			objective = Objective.STANDARD;
//                
//		else if(type.equals("e"))
//			objective = Objective.STEALTHY;
//                
//		else if(type.equals("s"))
//			objective = Objective.STANDARD;
//		else {
//			System.err.println("bad objective");
//			System.exit(1);
//		}
		
		int seed = Integer.parseInt(args[1]);
		Tools.setRandomizer(seed);
		
		boolean imageFlag = true;
		
		// Generate a 50x50 world
		Wells lg = new Wells();
		
		char[][] tileMap = lg.generateLevel(10);

		int startX = lg.getPlayerStartX();
		int startY = lg.getPlayerStartY();
		int endX = lg.getGatewayX();
		int endY = lg.getGatewayY();
		
		Astar astar = new astar.Astar(tileMap,startX,startY,endX,endY);

//		astar.util.Node path = astar.find(objective);
                astar.util.Node path = astar.find();
		
		AestheticAstar aes = new AestheticAstar(tileMap);
		
		aes.setR2(Double.parseDouble(args[2]));
		
                // TODO: build this into "tweak" of the pretty model
//		if(objective == Objective.PRETTY) {
//			//aes.straighten(path);
//			aes.straightLong(path);
//		}
		
		Node anode = path;
		
		int len = 0;
		
		while(anode != null) {
			len++;
			int x = anode.getCol();
			int y = anode.getRow();
			
			tileMap[y][x] = AestheticAstar.SYM_FULL_PATH;
			
			anode = anode.getParent();
		}

		//System.out.println("len = "+len);
	
		double ugs = aes.getUglyIndex(path);
		double hugs = aes.getHugsIndex(path);
		double zags = aes.getZagsIndex(path);
		//System.out.println("hugs = "+aes.getHugsIndex(path));
		//System.out.println("zags = "+aes.getZagsIndex(path));
		//System.out.println("ugly = "+ugs);		
		
		double bi = (double) (len - ugs) / (double) len;
		
		fdimage fdi = new fdimage(path,tileMap,SYM_OBSTACLE);
//		
//		if(imageFlag)
//			try {
//				String percent = "";
//				
//				if(type.equals("e"))
//					percent = "_p15";
//				
//				ImageIO.write(fdi,"png",new File("c:/Marist/research/IJCGT/img/"+type+"/fdi_"+seed+percent+".png"));			
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
//		
		BoxCountingMethod.setBoxOptions(boxes, false, -10, 10, 1);
		
		fdresult fdr = new fdresult(false);
		
		BoxCountingMethod.BoxCountingMethod(fdr, fdi);
//		
//		//fdr.showResult();
//		fdr.Save("test_"+type+".dat");
		double slope = fdr.CalcLogSlope();
//		System.out.println(slope);
		System.out.println(type+" len= "+len+" hugs= "+hugs+" zags= "+zags+" ugly= "+ugs+" bi= "+bi+" FD= "+slope);
//		aes.perfect(path,len);
//		
//		aes.writeImage(path,tileMap,"c:/tmp/after_"+type+"_"+seed+".png",true);
			
//		aes.dump7(path,len);

	}
	
	public void dump7(Node path, int lenOriginal) {
		double snr = 0;
		
		Node anode = path;

		Vector<Double> data = new Vector<Double>();
			
		int goalx = anode.getCol();
		int goaly = anode.getRow();
		
		// (25,25) -- uncomment
//		int goalx = 25;
//		int goaly = 25;
		
		// (25,25) -- comment
		anode = anode.getParent();
		
		while (anode != null) {
			// To skip start node, uncomment
//			if(anode.getParent() == null)
//				break;
			double x = anode.getCol();
			double y = anode.getRow();
			
//			System.out.println("("+x+" "+y+")");
			
			double dist = Math.abs(x-goalx) + Math.abs(y-goaly);
//			double dist = x + y;
//			double dist = Math.sqrt((x-goalx)*(x-goalx)+(y-goaly)*(y-goaly));
//			double dist = (y / x) * (y / x);
			
			data.add(dist);
			
//			System.out.println(xs[index]+" "+ys[index]);

			anode = anode.getParent();
		}
		
		Vector<Double> data4 = replicate(data);
		
		Vector<Double> fd = firstDerivative(data);
		
		int len = fd.size();

		Random ran = new Random(5);

		Vector<Double> data2 = new Vector<Double>( );
		Vector<Double> data3 = new Vector<Double>( );
		
		/*while(data.size() < 128)*/ {
			int extLen = pow2Max(len);

			int filler = extLen - len;
			Vector<Integer> hash = fillHash(len, filler);

			double b = 0;
			// j=1 to len-1 means we don't include start and goal
			for (int j = 0; j < len; j++) {
				
				int addPts = hash.elementAt(j) + 1;
				

				for (int k = 0; k < addPts; k++) {
//					double r = dist * ran.nextDouble() + ran.nextGaussian() ;
					double d = fd.elementAt(k);

					double r = d * (ran.nextGaussian()+0.125) - /*Math.abs*/(ran.nextDouble()) * 4.0 + b;
					
					// (25,25) -- uncomment
//					r = d * (ran.nextGaussian()+0.125) + ran.nextDouble() * 8 + a;

					
					double v = r;

					data2.add(v);
					
					if(k == 0)
						data3.add(v);

					b += ran.nextGaussian() * snr;
					
					// (25,25) -- uncomment
//					a += ran.nextGaussian() / 5;
					
//					System.out.println(v);
				}

			}
		}
		
		// Reduce data to required power of 2 -- otherwise Selfis gives
		// weird results
		int sz2 = data2.size();
		int sz3 = data3.size();
		
		double ks = testStationarity(data2);
//		double cor = testCorrelation(data,data3);
		double var = testVariance(data4);

		int reqSize = sz2;

		// if size not power of two, then set minimum power two
		if (!isPow2(reqSize))
			reqSize = pow2Max(sz2) >> 1;

		for (int ind = sz2 - 1; ind >= reqSize; ind--) {
			data2.removeElementAt(ind);
		}

		assert (data2.size() == reqSize);		
		
		RsPlot rs = new RsPlot();
		rs.estimate(data2);
		double a[] = rs.calcSlope();

		double h = Math.abs(a[0]);
		double p = a[5];

		System.out.println(h+" "+ p +" " + ks + " " + var);
//		System.out.println(h);
	}	
	
	public Vector<Double> firstDerivative(Vector<Double> data) {
//		Vector<Double> fd = new Vector<Double>( );
//		
//		for(int i=1; i < data.size(); i++) {
//			double d0 = data.elementAt(i-1);
//			double d1 = data.elementAt(i);
//			
//			double diff = Math.log(d0) - Math.log(d1);
//			
//			fd.add(diff);
//		}
//		return fd;
		return data;
	}
	
	public double testVariance(Vector<Double> data) {
		double sample[] = new double[data.size()];
		for(int i=0; i < data.size(); i++) {
			sample[i] = data.elementAt(i);
		}
		
		MeanVar mv = new MeanVar(sample);
		
		
		return mv.getMean();
	}
	
	public double testCorrelation(Vector<Double> data1,Vector<Double> data2) {
		double sample1[] = new double[data1.size()];
		double sample2[] = new double[data2.size()];
		for(int i=0; i < data1.size(); i++) {
			sample1[i] = data1.elementAt(i);
			sample2[i] = data2.elementAt(i);
			
		}
		
		PairedData pd = new PairedData(sample1,sample2);
		
		KendallCorrelation kc = new KendallCorrelation(pd);
		
		double r = kc.getR();
		
		return r;
	}
	
	private double testStationarity(Vector<Double> data) {
		double sample1[] = new double[data.size()/2];
		double sample2[] = new double[data.size()/2];
		for(int i=0; i < data.size()/2; i++)
			sample1[i] = data.elementAt(i);
		
		for(int i=0; i < data.size()/2; i++)
			sample2[i] = data.elementAt(i + data.size()/2);
		
		SmirnovTest st = new SmirnovTest(sample1,sample2);
		
		double p = st.getSP();
		
		return p;
		
	}
	
	public void dump6(Node path, int len) {
		Node anode = path;
		int index = 0;
		int[] xs = new int[len];
		int[] ys = new int[len];
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;

		while (anode != null) {
			xs[index] = anode.getCol();
			ys[index] = anode.getRow();
			
//			System.out.println(xs[index]+" "+ys[index]);
			if (xs[index] < minx)
				minx = xs[index];

			if (ys[index] < miny)
				miny = ys[index];

			anode = anode.getParent();

			index++;
		}

		Vector<Double> data = new Vector<Double>();

		Random ran = new Random(5);

		int shardSize = pow2Max(len);
		/*while(data.size() < 128)*/ {
			int extLen = pow2Max(len - 2);

			int filler = extLen - (len - 2);
			Vector<Integer> hash = fillHash(len - 2, filler);

			// j=1 to len-1 means we don't include start and goal
			for (int j = 1; j < len - 1; j++) {
				double dist = Math.abs(xs[j] - xs[0]) * Math.abs(ys[j] - ys[0]);
				
				int addPts = hash.elementAt(j - 1) + 1;
				for (int k = 0; k < addPts; k++) {
//					double r = dist * ran.nextGaussian() + ran.nextDouble()*10;
					double r = dist * ran.nextDouble() *10 + ran.nextGaussian();

					double v = r;

					data.add(v);

					System.out.println(v);
				}

			}


		}
		
		// Reduce data to required power of 2 -- otherwise Selfis gives
		// weird results
		int sz = data.size();
		
//		double ks = testStationarity(data);

		int reqSize = sz;

		// if size not power of two, then set minimum power two
		if (!isPow2(reqSize))
			reqSize = pow2Max(sz) >> 1;

		for (int ind = sz - 1; ind >= reqSize; ind--) {
			data.removeElementAt(ind);
		}

		assert (data.size() == reqSize);		
		
		RsPlot rs = new RsPlot();
		rs.estimate(data);
		double a[] = rs.calcSlope();

		double h = Math.abs(a[0]);
		double p = a[5];

		System.out.println(h);
	}	

	
	public static <T> Vector<T> replicate(Vector<T> samp) {
		Vector<T> replica = new Vector<T>();
		
		for(int i=0; i < samp.size(); i++)
			replica.add(samp.elementAt(i));
		
		return replica;
	}
	
	private static void shuffle(Vector<Double> samp) {
		Random ran = new Random(10);
		
		for (int i = 0; i < samp.size(); i++) {
			double tmp = samp.elementAt(i);
			
			int lottery = ran.nextInt(samp.size());			

			double d = samp.elementAt(lottery);

			samp.set(i, d);

			samp.set(lottery, tmp);

		}
	}
	
	public AestheticAstar(String name) {
		try {
			reader = new BufferedReader(new FileReader(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public AestheticAstar(char[][] tileMap) {
		this.tileMap = tileMap;
	}
	
	public void setR2(double r2) {
		this.r2 = r2;
	}
	
	/**
	 * Perfects a path.
	 * @param path
	 * @param len
	 */
	public void perfect(Node path, int len) {
		Node anode = path;
		int index = 0;

		while (anode != null) {
			
			System.out.print(index+" "+anode.getCol()+" "+anode.getRow());
			
			if (this.hugsWall(anode) || this.hugsWallCorner(anode)) {
				System.out.println(" hugs");
				eliminateTracking(anode);
			}
			else
				System.out.println("");

			anode = anode.getParent();
			
			index++;
		}
	}
	
	/**
	 * Removes tracking.
	 * @param anode The goal node
	 */
	protected void eliminateTracking(Node anode) {
		int x = anode.getCol();
		int y = anode.getRow();
		
		Node node1 = null, node2 = null;
		
		// If wall on left, move right
		if(isObstacle(x-1,y))
			anode.setCol(x+1);
		
		// if wall on right, move left
		else if(isObstacle(x+1,y))
			anode.setCol(x-1);
		
		// if wall above, move down
		else if(isObstacle(x,y-1))
			anode.setRow(y+1);
		
		// if wall below, move up
		else if(isObstacle(x,y+1))
			anode.setRow(y-1);
		
		// if wall SE, insert two nodes NW

		else if(isObstacle(x+1,y+1)) {
			node1 = new Node(x,y-1);
			node2 = new Node(x-1,y);
			cutOut(anode,node1,node2);
		}
		
		// if wall SW, insert two node NE
		else if(isObstacle(x-1,y+1)) {
			node1 = new Node(x+1,y);
			node2 = new Node(x,y-1);
			cutOut(anode,node1,node2);
		}
		
		// if wall NE, insert two nodes SW
		else if(isObstacle(x+1,y-1)) {
			node1 = new Node(x-1,y);
			node2 = new Node(x,y+1);
			cutOut(anode,node1,node2);
		}
		
		// if wall NW, insert two node SE 
		else if(isObstacle(x-1,y-1)) {
			node1 = new Node(x+1,y);
			node2 = new Node(x,y+1);
			cutOut(anode,node1,node2);
		}
			

		
		else
			System.err.println("Error at node "+anode);
	}
	
	protected void cutOut(Node curNode,Node node1,Node node2) {
		node1.setParent(node2);
		node2.setParent(curNode.getParent());
		curNode.getChild().setParent(node1);
		node1.setChild(curNode.getChild());	
	}
	
	public void dump5(Node path, int len) {
		Node anode = path;
		int index = 0;
		int[] xs = new int[len];
		int[] ys = new int[len];
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;

		while (anode != null) {
			xs[index] = anode.getCol();
			ys[index] = anode.getRow();

			if (xs[index] < minx)
				minx = xs[index];

			if (ys[index] < miny)
				miny = ys[index];

			anode = anode.getParent();

			index++;
		}

		Vector<Double> data = new Vector<Double>();

		Random ran = new Random(5);

		while(data.size() < 1024) {
			int extLen = pow2Max(len - 2);

			int filler = extLen - (len - 2);
			Vector<Integer> hash = fillHash(len - 2, filler);

			// double addPts = ADD_PTS;

			double step = (2.0 * Math.PI) / (len + filler) / 2;

			double theta = 0;

			// double delta = 0.0;
			double bump = 0.0;

			for (int j = 1; j < len - 1; j++) {

				double delta = 0;
				// Use the square of the slope change
//				if (xs[j - 1] - xs[j] != xs[j] - xs[j + 1])
//					delta += 1;
//
//				if (ys[j - 1] - ys[j] != ys[j] - ys[j + 1])
//					delta += 1;

				 if(xs[j-1]-xs[j] > xs[j]-xs[j+1])
				 delta++;
							
				 else if(xs[j-1]-xs[j] < xs[j]-xs[j+1])
				 delta--;
						
				 if(ys[j-1]-ys[j] > ys[j]-ys[j+1])
				 delta++;
							
				 else if(ys[j-1]-ys[j] < ys[j]-ys[j+1])
				 delta--;

				// Use the relative slope change
				if (this.hugsWall(xs[j], ys[j])) {
					if(delta != 0)
						delta *= 2;
					else
						delta += 1;
				}
											
//				delta = 0;
				delta += ran.nextGaussian() * r2;
				
				int addPts = hash.elementAt(j - 1) + 1;
				for (int k = 0; k < addPts; k++) {
					double r = delta * (Math.abs(ran.nextInt()) % 10);
//					double r = delta * (Math.abs(ran.nextDouble()));
//					double r = delta;

					double v = r;

					data.add(v);

//					System.out.println(v);
				}

			}


		}
		
		// Reduce data to required power of 2 -- otherwise Selfis gives
		// weird results
		int sz = data.size();

		int reqSize = sz;

		// if size not power of two, then set minimum power two
		if (!isPow2(reqSize))
			reqSize = pow2Max(sz) >> 1;

		for (int ind = sz - 1; ind >= reqSize; ind--) {
			data.removeElementAt(ind);
		}

		assert (data.size() == reqSize);		
		
		RsPlot rs = new RsPlot();
		rs.estimate(data);
		double a[] = rs.calcSlope();

		double h = Math.abs(a[0]);
		double p = a[5];

		System.out.println(h + " " + p + " " +data.size() + " " + len);
	}	
	
	public Vector<Integer> fillHash(int len,int fillNum) {
		Vector<Integer> hash = new  Vector<Integer>();

		for(int j=0; j < len; j++)
			hash.addElement(new Integer(0));
		
		Random ran = new Random(5);
		
		int cnt = 0;
		while(cnt < fillNum) {
			int index = (int) (len * Math.abs(ran.nextDouble()));
			
			Integer sum = hash.elementAt(index);
		
			sum++;
			
			hash.setElementAt(sum, index);
			
			cnt++;
		}
		return hash;
	}
	
	public int pow2Max(int v) {
		int pow2 = 1;
		while(v > pow2) {
			pow2 = pow2 << 1;
		}
		
		return pow2;		
	}
	
	public boolean isPow2(int v) {
		int pow2 = pow2Max(v);
		
		return pow2 == v;
	}
	
	// 74 / 100
	public void dump2(Node path,int len) {
		Node anode = path;
		int index = 0;
		int[] xs = new int[len];
		int[] ys = new int[len];
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		
		while(anode != null) {
			xs[index] = anode.getCol();
			ys[index] = anode.getRow();
			
			if(xs[index] <  minx)
				minx = xs[index];
			
			if(ys[index] < miny)
				miny = ys[index];
			
			anode = anode.getParent();
			
			index++;
		}
		
		Vector<Double> data = new Vector<Double>();
		
		Random ran = new Random(5);
		
		int slope_sum = 0;
		
		double ADD_PTS = 1;		
		
		double step = (2.0 * Math.PI) / len / ADD_PTS / 15;
		
		double amplitude = 10;

		double theta = 0;
		
//		double delta = 0.0;
		
		for(int j=1; j < len-1; j++) {

//			int slope_sum = 0;
			// Use the square of the slope change
//			if(xs[j-1]-xs[j] != xs[j]-xs[j+1])
//				delta += 1;
//			
//			if(ys[j-1]-ys[j] != ys[j]-ys[j+1])
//				delta += 1;
			
			double delta = 0;
			
			// Use the relative slope change
			if(xs[j-1]-xs[j] > xs[j]-xs[j+1])
				delta++;
			
			else if(xs[j-1]-xs[j] < xs[j]-xs[j+1])
				delta--;
		
			if(ys[j-1]-ys[j] > ys[j]-ys[j+1])
				delta++;
			
			else if(ys[j-1]-ys[j] < ys[j]-ys[j+1])
				delta--;
		
			for(int k=0; k < ADD_PTS; k++) {
				double r = delta == 0 ? 0 : (amplitude * (ran.nextInt() % 100)/100.0);
							
				theta += step;
				
				double v = amplitude * Math.sin(theta) + r;
				
				data.add(v);
				
				System.out.println(v);
			}

		}
		
		RsPlot rs = new RsPlot();
		rs.estimate(data);
//		double a[] = rs.calcSlope();
//		System.out.println(a[0]+" "+data.size()+" "+slope_sum);
//		System.out.println(a[0]+" "+data.size());
	}
	
	// 81 / 100
	public void dump1(Node path,int len) {
		Node anode = path;
		int index = 0;
		int[] xs = new int[len];
		int[] ys = new int[len];
		int minx = Integer.MAX_VALUE;
		int miny = Integer.MAX_VALUE;
		
		while(anode != null) {
			xs[index] = anode.getCol();
			ys[index] = anode.getRow();
			
			if(xs[index] <  minx)
				minx = xs[index];
			
			if(ys[index] < miny)
				miny = ys[index];
			
			anode = anode.getParent();
			
			index++;
		}
		
		Vector<Double> data = new Vector<Double>();
		
		Random ran = new Random(5);
		
		int slope_sum = 0;
		
		double ADD_PTS = 2;		
		
		double step = (2.0 * Math.PI) / len / ADD_PTS;
		
		double amplitude = 10;

		double theta = 0;
		
//		double delta = 0.0;
		
		for(int j=1; j < len-1; j++) {

//			int slope_sum = 0;
			// Use the square of the slope change
//			if(xs[j-1]-xs[j] != xs[j]-xs[j+1])
//				delta += 1;
//			
//			if(ys[j-1]-ys[j] != ys[j]-ys[j+1])
//				delta += 1;
			
			double delta = 0;
			
			// Use the relative slope change
			if(xs[j-1]-xs[j] > xs[j]-xs[j+1])
				delta++;
			
			else if(xs[j-1]-xs[j] < xs[j]-xs[j+1])
				delta--;
		
			if(ys[j-1]-ys[j] > ys[j]-ys[j+1])
				delta++;
			
			else if(ys[j-1]-ys[j] < ys[j]-ys[j+1])
				delta--;
		
			for(int k=0; k < ADD_PTS; k++) {
				double r = delta == 0 ? 0 : (amplitude * (Math.abs(ran.nextInt()) % 100)/100.0);
				
				double ind = j * ADD_PTS + k;
				
				theta += step * ind;
				
				double v = amplitude * Math.sin(theta) + r;
			
				data.add(v);
				
				System.out.println(v);
			}

		}
		
		RsPlot rs = new RsPlot();
		rs.estimate(data);
		double a[] = rs.calcSlope();
//		System.out.println(a[0]+" "+data.size()+" "+slope_sum);
//		System.out.println(a[0]+" "+data.size());
	}

	public void straightLong(Node path) {
		Node anode = path;

		while(anode != null) {
			boolean trkFound = false;
			
			for(int j=0; j < xyOffsets.length; j++) {
				int dx = xyOffsets[j][0];
				int dy = xyOffsets[j][1];				
				
				// Track to the next node in line
				Node next = track(anode,dx,dy);
				
				// If track found
				if(!next.equals(anode)) {
				  trkFound = true;
				  anode = next;
				  break;
				}
			}
			if(!trkFound)
				anode = anode.getParent();
		}
	}

	protected Node track(Node anode,int dx, int dy) {

		int x = anode.getCol() + dx;
		int y = anode.getRow() + dy;
		
		Node parent = anode.getParent();
		if(parent == null)
			return anode;
		
		if(parent.equals(x,y))
			return anode;
		
		Node child = anode.getChild();
		if(child != null && child.equals(x,y))
			return anode;
		
		char[][] scratchMap = pin(anode);
		
        Node graft = null;
        Node start = null;
        
		int len = 0;
		
		int hugs = 0;
		
		boolean linked = false;
		do {
			char tile = scratchMap[y][x];
			
			// If we ran into an obstacle, no point in continuing
			if(tile == SYM_OBSTACLE)
				return anode;
			
			len++;
			
			if(hugsWall(x,y))
				hugs++;
			
			// If we ran into a path... do something!
			if(tile == SYM_FULL_PATH) {
				linked = true;
				break;
			}
			
			if(graft == null)
				graft = start = new Node(x,y);
			else {
				Node tmp = new Node(x,y);
				graft.setParent(tmp);
				tmp.setChild(graft);
				graft = tmp;
			}
			
			x += dx;
			y += dy;

		} while(true);
		
        if(!linked)
        	return anode;
        
        double hratio = (double) hugs / (double) len;
        if(hratio > 0.10)
        	return anode;
        
        Node terminal = findBackward(anode,x,y);
        if(terminal == null) {
        	System.err.println("INTERNAL ERROR: x="+x+" y="+y+" is not a node on path.");
        	return null;
        }
        
        graft.setParent(terminal);
        
        terminal.setChild(graft);
        
        anode.setParent(start);
        
        char[][] dummy = pin(anode);
        //dumpBackward(dummy);
		
        return terminal;
        
	}

	public void dumpBackward(char[][] dummy) {
        System.out.println("------------");
		System.out.print("   ");
		for(int k=0; k < dummy[0].length; k++)
			System.out.print(""+(k%10));
		System.out.println("");
		
		for(int j=0; j < dummy.length; j++) {
			System.out.printf("%2d ",(j%100));
			for(int i=0; i < dummy[0].length;i++) {
				System.out.print(dummy[j][i]);
			}
			System.out.println("");
		}
		
		System.out.print("   ");
		for(int k=0; k < tileMap[0].length; k++)
			System.out.print(""+(k%10));		
	}
	
	public char[][] pin(Node path) {
		int height = tileMap.length;
		int width = tileMap[0].length;
		
		char[][] amap = new char[height][width];
		
		for(int row=0; row < height; row++)
			for(int col=0; col < width; col++)
				amap[row][col] = tileMap[row][col];
		
		Node anode = path;
		while(anode != null) {
			int x = anode.getCol();
			int y = anode.getRow();
			amap[y][x] = SYM_FULL_PATH;
			anode = anode.getParent();
		}
		
		return amap;
	}	
	
	public void writeImage(Node anode,char[][] tileMap,String filen,boolean labels) {
		int w = tileMap[0].length;
		int h = tileMap.length;
		
		BufferedImage bimg =
			new BufferedImage(w*10,h*10,BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = bimg.createGraphics();
		
		Font f = new Font(null,Font.PLAIN,14);
		
		g2d.setFont(f);
		
		// Fill the background with white
		//g2d.setColor(new Color(255,255,255));
		g2d.setColor(new Color(0,255,255));
		g2d.fillRect(0,0,w*10,h*10);
		
		/*
		ImageIcon img = new ImageIcon("sandTile.png");
		Image sandTile = img.getImage();
		for(int row=0; row < 10; row++)
			for(int col=0; col < 10; col++)
				g2d.drawImage(sandTile, col*50, row*50, null);
		*/

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
			if(child == null && labels)
				g2d.drawString("goal", x*10-8, y*10-4);
			
			else if(parent == null && labels)
				g2d.drawString("start", x*10-8, y*10-4);
			
			// Render the path
			//g2d.setColor(new Color(150,150,150));
			g2d.setColor(new Color(0,0,0));
			//g2d.fillRect(x*10, y*10, 10, 10);
			g2d.fillOval(x*10, y*10, 10, 10);
			anode = parent;
		}
		
		// Draw the rooms

//		g2d.setColor(new Color(0,0,0));
//		for(int row=0; row < h; row++)
//			for(int col=0; col < w; col++) {
//				if(tileMap[row][col] == SYM_OBSTACLE)
//					g2d.fillRect(col*10,row*10,10,10);
//			}

		// Save the image
		try {
			ImageIO.write(bimg,"png",new File(filen));
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//computeFD(bimg);
	}
	
	
	protected void computeFD(BufferedImage bimg) {
		int[] counts = new int[boxes.length];
		Rectangle rect = new Rectangle();
		
		int width = bimg.getWidth();
		int height = bimg.getHeight();
		
		for(int k=0; k < boxes.length; k++) {
			int len = boxes[k];
			
			for(int row=0; row < height; row += len) {
				int rowoff = Math.min(height - (row+len),0);

				for(int col=0; col < width; col += len) {
					int coloff = Math.min(width - (col+len), 0);

					int rowlen = len + rowoff;
					int collen = len + coloff;
					
					rect.setBounds(col,row,collen,rowlen);

					int[] pixels = new int[rowlen * collen * 3];
					
					try {					
						Raster raster = bimg.getData(rect);
					
						raster.getPixels(col,row,collen,rowlen,pixels);
					}
					catch(Exception e) {
						e.printStackTrace();						
						System.out.println("width="+width);
						System.out.println("height="+height);
						System.out.println("len="+len);
						System.out.println("row="+row);
						System.out.println("col="+col);
						System.out.println("rowoff="+rowoff);
						System.out.println("coloff="+coloff);
						//System.out.println("MIN X="+raster.getMinX());
						//System.out.println("MIN Y="+raster.getMinY());
						return;
					}

					for(int j=0; j < pixels.length; j++) {
						if(pixels[j] != 255) {
							counts[k]++;
							break;
						}
					}
				}
			}
			System.out.println(boxes[k]+" "+counts[k]);
		}
	}
	
	public int[] countBoxes(Node path) {
		int[] counts = new int[boxes.length];
		for(int k=0; k < boxes.length; k++) {

			int len = boxes[k];
			for(int row=0; row < tileMap.length; row += len) {
				for(int col=0; col < tileMap[0].length; col += len) {
					boolean crosses = inbox(col,row,col+len,row+len,path);
					if(crosses)
						counts[k]++;						
				}
			}
		}
		
		return counts;
	}
	
	protected boolean inbox(int startx,int starty,int endx,int endy,Node path) {
		Node anode = path;
		while(anode != null) {
			int x = anode.getCol();
			int y = anode.getRow();
			if(x >= startx && x < endx && y >= starty && y < endy)
				return true;
			anode = anode.getParent();
			
		}
		
		return false;
	}
	
	public double getUglyIndex(Node path) {
		double index = 0;
		int len = 0;
		Node anode = path;
		
		while(anode != null) {
			boolean hugging = hugsWall(anode);
			boolean zagging = isChanging(anode);
			
			if(hugging && !zagging)
				index += 0.5;
			
			else if(hugging && zagging)
				index += 1.5;
							
			// Don't count wall and direction change twice
			if(!hugging && zagging) {
				index += 1;
			}
			anode = anode.getParent();
		}
		
		return index;
	}

	public int getHugsIndex(Node path) {
		int index = 0;
		int len = 0;
		Node anode = path;
		
		while(anode != null) {
			len++;
			
			if(hugsWall(anode))
				index++;
			
			Node parent = anode.getParent();

			anode = parent;
		}
		
		return index;
	}
	
	public int getZagsIndex(Node path) {
		int index = 0;
		int len = 0;
		Node anode = path;
		
		while(anode != null) {
			len++;
			
			if(isChanging(anode))
				index++;
			
			Node parent = anode.getParent();

			anode = parent;
		}
		
		return index;
	}
	
	public void straighten(Node path) {
		Node anode = path;
		
		while(anode != null) {
			Node parent = anode.getParent();
			if(parent == null)
				break;
			
			Node gran = parent.getParent();
			if(gran == null)
				break;

			int ax = anode.getCol();
			int ay = anode.getRow();
			
			int px = parent.getCol();
			int py = parent.getRow();
			
			int gx = gran.getCol();
			int gy = gran.getRow();
			
			if(ax == 7 && ay == 35)
				ax += 0;

			//        +
			//       +.+ anode
			if(ay == gy && (ay-1) == py && (ax-2) == gx && tileMap[ay][px] != SYM_OBSTACLE) {
				parent.setRow(ay);
			}
			
			//        +
			// anode +.+
			else if(ay == gy && (ay-1) == py && (ax+2) == gx && tileMap[ay][px] != SYM_OBSTACLE) {
				parent.setRow(ay);
			}
			
			// anode +.+
			//        +
			else if(ay == gy && (ay+1) == py && (ax-2) == gx && tileMap[ay][px] != SYM_OBSTACLE) {
				parent.setRow(ay);
			}
			// +.+ anode
			//  +
			else if(ay == gy && (ay+1) == py && (ax+2) == gx && tileMap[ay][px] != SYM_OBSTACLE) {
				parent.setRow(ay);
			}
			
			//  + anode
			// +.
			//  +
			else if(ax == gx && (ax-1) == px && (ay+2) == gy && tileMap[py][ax] != SYM_OBSTACLE) {
			    parent.setCol(ax);
			}
			
			//  + anode
			//  .+
			//  +
			else if(ax == gx && (ax+1) == px && (ay+2) == gy && tileMap[py][ax] != SYM_OBSTACLE) {
			    parent.setCol(ax);
			}
			
			// add cases for
			//  +
			//  .+
			//  + anode
			else if(ax == gx && (ax+1) == px && (ay-2) == gy && tileMap[py][ax] != SYM_OBSTACLE) {
			    parent.setCol(ax);
			}
			
			//  +
			// +.
			//  + anode
			else if(ax == gx && (ax-1) == px && (ay-2) == gy && tileMap[py][ax] != SYM_OBSTACLE) {
			    parent.setCol(ax);
			}
			
			anode = parent;
		}

	}
	

	protected Node findBackward(Node anode,int x, int y) {
		while(anode != null) {
			if(anode.equals(x,y))
				return anode;
			
			anode = anode.getParent();
			
		}
		return null;
	}
	protected boolean isChanging(Node anode) {
		Node child = anode.getChild();
		Node parent = anode.getParent();
		
		if(child == null || parent == null)
			return false;
		
    	int dx = anode.getCol() - child.getCol();
		int dy = anode.getRow() - child.getRow();
			
		int nx = anode.getCol()+dx;
		int ny = anode.getRow()+dy;
			
		if(!parent.equals(nx,ny))
			return true;
		
        return false;

	}
	
    protected boolean hugsWall(Node anode) {
    	int x = anode.getCol();
    	int y = anode.getRow();
    	
    	return hugsWall(x,y);
    }
    
	protected boolean hugsWall(int x, int y) {
    	return isObstacle(x-1,y) ||
	       isObstacle(x+1,y) ||
	       isObstacle(x,y-1) ||
	       isObstacle(x,y+1);
//    	return isObstacle(x-1,y) ||
//	       isObstacle(x+1,y) ||
//	       isObstacle(x,y-1) ||
//	       isObstacle(x,y+1) || hugsWallCorner(x,y);    	
	}
	
	protected boolean hugsWallCorner(Node anode) {
		int x = anode.getCol();
		int y = anode.getRow();
		
    	return hugsWallCorner(x,y);
	}
	
	protected boolean hugsWallCorner(int x, int y) {	
    	return isObstacle(x+1,y+1) ||
	           isObstacle(x-1,y-1) ||
	           isObstacle(x-1,y+1) ||
	           isObstacle(x+1,y-1);
	}


    protected boolean isObstacle(int x,int y) {
        char sym = tileMap[y][x];

        return sym == SYM_OBSTACLE;
    }
    
	private void generatePaths() {
		generateFullPath();

		//generateBasicPath();

	}

	/**
	 * Gets the full path.
	 * When this method returns you can walk the path from
	 * the src to dest using the child node or dest to src
	 * using the parent node.
	 */
	private void generateFullPath() {
		Node node = fullSrc;

		char[] pathMarks = {
				SYM_FULL_PATH,
		        // SYM_FULL_SRC,  // Full src included above
				SYM_FULL_DEST,
				SYM_BASIC_PATH,
				SYM_BASIC_SRC,
				SYM_BASIC_DEST };

		// Clear out the path
		path = new Vector<Node>();

		while (true) {
			Node anode = getNext(node, pathMarks);

			if (anode == null)
				break;

			// Put basic src & dest on full path
			if(anode.equals(basicSrc))
				basicSrc = anode;
			
			else if(anode.equals(basicDest))
				basicDest = anode;
			
			else if(anode.equals(fullSrc))
				fullSrc = anode;
			
			else if(anode.equals(fullDest))
				fullDest = anode;
				
			path.add(anode);

			node = anode;
		}

		//System.out.println("Full path:");
		//dumpForward(fullSrc);
	}

	/**
	 * Gets the basic path.
	 * When this method returns you can walk the path from
	 * the src to dest using the child node or dest to src
	 * using the parent node.
	 */
	private void generateBasicPath() {
		Node node = basicSrc;

		char[] pathMarks = { SYM_BASIC_PATH,
		//SYM_BASIC_SRC,  // Basic src include above
				SYM_BASIC_DEST };

		// Clear out the path
		path = new Vector<Node>();

		while (true) {
			Node anode = getNext(node, pathMarks);

			if (anode == null)
				return;

			path.add(anode);

			node = anode;

		}
	}

	/**
	 * Gets the next node on the path.
	 * @param node
	 * @param syms
	 * @return
	 */
	private Node getNext(Node node, char[] syms) {
		// If next node is straight ahead, use it
		Node anode = getQuickNode(node,syms);
		
		if(anode  != null)
			return anode;
		
		// Otherwise check adjacent tiles
		for (int j = 0; j < xyOffsets.length; j++) {
			int x = xyOffsets[j][0] + node.getCol();
			int y = xyOffsets[j][1] + node.getRow();

			// Check next inbounds
			// tileMap[0].length means number of columns
			// tileMap.lenth means the number of rows
			if (!isInbounds(x,y))
				continue;

			// Check next already on the path
			if (onPath(x, y))
				continue;

			// Check next one of our symbols
			char tileSym = tileMap[y][x];
			if(isSymbol(tileSym,syms))
				return new Node(x, y, node);
		}

		return null;
	}
	
	private boolean isInbounds(int x,int y) {
		return y>=0 && y < tileMap[0].length && x>=0 && x < tileMap.length;
	}
	
	private boolean isSymbol(char c,char[] syms) {
		for (int k = 0; k < syms.length; k++)
			if (c == syms[k])
				return true;
		
		return false;
	}

	/**
	 * Gets the "quick" (straight ahead) node.
	 * @param node
	 * @param syms
	 * @return
	 */
	private Node getQuickNode(Node node, char[] syms) {
		if(node.getParent() == null)
			return null;
		
		int dx = node.getCol() - node.getParent().getCol();
		int dy = node.getRow() - node.getParent().getRow();
		
		int x = node.getCol() + dx;
		int y = node.getRow() + dy;
		
		if(!isInbounds(x,y))
			return null;
		
		char tileSym = tileMap[y][y];
		
		if(isSymbol(tileSym,syms) && !onPath(x,y))
			return new Node(x,y,node);
		
		return null;		
	}
	
	/**
	 * Gets the (Bresenham) line of sight path assumed to be inbounds.
	 * @param src Source node
	 * @param dest Destination node
	 * @return Path.
	 */
	public Node getLos(Node src, Node dest) {
		int nextx = src.getCol();
		int nexty = src.getRow();

		int endx = dest.getCol();
		int endy = dest.getRow();

		int deltay = dest.getRow() - src.getRow();
		int deltax = dest.getCol() - src.getCol();

		int stepy = (deltay < 0) ? -1 : 1;
		int stepx = (deltax < 0) ? -1 : 1;
		
		deltax = Math.abs(deltax * 2);
		deltay = Math.abs(deltay * 2);

		Node path = new Node(src);
		path.setSteps(0);

		int fraction = 0;
		if (deltax > deltay) {
			fraction = deltay * 2 - deltax;
			while (nextx != endx) {
				if (fraction >= 0) {
					nexty += stepy;
					fraction -= deltax;
				}
				nextx += stepx;
				fraction += deltay;			
				path = new Node(nextx, nexty, path);
			}
		} else {
			fraction = deltax * 2 - deltay;
			while (nexty != endy) {
				if (fraction >= 0) {
					nextx += stepx;
					fraction -= deltay;
				}
				nexty += stepy;
				fraction += deltax;		
				path = new Node(nextx, nexty, path);
			}
		}
		return path;
	}

	/**
	 * Returns true if x,y is already on the path.
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean onPath(int x, int y) {
		for (int j = 0; j < path.size(); j++) {
			Node anode = path.elementAt(j);
	
			if (anode.equals(x, y))
				return true;
		}
	
		return false;
	}
	
	/**
	 * Returns true if (inbound) path is not free (backward).
	 * @param path
	 * @return
	 */
	private boolean isBlocked(Node path) {
		if(path == null)
			return true;
		
		while(true) {
			int x = path.getCol();
			
			int y = path.getRow();
			
			if(tileMap[y][x] != SYM_FREE)
				return true;
			
			path = path.getParent();
		}
	}

	/**
	 * Load the tile map from a file.
	 * Must invoke constructor with file parameter before invoking this method.
	 */
	public void loadMap() {
		try {
			StringTokenizer metaInfo = new StringTokenizer(reader.readLine());

			width = Integer.parseInt(metaInfo.nextToken());

			height = Integer.parseInt(metaInfo.nextToken());

			tileMap = new char[height][width];

			for (int k = 0; k < height; k++) {
				String srow = reader.readLine();

				if (srow.length() != width)
					throw new Exception("bad row width");

				for (int j = 0; j < width; j++) {
					char sym = srow.charAt(j);
					tileMap[k][j] = sym;

					if (sym == SYM_BASIC_DEST)
						basicDest = new Node(j, k);

					else if (sym == SYM_BASIC_SRC)
						basicSrc = new Node(j, k);

					else if (sym == SYM_FULL_SRC)
						fullSrc = new Node(j, k);

					else if (sym == SYM_FULL_DEST)
						fullDest = new Node(j, k);
				}
			}

			generatePaths();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Dump forward from child.
	 * @param node Starting node.
	 */
	public static void dumpBackward(Node node) {
		while(node != null) {
			System.out.println(node);
			node = node.getParent();
		}
	}
	
	/**
	 * Dump forward from parent.
	 * @param node Starting node.
	 */
	public static void dumpForward(Node node) {
		while(node != null) {
			System.out.println(node);
			node = node.getChild();
		}
	}	

	/**
	 * @return the basicDest
	 */
	public Node getBasicDest() {
		return basicDest;
	}

	/**
	 * @param basicDest the basicDest to set
	 */
	public void setBasicDest(Node basicDest) {
		this.basicDest = basicDest;
	}

	/**
	 * @return the basicSrc
	 */
	public Node getBasicSrc() {
		return basicSrc;
	}

	/**
	 * @param basicSrc the basicSrc to set
	 */
	public void setBasicSrc(Node basicSrc) {
		this.basicSrc = basicSrc;
	}

	/**
	 * @return the fullDest
	 */
	public Node getFullDest() {
		return fullDest;
	}

	/**
	 * @param fullDest the fullDest to set
	 */
	public void setFullDest(Node fullDest) {
		this.fullDest = fullDest;
	}

	/**
	 * @return the fullSrc
	 */
	public Node getFullSrc() {
		return fullSrc;
	}

	/**
	 * @param fullSrc the fullSrc to set
	 */
	public void setFullSrc(Node fullSrc) {
		this.fullSrc = fullSrc;
	}

	
	/**
	 * Finds a stretch (LOS) path by walking along the full path. 
	 * @param child
	 * @param parent
	 * @return Stretch path or null if no such path exists.
	 */
	private Node stretch(Node child,Node parent) {
		int len = 1;
		
		Vector<Node> freePaths = new Vector<Node>();
		
	    while(true) {
	    	Node path = getLos(parent, child);
	    	
	    	if(!isBlocked(path))
	    		freePaths.addElement(path);
	    	
	    	if(path.getSteps() > len)
	    		return splice(child,parent,freePaths);
	    	
	    	Node nextChild = child.getChild();
	    	Node nextParent = parent.getParent();
	    	
	    	if(child.getChild() == null || parent.getParent() == null)
	    		return splice(child,parent,freePaths);
	    	
	    	freePaths.addElement(path);
	    	
	    	child = nextChild;
	    	parent = nextParent;
	    	
	    	len++;
	    }
	}
	
	/**
	 * Splices in the path
	 * @param child Assumed to point to the head of the new path.
	 * @param parent Assumed to point to the tail of the new path.
	 * @param path Assumed to point to the head of the path.
	 * @return
	 */
	private Node splice(Node child,Node parent, Vector<Node> freePaths) {
		if(freePaths.size() == 0)
			return null;
		
		Node path = freePaths.lastElement();
		
		// Link in head of splice
		path.setChild(child.getChild());
		
		// Link in tail of splice
		Node node = path;
		
		while(node.getParent() != null) {
			node = node.getParent();
		}
		
		parent.setChild(node.getChild());
		
		return parent;
	}
}
