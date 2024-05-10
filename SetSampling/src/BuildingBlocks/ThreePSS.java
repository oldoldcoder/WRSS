package BuildingBlocks;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import BuildingBlocks.ASE.Shares;
import EPWS.Key;

public class ThreePSS {
	
	public static int n = 64;
	public static BigInteger modVal = BigInteger.TWO.pow(n);
	public static SecureRandom srnd = new SecureRandom();
	
	public static Shares ThreePSS_add(Shares xShares, Shares yShares) {
		
		BigInteger s1 = xShares.s1.add(yShares.s1).mod(modVal);
		BigInteger s2 = xShares.s2.add(yShares.s2).mod(modVal);
		BigInteger s3 = xShares.s3.add(yShares.s3).mod(modVal);
		
		s1 = xShares.s1.add(yShares.s1).mod(modVal);
		s2 = xShares.s2.add(yShares.s2).mod(modVal);
		s3 = xShares.s3.add(yShares.s3).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, s2, s3);
		return zShares;
		
	}
	
	public static Shares ThreePSS_add(Shares xShares, int y) {
		
		BigInteger s1 = xShares.s1.add(BigInteger.valueOf(y)).mod(modVal);
		BigInteger s1Ply = xShares.s1.add(BigInteger.valueOf(y)).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, xShares.s2, xShares.s3);
		
		return zShares;
	}
	
	public static Shares ThreePSS_sub(Shares xShares, Shares yShares) {
		
		BigInteger s1 = xShares.s1.subtract(yShares.s1).mod(modVal);
		BigInteger s2 = xShares.s2.subtract(yShares.s2).mod(modVal);
		BigInteger s3 = xShares.s3.subtract(yShares.s3).mod(modVal);
		
		s1 = xShares.s1.subtract(yShares.s1).mod(modVal);
		s2 = xShares.s2.subtract(yShares.s2).mod(modVal);
		s3 = xShares.s3.subtract(yShares.s3).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, s2, s3);
		
		return zShares;
	}
	
	public static Shares ThreePSS_sub(int x, Shares yShares) {
		
		BigInteger s1 = BigInteger.valueOf(x).subtract(yShares.s1).mod(modVal);
		BigInteger s1Ply = BigInteger.valueOf(x).subtract(yShares.s1).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, yShares.s2.negate(), yShares.s3.negate());
		return zShares;
		
	}
		
	public static Shares ThreePSS_scalP(Shares xShares, int y) {
		
		BigInteger yBN = BigInteger.valueOf(y);
		BigInteger s1 = xShares.s1.multiply(yBN).mod(modVal);
		BigInteger s2 = xShares.s2.multiply(yBN).mod(modVal);
		BigInteger s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		s1 = xShares.s1.multiply(yBN).mod(modVal);
		s2 = xShares.s2.multiply(yBN).mod(modVal);
		s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, s2, s3);
		return zShares;
		
	} 
	
	public static Shares ThreePSS_scalP(Shares xShares, BigInteger yBN) {
		
		BigInteger s1 = xShares.s1.multiply(yBN).mod(modVal);
		BigInteger s2 = xShares.s2.multiply(yBN).mod(modVal);
		BigInteger s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		s1 = xShares.s1.multiply(yBN).mod(modVal);
		s2 = xShares.s2.multiply(yBN).mod(modVal);
		s3 = xShares.s3.multiply(yBN).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1, s2, s3);
		return zShares;
		
	} 
	
	public static Shares ThreePSS_mul(Shares xShares, Shares yShares, Key key) throws Exception {
		
		Random rand = new Random();
		
		BigInteger s1 = xShares.s1.multiply(yShares.s1).mod(modVal);
		s1 = s1.add(xShares.s1.multiply(yShares.s2).mod(modVal)).mod(modVal);
		s1 = s1.add(xShares.s2.multiply(yShares.s1).mod(modVal)).mod(modVal);
		
		BigInteger s2 = xShares.s2.multiply(yShares.s2).mod(modVal);
		s2 = s2.add(xShares.s2.multiply(yShares.s3).mod(modVal)).mod(modVal);
		s2 = s2.add(xShares.s3.multiply(yShares.s2).mod(modVal)).mod(modVal);
		
		BigInteger s3 = xShares.s3.multiply(yShares.s3).mod(modVal);
		s3 = s3.add(xShares.s3.multiply(yShares.s1).mod(modVal)).mod(modVal);
		s3 = s3.add(xShares.s1.multiply(yShares.s3).mod(modVal)).mod(modVal);
		
		int sid = rand.nextInt();
		String sidString = String.valueOf(sid);
		String sidString1 = sidString;
		
		byte[] sidByte1 = sidString1.getBytes();
		
		BigInteger[] r = new BigInteger[3];
		
		for(int i = 0; i < 3; i++) {
			r[i] = ASE.hmacSHA256(key.pair_key.get(i), sidByte1);
		}
		
		BigInteger s1Ply = s1.add(r[0]).subtract(r[2]).mod(modVal);
		BigInteger s2Ply = s2.add(r[1]).subtract(r[0]).mod(modVal);
		BigInteger s3Ply = s3.add(r[2]).subtract(r[1]).mod(modVal);
		
		Shares zShares = ASE.ase.new Shares(s1Ply, s2Ply, s2Ply);
		return zShares;
		
	}
		

}
