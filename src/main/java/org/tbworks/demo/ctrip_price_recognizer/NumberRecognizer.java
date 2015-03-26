package org.tbworks.demo.ctrip_price_recognizer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class NumberRecognizer {

	/** 获取图片像素的二值化二维数组——纵向优先， 为true就是该点为白色，为false该点为黑色
	 * @param img
	 * @return
	 */
	public static boolean [][] get2ValuePixesHeightFirst(BufferedImage img)
	{
		int width= img.getWidth();
		int height= img.getHeight();
		boolean [][] result = new boolean[width][height];
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
			{
				//透明（在RGB中为黑色）和白色 都设置为false；
				result[i][j]=img.getRGB(i, j)==16777215 || img.getRGB(i, j)== 0?false:true;
			}
		return result;
	}
	
	// (-1) 空白 
	//(-2)stopSymbol   16 17 18
	//(-3)comma                 19 20 21 22
	// 1          8 9  17 18
	// 2      6 7 8 9       16 17 18
	// 3      6 7 8 9    15 16 17 18
	// 4          12 13 14 15      
	// 5                 15 16 17 18
	// 6            9 10 11 12 13 14 15 16
	// 7      6 7      17 18
	// 8        7 8 9 10 11  13 14 15 16 17
	// 9          8 9 10 11 12 13
	// 0          8 9 10 11 12 13 14 15 16
	
	
	/** 判断是否为空白竖列
	 * @param verticalLineArray
	 * @return
	 * @throws Exception
	 */
	public static boolean isBlankLine(boolean [] verticalLineArray) throws Exception
	{
		if(verticalLineArray.length!=22)
		{
			throw new Exception("This is a new rule image. Can not recognize it!");
		}
		for(int i=0;i<verticalLineArray.length;i++)
		{
			if(verticalLineArray[i])
			{
				return false;
			}
		}
		return true;
	}
	
	/** 识别数字
	 * @param verticalLineArray
	 * @return
	 * @throws Exception
	 */
	public static char recognizeNumber( boolean [] verticalLineArray) throws Exception
	{
		if(verticalLineArray.length!=22)
		{
			throw new Exception("This is a new rule image. Can not recognize it!");
		}
		if(verticalLineArray[6-1])
		{// 2 , 3,  7
			if(verticalLineArray[8-1])
			{// 2, 3
				if(verticalLineArray[15-1])
				{//3
					return '3';
				}
				else
				{
					return '2';
				}
			}
			else
			{// 7
				return '7';
			}
		}
		else
		{
			if(verticalLineArray[7-1])
			{//8
				return '8';
			}
			else
			{
				if(verticalLineArray[8-1])
				{//1 , 9 , 0
					if(verticalLineArray[10-1])
					{//9 , 0
						if(verticalLineArray[14-1])
						{
							return '0';
						}
						else
						{
							return '9';
						}
					}
					else
					{
						return '1';
					}
				}
				else
				{
					if(verticalLineArray[9-1])
					{// 6
						return '6';
					}
					else
					{// 4 ,5 , '.' , ','
						if(verticalLineArray[12-1])
						{// 4
							return '4';
						}
						else
						{
							if(verticalLineArray[15-1])
							{
								return '5';
							}
							else
							{
								if(verticalLineArray[16-1])
								{
									return '.';
								}
								else if(verticalLineArray[19-1])
								{
									return ',';
								}
								else
								{
									return '\0';
								}
							}
						}
					}
				}
			}
		}
			
	}

	/** 初始化
	 * @param imageNumerMap
	 * @throws Exception
	 */
	public static void initialize(Map<Integer,String> imageNumerMap) throws Exception
	{
		ImageHelper tbimg= new ImageHelper();
		BufferedImage img = tbimg.getImg("C://1.gif");
		
		System.out.println("算法开始...");
		long t1=System.currentTimeMillis();
		//运行测试内容
		
		
		boolean [][] pixes = get2ValuePixesHeightFirst(img);
		int width = img.getWidth();
		//用于标志是否是第一个非空白竖列，也就是每个数字的第一竖列
		boolean isFirstUnblank=true;
		for(int i=0;i<width;i++)
		{
			boolean [] tempVL = pixes[i];
			if(!isBlankLine(tempVL))
			{//不是空白行
				if(isFirstUnblank)
				{//是数字的第一竖列
					char tempChar = recognizeNumber(tempVL);
					imageNumerMap.put(i+1, ""+tempChar);
					isFirstUnblank = false;
				}
			}
			else
			{
				isFirstUnblank =true;
			}
		}
		long t2=System.currentTimeMillis();
		System.out.println("算法结束，耗时："+(t2-t1)+"ms");
	}
	
	public static int getMapKey(int index)
	{
		return index+2;
	}

}
