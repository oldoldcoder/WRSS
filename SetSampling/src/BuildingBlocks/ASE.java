package BuildingBlocks;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import EPWS.Key;

// eTPSS加密方法
public class ASE {
	
	public static int n = 64;
	public static BigInteger modVal = BigInteger.TWO.pow(n);
	public static ASE ase = new ASE();
	public static SecureRandom srnd = new SecureRandom();
	
	public class Shares {
		
		public BigInteger s1;
		public BigInteger s2;
		public BigInteger s3;
		
		public Shares(BigInteger s1, BigInteger s2, BigInteger s3) {
			this.s1 = s1;
			this.s2 = s2;
			this.s3 = s3;
		}
		
	} 
	
	public class VecShares {
		
		public BigInteger[] s1Vec;
		public BigInteger[] s2Vec;
		public BigInteger[] s3Vec;
		
		public VecShares(BigInteger[] s1Vec, BigInteger[] s2Vec, BigInteger[] s3Vec) {
		
			this.s1Vec = s1Vec;
			this.s2Vec = s2Vec;
			this.s3Vec = s3Vec;
		}
		
	}
	
	public static Shares share(BigInteger x) {
		
		BigInteger s1 = new BigInteger(n, srnd);
		BigInteger s2 = new BigInteger(n, srnd);
		BigInteger s3 = x.subtract(s1).subtract(s2).mod(modVal);
		Shares xShares = ase.new Shares(s1, s2, s3);
		
		return xShares;
	}
	
	public static Shares share(int x) {
		
		BigInteger xBN = BigInteger.valueOf(x);
		return share(xBN);
	}
	
	public static VecShares share(BigInteger[] xVec) {
		
		int n = xVec.length;
		BigInteger[] s1Vec = new BigInteger[n];
		BigInteger[] s2Vec = new BigInteger[n];
		BigInteger[] s3Vec = new BigInteger[n];
		
		for(int i = 0; i < n; i++) {
			Shares zShares = share(xVec[i]);
			s1Vec[i] = zShares.s1;
			s2Vec[i] = zShares.s2;
			s3Vec[i] = zShares.s3;
		}
		
		VecShares xVecShares = ase.new VecShares(s1Vec, s2Vec, s3Vec);
		
		return xVecShares;
		
	}
	
	public static VecShares share(int[] xVec) {
		
		BigInteger[] xVecBN = new BigInteger[xVec.length];
		for(int i = 0; i < xVec.length; i++) {
			xVecBN[i] = BigInteger.valueOf(xVec[i]);
		}
		
		VecShares xVecShares = share(xVecBN);
		
		return xVecShares;
		
	}
	
	public static ArrayList<Shares> share(ArrayList<Integer> x) {
		
		ArrayList<Shares> xShares = new ArrayList<Shares>();
		for(int i = 0; i < x.size(); i++) {
			xShares.add(share(x.get(i)));
		}
		return xShares;
	}
	
	
	public static Shares[] shareVec(BigInteger[] xVec) {
		
		Shares[] xVecShares = new Shares[xVec.length];
		
		for(int i = 0; i < xVec.length; i++) {
			xVecShares[i] = share(xVec[i]);
		}
		
		return xVecShares;
		
	}
	
	public static Shares[] shareVec(int[] xVec) {
		
		Shares[] xVecShares = new Shares[xVec.length];
		
		for(int i = 0; i < xVec.length; i++) {
			xVecShares[i] = share(xVec[i]);
		}
		
		return xVecShares;
		
	}
	
	public static BigInteger recover(Shares xShares) {
		
		BigInteger x = xShares.s1.add(xShares.s2).add(xShares.s3).mod(modVal);
		
		if(x.compareTo(modVal.divide(BigInteger.TWO)) == -1) {
			return x;
		}else {
			return x.subtract(modVal);
		}
	}
	
	public static int recoverBinary(Shares[] xShares) {
		
		int sum = 0;
		for(int i = 0; i < xShares.length; i++) {
			BigInteger xBN = recover(xShares[i]);
			sum = (int) (sum + xBN.intValue()*Math.pow(2, i));
		}
		return sum;
	}
	
	
	public static BigInteger[] recover(VecShares xVecShares) {
		
		BigInteger[] xVec = Util.bigIntegerAdd(xVecShares.s1Vec, xVecShares.s2Vec, modVal);
		xVec = Util.bigIntegerAdd(xVecShares.s3Vec, xVec, modVal);
		return xVec;
		
	}
	
	public static Shares ASE_add(Shares xShares, Shares yShares) {
		
		BigInteger s1 = xShares.s1.add(yShares.s1).mod(modVal);
		BigInteger s2 = xShares.s2.add(yShares.s2).mod(modVal);
		BigInteger s3 = xShares.s3.add(yShares.s3).mod(modVal);
		Shares zShares = ase.new Shares(s1, s2, s3);
		
		return zShares;
	}
	
	public static Shares ASE_add(Shares xShares, int y) {
		
		BigInteger s1 = xShares.s1.add(BigInteger.valueOf(y)).mod(modVal);
		Shares zShares = ase.new Shares(s1, xShares.s2, xShares.s3);
		
		return zShares;
	}
	
	public static Shares ASE_sub(Shares xShares, Shares yShares) {
		
		BigInteger s1 = xShares.s1.subtract(yShares.s1).mod(modVal);
		BigInteger s2 = xShares.s2.subtract(yShares.s2).mod(modVal);
		BigInteger s3 = xShares.s3.subtract(yShares.s3).mod(modVal);
		
		Shares zShares = ase.new Shares(s1, s2, s3);
		
		return zShares;
	}
	
	public static Shares ASE_sub(int x, Shares yShares) {
		
		BigInteger s1 = BigInteger.valueOf(x).subtract(yShares.s1).mod(modVal);
		Shares zShares = ASE.ase.new Shares(s1, yShares.s2.negate(), yShares.s3.negate());
		return zShares;
		
	}
	
	public static Shares ASE_scalP(Shares xShares, int y) {
		
		BigInteger yBN = BigInteger.valueOf(y);
		BigInteger s1 = xShares.s1.multiply(yBN).mod(modVal);
		BigInteger s2 = xShares.s2.multiply(yBN).mod(modVal);
		BigInteger s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		Shares zShares = ase.new Shares(s1, s2, s3);
		return zShares;
		
	} 
	
	public static Shares ASE_scalP(Shares xShares, BigInteger yBN) {
		
		BigInteger s1 = xShares.s1.multiply(yBN).mod(modVal);
		BigInteger s2 = xShares.s2.multiply(yBN).mod(modVal);
		BigInteger s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		Shares zShares = ase.new Shares(s1, s2, s3);
		return zShares;
		
	} 
	
	public static BigInteger hmacSHA256(byte[] key, byte[] content) throws Exception {
        
		Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(key, 0, key.length, "HmacSHA256"));
        byte[] hmacSha256Bytes = hmacSha256.doFinal(content);
        
        BigInteger cipherBN = new BigInteger(hmacSha256Bytes);
    	cipherBN = cipherBN.mod(modVal);
    	return cipherBN;
    	
    }

	
	public static Shares ASE_mul(Shares xShares, Shares yShares, Key key) throws Exception {

		// ASE without random noise
		BigInteger s1 = xShares.s1.multiply(yShares.s1).mod(modVal);
		s1 = s1.add(xShares.s1.multiply(yShares.s2).mod(modVal)).mod(modVal);
		s1 = s1.add(xShares.s2.multiply(yShares.s1).mod(modVal)).mod(modVal);
		
		BigInteger s2 = xShares.s2.multiply(yShares.s2).mod(modVal);
		s2 = s2.add(xShares.s2.multiply(yShares.s3).mod(modVal)).mod(modVal);
		s2 = s2.add(xShares.s3.multiply(yShares.s2).mod(modVal)).mod(modVal);
		
		BigInteger s3 = xShares.s3.multiply(yShares.s3).mod(modVal);
		s3 = s3.add(xShares.s3.multiply(yShares.s1).mod(modVal)).mod(modVal);
		s3 = s3.add(xShares.s1.multiply(yShares.s3).mod(modVal)).mod(modVal);
		
		Shares zShares = ase.new Shares(s1, s2, s3);
		return zShares;
		
	}
	
	
	public static Shares ASE_mul_Without_Random(Shares xShares, Shares yShares, Key key) throws Exception {
		
		BigInteger s1 = xShares.s1.multiply(yShares.s1).mod(modVal);
		s1 = s1.add(xShares.s1.multiply(yShares.s2).mod(modVal)).mod(modVal);
		s1 = s1.add(xShares.s2.multiply(yShares.s1).mod(modVal)).mod(modVal);
		
		BigInteger s2 = xShares.s2.multiply(yShares.s2).mod(modVal);
		s2 = s2.add(xShares.s2.multiply(yShares.s3).mod(modVal)).mod(modVal);
		s2 = s2.add(xShares.s3.multiply(yShares.s2).mod(modVal)).mod(modVal);
		
		BigInteger s3 = xShares.s3.multiply(yShares.s3).mod(modVal);
		s3 = s3.add(xShares.s3.multiply(yShares.s1).mod(modVal)).mod(modVal);
		s3 = s3.add(xShares.s1.multiply(yShares.s3).mod(modVal)).mod(modVal);
		
		Shares zShares = ase.new Shares(s1, s2, s3);
		return zShares;
		
	}

}
