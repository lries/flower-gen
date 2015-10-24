import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Flowergen {
	private static boolean[][] generateQuarter(int size){
		//Generates a random noise quarter for the flower
		boolean[][] ret = new boolean[size][size];
		for (int x=0; x<size; x++){
			for (int y=0; y<size; y++){
				ret[x][y] = new Random().nextBoolean();
			}
		}
		return ret; 
	}
	
	private static int adj(int i, int low, int high){
		//calls Math.min(high, Math.max(i, low))
		if (i<low) return low;
		if (i>high) return high;
		return i;
	}
	
	private static int adj(int i){
		//calls adj with most common values
		return adj(i, 0, 255);
	}
	
	/*Unused method; involved in accent colors
	private static int ntrue(boolean[][] b, int x, int y){
		int ret = 0;
		int rx = adj(x-1, 0, b.length-1);
		int tx = adj(x+1, 0, b.length-1);
		int ry = adj(y-1, 0, b.length-1);
		int ty = adj(y+1, 0, b.length-1);
		for (int i = rx; i<tx+1; i++){
			for (int j = ry; j<ty+1; j++){
				System.out.println(i+","+j);
				if (b[i][j]) ret++;
			}
		}
		return ret; 
	}
	*/
	private static Color[][] colorizeFlower(boolean[][] flower){
		//Colorizes a flower. 
		Color[][] ret = new Color[flower.length][flower[0].length];
		//find the key quarter
		Color[][] quarter = new Color[flower.length/2][flower[0].length/2];
		//select a base petal color - will weight this later; for now it's fully random except that it can't be too dark
		Random r = new Random();
		int pos = r.nextInt(2);
		int[] base = {r.nextInt(255),r.nextInt(255),r.nextInt(255)};
		//int[] accent = {r.nextInt(255),r.nextInt(255),r.nextInt(255)};
		while (base[0]+base[1]+base[2] < 50){
			base[0] = r.nextInt(255); base[1] = r.nextInt(255); base[2] = r.nextInt(255);	
		}
		//create the "base quarter"
		int dist;
		for (int x=0; x<quarter.length; x++){
			for (int y=0; y<quarter[0].length; y++){
				if (pos==0) dist = x+y;
				else dist = -x-y;
				dist = dist*10; 
				if (flower[x][y]){
					quarter[x][y] = new Color(adj(base[0]+dist), adj(base[1]+dist), adj(base[2]+dist));
				}
				else quarter[x][y] = new Color(0,0,0);
			}
		}
		Color[][] quarter2 = rotate(quarter);
		Color[][] quarter3 = rotate(quarter2);
		Color[][] quarter4 = rotate(quarter3);
		for (int x=0; x<quarter.length; x++){
			for (int y=0; y<quarter[0].length; y++){
				ret[x][y] = quarter[x][y];
				ret[x+quarter.length][y] = quarter4[x][y];
				ret[x+quarter.length][y+quarter.length] = quarter3[x][y];
				ret[x][y+quarter.length] = quarter2[x][y]; 
			}
		}
		dist=0;
		return ret; 
	}
	
	private static Color[][] rotate(Color[][] array) {
	//Rotates a color array
		Color[][] ret = new Color[array[0].length][array.length];
	      for(int i = 0; i < array[0].length;i++) {
	         for(int j = 0; j < array.length; j++) {
	            ret[i][j] = array[array.length - j - 1][i];
	         }
	      }
	      return ret;
	}

	private static boolean[][] rotate(boolean[][] array) {
	//Rotates a boolean array. Could probably be worked in with the above but I'm tbh lazy as hell and this works
		boolean[][] ret = new boolean[array[0].length][array.length];
	      for(int i = 0; i < array[0].length;i++) {
	         for(int j = 0; j < array.length; j++) {
	            ret[i][j] = array[array.length - j - 1][i];
	         }
	      }
	      return ret;
	   }
	
	private static boolean[][] generateFlower(int size){
		//Generates a bool flower. 
		boolean[][] quarter1 = generateQuarter(size);
		boolean[][] quarter2 = rotate(quarter1);
		boolean[][] quarter3 = rotate(quarter2);
		boolean[][] quarter4 = rotate(quarter3);
		boolean[][] ret = new boolean[size*2][size*2];
		for (int x=0; x<size; x++){
			for (int y=0; y<size; y++){
				ret[x][y] = quarter1[x][y];
				ret[x+size][y] = quarter4[x][y];
				ret[x+size][y+size] = quarter3[x][y];
				ret[x][y+size] = quarter2[x][y]; 
			}
		}
		return ret; 
	}

	private static void drawFlower(Color[][] flower, String filename){
		//Saves a color flower with this filename. 
		BufferedImage bi = new BufferedImage(flower.length,flower[0].length,BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		for (int i=0; i<flower.length; i++){
			for (int i2=0; i2<flower[0].length; i2++){
				g.setColor(flower[i][i2]);
				g.fillRect(i, i2, 1, 1);
			}
		}
		try {
			File file = new File(filename+".png");
			ImageIO.write(bi, "png", file);
		} catch (IOException e) {
			System.out.println("Saving failed.");
			return;
		}
	}
	
	private static void drawFlower(boolean[][] flower, String filename){
		//Saves a bool flower with this filename. 
		BufferedImage bi = new BufferedImage(flower.length,flower[0].length,BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		for (int i=0; i<flower.length; i++){
			for (int i2=0; i2<flower[0].length; i2++){
				if (flower[i][i2]) {
					int color = 254;
					g.setColor(new Color(color,color,color));
					g.fillRect(i, i2, 1, 1);
				}
				else {
					int color = 0; 
					g.setColor(new Color(color,color,color));
					g.fillRect(i, i2, 1, 1);
				}
			}
		}
		try {
			File file = new File(filename+".png");
			ImageIO.write(bi, "png", file);
		} catch (IOException e) {
			System.out.println("Saving failed.");
			return;
		}
	}
	
	public static void main(String[] arg){
		//draws four color flowers
		drawFlower(colorizeFlower(generateFlower(7)), "cflower1");
		drawFlower(colorizeFlower(generateFlower(7)), "cflower2");
		drawFlower(colorizeFlower(generateFlower(7)), "cflower3");
		drawFlower(colorizeFlower(generateFlower(7)), "cflower4");
		//draws four bool flowers
		drawFlower(generateFlower(7), "bflower1");
		drawFlower(generateFlower(7), "bflower2");
		drawFlower(generateFlower(7), "bflower3");
		drawFlower(generateFlower(7), "bflower4");
	}
}
