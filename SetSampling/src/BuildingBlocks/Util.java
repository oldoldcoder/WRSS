package BuildingBlocks;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import BuildingBlocks.Alias.AliasPlain;
import EPWS.Key;

public class Util {
	
	public static BigInteger genRandnum(byte[] key) throws Exception {
		
		Random rnd = new Random();
		int sid = rnd.nextInt();
		String sidStr = String.valueOf(sid);
		byte[] sidByte = sidStr.getBytes();
		
		BigInteger r = ASE.hmacSHA256(key, sidByte);
		
		return r;
	}
	
	public static BigInteger[] genRandnumVec(int n, byte[] key) throws Exception {
		
		Random rnd = new Random();
		int sid = rnd.nextInt();
		String sidStr = String.valueOf(sid);
		
		BigInteger[] vec = new BigInteger[n];
		
		for(int i = 0; i < n; i++) {
			String str = sidStr + String.valueOf(i);
			byte[] strByte = str.getBytes();
			BigInteger r = ASE.hmacSHA256(key, strByte);
			vec[i] = r;
		}
		return vec;
	}
	
	public static BigInteger[] bigIntegerAdd(BigInteger[] a, BigInteger[] b, BigInteger modVal) {
		
		BigInteger[] c = new BigInteger[a.length];
		for(int i = 0; i < a.length; i++) {
			c[i] = a[i].add(b[i]).mod(modVal);
		}
		return c;
	}
	
	public static BigInteger[] negativeBigVec(BigInteger[] a, BigInteger modVBigInteger) {
		
		BigInteger[] b = new BigInteger[a.length];
		for(int i = 0; i < a.length; i++) {
			b[i] = a[i].negate();
		}
		return b;
		
	}
	
	public static void print(BigInteger[] vec) {
		
		for(int i = 0; i < vec.length; i++) {
			System.out.print(vec[i] + ",");
		}
		System.out.println();
	}

	public static void print(int[] vec) {
		
		for(int i = 0; i < vec.length; i++) {
			System.out.print(vec[i] + ",");
		}
		System.out.println();
	}

	public static int[] intToBinay(int x, int bitLength) {
		
		int[] xBinary = new int[bitLength];
		int i = 0;
		while(x != 0) {
			xBinary[i] = x %2;
			x = x/2;
			i++;
		}
		
		return xBinary;
	}
	
	public static int[] median(ArrayList<Integer> xDataset) {
		
		int[] sortedXData = new int[xDataset.size()];
		for(int i = 0; i < xDataset.size(); i++) {
			sortedXData[i] = xDataset.get(i);
		}
		
//		Util.print(sortedXData);
		
		Arrays.sort(sortedXData);
		
//		System.out.println("end");
		
//		print(sortedXData);
		
		int medianLoc;
		if(xDataset.size() % 2 == 1) {
			medianLoc = (xDataset.size() - 1)/2;
		}else {
			medianLoc = (xDataset.size() - 2)/2;
		}
		
		int[] result = new int[3];
		
		result[0] = sortedXData[0];
		result[1] = sortedXData[medianLoc];
		result[2] = sortedXData[xDataset.size() - 1];
		
		return result;
		
	}
	
	public static int sum(ArrayList<Integer> wDataset) {
		
		int result = 0;
		for(int i = 0; i < wDataset.size(); i++) {
			result = result + wDataset.get(i);
		}
		return result;
	} 

	public static int max(ArrayList<Integer> xDataset) {
		
		int maxVal = 0;
		for(int i = 0; i < xDataset.size(); i++) {
			
			if(xDataset.get(i) > maxVal) {
				maxVal = xDataset.get(i);
			}
			
		}
		return maxVal;
		
	}
	
	public static ArrayList<Integer> genRandList(int n, int max) {
		
		Random rnd = new Random();
		
		ArrayList<Integer> dataList = new ArrayList<Integer>();
		for(int i = 0; i < n; i++) {
			dataList.add(Math.abs(rnd.nextInt() % max));
		}
		return dataList;
	}
	
	public static void printTwoList(ArrayList<Integer> x, ArrayList<Integer> y) {
		
		System.out.println("x" + "\t" + "w");
		for(int i = 0; i < x.size(); i++) {
			System.out.println(x.get(i) + "\t" + y.get(i));
		}
	}
	
	public static void printAlias(AliasPlain[] aliasPlain) {
		
		System.out.println("ele1" + "\t" + "wgt1" + "\t" + "ele2" + "\t" + "wgt2");
		for(int i = 0; i < aliasPlain.length; i++) {
			
			System.out.println(aliasPlain[i].ele1 + "\t" + aliasPlain[i].wgt1 + "\t" + aliasPlain[i].ele2 + "\t" + aliasPlain[i].wgt2);
			
		}
		
	}
	
}
