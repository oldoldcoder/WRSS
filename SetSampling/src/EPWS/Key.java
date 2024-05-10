package EPWS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Key {
	
	public int n;
	public BigInteger modVal;
	public int kappa;
	public int beta;
	public int delta;
	public int eta;
	public ArrayList<byte[]> pair_key;
	public ArrayList<byte[]> key;

	public Key(int n, BigInteger modVal, int kappa, int beta, int delta, int eta, ArrayList<byte[]> pair_key, ArrayList<byte[]> key) {
		
		this.n = n;
		this.modVal = modVal;
		this.kappa = kappa;
		this.beta = beta;
		this.delta = delta;
		this.eta = eta;
		this.pair_key = pair_key;
		this.key = key;
		
	}
	

	public static String genRandString(int bitLength) {
		
		String templateStr = "abcdefghijklmnopqrstuvwxyz";
		char[] templateCharArray = templateStr.toCharArray();
		
		String str = "";
		Random rnd = new Random();
		
		for(int i = 0; i < bitLength; i++) {
			int loc = rnd.nextInt() % 26;
			loc = Math.abs(loc);
			str = str + String.valueOf(templateCharArray[loc]);
		}
		
		return str;
		
	}
	
	public static Key InitKey(int beta, int delta, int eta) {
		
		int n = 64;
		BigInteger modVal = BigInteger.TWO.pow(n);
		int kappa = 256;
		ArrayList<byte[]> pair_key = new ArrayList<byte[]>();
		ArrayList<byte[]> key = new ArrayList<byte[]>();
		
		int bitLength = kappa/8;
		
		for(int i = 0; i < 3; i++) {
			
			String keyString = genRandString(bitLength);
			byte[] keyByte = keyString.getBytes();
			pair_key.add(keyByte);
			
		}
		
		for(int i = 0; i < 3; i++) {
			
			String keyString = genRandString(bitLength);
			byte[] keyByte = keyString.getBytes();
			key.add(keyByte);
			
		}
		
		Key sk = new Key(n, modVal, kappa, beta, delta, eta, pair_key, key);
		
		return sk;
		
	}
	
}
