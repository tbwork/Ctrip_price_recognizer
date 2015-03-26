package org.tbworks.demo.ctrip_price_recognizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Test {

	
	static Map<Integer,String> imageNumerMap= new HashMap<Integer,String>();
	
	public static void main(String[]args) throws Exception
	{
		
		NumberRecognizer.initialize(imageNumerMap);
		Set<Integer> sorted= new TreeSet<Integer>(imageNumerMap.keySet());
		Iterator<Integer> it = sorted.iterator();  
		while(it.hasNext())
		{
			Integer temp= it.next();
			
			System.out.println(temp+"   :   "+imageNumerMap.get(temp));
		}
		
		Scanner sc = new Scanner(System.in);   
		
		while(true)
		{
			System.out.println("请输入坐标值：");
			int input = sc.nextInt();
			long t1=System.currentTimeMillis();
			String result = imageNumerMap.get(input);
			if(result==null)
				result = imageNumerMap.get(NumberRecognizer.getMapKey(input));
			long t2=System.currentTimeMillis();
			System.out.println("该位置的数字为："+result +" ; 耗时:"+(t2-t1)+"ms");
			if(input<0)
			{
				break;
			}
		}
		
	}
	
}
