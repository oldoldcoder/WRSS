package BuildingBlocks;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;
import BuildingBlocks.ASE.Shares;
import BuildingBlocks.ASE.VecShares;
import EPWS.Key;

public class PrivateAlgs {
	
	// private identity vector generation algorithm
	// Comm: 2.5ASE_share*numRecords
	public static VecShares PIVG(int numRecords, Key key) throws Exception {
		
		int[] v = new int[numRecords];
		Random rnd = new Random();
		
		int loc = rnd.nextInt() % numRecords;
		loc = Math.abs(loc);
		v[loc] = 1;
		
		VecShares vVecShares = ASE.share(v);
		
		// generate a random number r
		BigInteger r = Util.genRandnum(key.pair_key.get(1));
		
		// generate a random vector
		BigInteger[] r2 = Util.genRandnumVec(numRecords, key.pair_key.get(1));
		BigInteger[] r3 = Util.genRandnumVec(numRecords, key.pair_key.get(1));
		
		// CS3 computes r1
		BigInteger[] r1 = Util.bigIntegerAdd(r2, r3, ASE.modVal);
		r1 = Util.negativeBigVec(r1, ASE.modVal);
		
		BigInteger[] v1plyply = Util.bigIntegerAdd(vVecShares.s1Vec, r1, ASE.modVal);
		BigInteger[] v2plyply = Util.bigIntegerAdd(vVecShares.s2Vec, r2, ASE.modVal);
		BigInteger[] v3plyply = Util.bigIntegerAdd(vVecShares.s3Vec, r3, ASE.modVal);
		
		ASE ase = new ASE();
		
		VecShares vplyVecShares = ase.new VecShares(v1plyply, v2plyply, v3plyply); 
		
		return vplyVecShares;
		
	}
	
	// private uniform random data generation
	// Comm: 2*delta*ASE_share + delta*ASE_mul
	public static Shares PURDG(int delta, Key key) throws Exception {
		
		Random rnd = new Random();
		int bitLength = delta; 
		// CS1 generates r1
		BigInteger[] r1 = new BigInteger[bitLength];
		Shares[] r1Shares = new Shares[bitLength];
		for(int i = 0; i < bitLength; i++) {
			int tmp = rnd.nextInt() % 2;
			tmp = Math.abs(tmp);
			r1[i] = BigInteger.valueOf(tmp); 
			r1Shares[i] = ASE.share(r1[i]);
		}
		
		
		// CS2 generates r2
		BigInteger[] r2 = new BigInteger[bitLength];
		Shares[] r2Shares = new Shares[bitLength];
		for(int i = 0; i < bitLength; i++) {
			int tmp = rnd.nextInt() % 2;
			tmp = Math.abs(tmp);
			r2[i] = BigInteger.valueOf(tmp); 
			r2Shares[i] = ASE.share(r2[i]);
		}
		
		Shares tmp1 = ASE.ASE_add(r1Shares[0], r2Shares[0]); 
		Shares tmp2 = ASE.ASE_mul(r1Shares[0], r2Shares[0], key);
		tmp2 = ASE.ASE_scalP(tmp2, -2);
		tmp2 = ASE.ASE_add(tmp1, tmp2);
		Shares z = ASE.ASE_scalP(tmp2, (int) Math.pow(2, 0));
		
		for(int i = 1; i < bitLength; i++) {
			
			tmp1 = ASE.ASE_add(r1Shares[i], r2Shares[i]); 
			tmp2 = ASE.ASE_mul(r1Shares[i], r2Shares[i], key);
			tmp2 = ASE.ASE_scalP(tmp2, -2);
			tmp2 = ASE.ASE_add(tmp1, tmp2);
			tmp2 = ASE.ASE_scalP(tmp2, (int) Math.pow(2, i));
			z = ASE.ASE_add(z, tmp2);
		}
		
		return z;
		
	}
	
	// Private random data generation algorithm
	public static Shares PRDG(Key key) {
		
		SecureRandom rnd = new SecureRandom();
		BigInteger r1 = new BigInteger(ASE.n, rnd);
		r1 = r1.mod(ASE.modVal);
		BigInteger r2 = new BigInteger(ASE.n, rnd);
		r2 = r2.mod(ASE.modVal);
		BigInteger r3 = new BigInteger(ASE.n, rnd);
		r3 = r3.mod(ASE.modVal);
		
		Shares z = ASE.ase.new Shares(r1, r2, r3);
		return z;
	}
	
	// Private Binary Data Comparison
	// (2*beta + 1)ASE_mul + ASE_recover
	public static boolean PBDC(Shares[] x, Shares[] y, Key key) throws Exception {
		
		int beta = x.length;
		Shares[] w = new Shares[beta];
		Shares[] c = new Shares[beta];
		
		// beta*ASE_mul
		for(int i = 0; i < beta; i++) {
			Shares tmp1 = ASE.ASE_add(x[i], y[i]);
			Shares tmp2 = ASE.ASE_mul(x[i], y[i], key);
			tmp2 = ASE.ASE_scalP(tmp2, -2);
			w[i] = ASE.ASE_add(tmp1, tmp2);
		}
		
//		for(int i = 0; i < beta; i++) {
//			BigInteger tmp = ASE.recover(w[i]);
//			System.out.print(tmp + ";");
//		}
//		System.out.println();
		
		Shares[] sumW = new Shares[beta - 1]; 
		Shares tmp = w[beta - 1];
		for(int j = beta - 2; j >= 0; j--) {
			sumW[j] = tmp;
			if(j > 0) {
				tmp = ASE.ASE_add(tmp, w[j]);
			}
		}
		
//		for(int i = 0; i < beta - 1; i++) {
//			BigInteger tmp1 = ASE.recover(sumW[i]);
//			System.out.print(tmp1 + ";");
//		}
//		System.out.println();
		
		Shares tmp1 = ASE.ASE_sub(x[0], y[0]);
		tmp1 = ASE.ASE_add(tmp1, 1);
		Shares productC = ASE.ASE_add(tmp1, sumW[0]);
		
		
		// beta*ASE_mul
		for(int i = 0; i < beta; i++) {
			tmp1 = ASE.ASE_sub(x[i], y[i]);
			tmp1 = ASE.ASE_add(tmp1, 1);
			if(i != beta - 1) {
				tmp1 = ASE.ASE_add(tmp1, sumW[i]);
			}
			productC = ASE.ASE_mul(tmp1, productC, key);
		}
		
		Shares rShares = PRDG(key);
		rShares = ASE.ASE_mul(rShares, productC, key);
		
		BigInteger r = ASE.recover(rShares);
		
		if(r.compareTo(BigInteger.ZERO) == 0) {
			return true;
		}else {
			return false;
		}
	}
	
	public static Shares PBDC_PRCT(Shares[] x, Shares[] y, Key key) throws Exception {
		
		int beta = x.length;
		Shares[] w = new Shares[beta];
		Shares[] c = new Shares[beta];
		
		for(int i = 0; i < beta; i++) {
			Shares tmp1 = ASE.ASE_add(x[i], y[i]);
			Shares tmp2 = ASE.ASE_mul(x[i], y[i], key);
			tmp2 = ASE.ASE_scalP(tmp2, -2);
			w[i] = ASE.ASE_add(tmp1, tmp2);
		}
		
//		for(int i = 0; i < beta; i++) {
//			BigInteger tmp = ASE.recover(w[i]);
//			System.out.print(tmp + ";");
//		}
//		System.out.println();
		
		Shares[] sumW = new Shares[beta - 1]; 
		Shares tmp = w[beta - 1];
		for(int j = beta - 2; j >= 0; j--) {
			sumW[j] = tmp;
			if(j > 0) {
				tmp = ASE.ASE_add(tmp, w[j]);
			}
		}
		
//		for(int i = 0; i < beta - 1; i++) {
//			BigInteger tmp1 = ASE.recover(sumW[i]);
//			System.out.print(tmp1 + ";");
//		}
//		System.out.println();
		
		Shares tmp1 = ASE.ASE_sub(x[0], y[0]);
		tmp1 = ASE.ASE_add(tmp1, 1);
		Shares productC = ASE.ASE_add(tmp1, sumW[0]);
		
		for(int i = 0; i < beta; i++) {
			tmp1 = ASE.ASE_sub(x[i], y[i]);
			tmp1 = ASE.ASE_add(tmp1, 1);
			if(i != beta - 1) {
				tmp1 = ASE.ASE_add(tmp1, sumW[i]);
			}
			productC = ASE.ASE_mul(tmp1, productC, key);
		}
		return productC;
	}
	
	// Private range containment test algorithm
	public static boolean PRCT(Shares[] l, Shares[] u, Shares[] l_node, Shares[] u_node, Key key) throws Exception {
		
		Shares c1 = PBDC_PRCT(l_node, l, key); // with 2*beta*ASE_mul communication
		Shares c2 = PBDC_PRCT(u, u_node, key); // with 2*beta*ASE_mul communication
		
		Shares rShares = PRDG(key);
		rShares = ASE.ASE_mul(rShares, c1, key);
		rShares = ASE.ASE_mul(rShares, c2, key);
		
		BigInteger r = ASE.recover(rShares);
		if(r.compareTo(BigInteger.ZERO) == 0) {
			return false;
		}else {
			return true;
		}
		
		// The total communication overhead is (4*beta + 2)*ASE_mul + ASE_recover
	}
	
	// Private arithmetic data comparison
	public static boolean PADC(Shares xShares, int eta) {
		
		// regenerate shares
		Shares newXShares = reShare(xShares, eta);
		// 1 ASE_share
		
		Random rnd = new Random();
		double r = rnd.nextDouble();
		double alpha = rnd.nextDouble();
		if(rnd.nextInt() %2 == 0) {
			alpha = -alpha;
		}
		
		// CS1 compute z1
		double x1 = newXShares.s1.doubleValue();
		double z1 = x1 - r;
		
		// CS3
		double z3 = (z1 + newXShares.s3.doubleValue())*alpha;
		
		// CS2
		double z2 = (newXShares.s2.doubleValue()  - r)*alpha;
		// 1 ASE_share
		
		// CS1
		double z = z2 + z3;
		// 1 ASE_share
		
		int gamma1;
		if(z < 0) {
			gamma1 = 1;
		}else {
			gamma1 = 0;
		}
		
		int gamma3;
		if(alpha < 0) {
			gamma3 = 1;
		}else {
			gamma3 = 0;
		}
		
//		System.out.println("z:" + z);
//		System.out.println("alpha:" + alpha);
		
		int gamma = gamma1 + gamma3 - 2*gamma1*gamma3;
		
		if(gamma == 1) {
			return true;
		}else {
			return false;
		}
	}
	
	
	// Private arithmetic data comparison for aliasSampling
	
	public static Shares[] PADC_aliasSampling(Shares xShares, int eta) {
		
		// regenerate shares
		Shares newXShares = reShare(xShares, eta);
		
		Random rnd = new Random();
		double r = rnd.nextDouble();
		double alpha = rnd.nextDouble();
		if(rnd.nextInt() %2 == 0) {
			alpha = -alpha;
		}
		
		// CS1 compute z1
		double x1 = newXShares.s1.doubleValue();
		double z1 = x1 - r;
		
		// CS3
		double z3 = (z1 + newXShares.s3.doubleValue())*alpha;
		
		// CS2
		double z2 = (newXShares.s2.doubleValue()  - r)*alpha;
		
		// CS1
		double z = z2 + z3;
		
		int gamma1;
		if(z < 0) {
			gamma1 = 1;
		}else {
			gamma1 = 0;
		}
		
		int gamma3;
		if(alpha < 0) {
			gamma3 = 1;
		}else {
			gamma3 = 0;
		}
		
		Shares gamma1Share = ASE.ase.new Shares(BigInteger.valueOf(gamma1), BigInteger.ZERO, BigInteger.ZERO);
		Shares gamma3Share = ASE.ase.new Shares(BigInteger.ZERO, BigInteger.ZERO, BigInteger.valueOf(gamma3));
		
		Shares[] resultShares = new Shares[2];
		resultShares[0] = gamma1Share;
		resultShares[1] = gamma3Share;
		
		return resultShares;
		
	}
	
	// Comm: 2 64-bit shares;
	public static Shares reShare(Shares x, int eta) {
	
		SecureRandom rnd = new SecureRandom();
		BigInteger u1 = new BigInteger(eta, rnd);
		BigInteger u2 = new BigInteger(eta, rnd);
		
		BigInteger x1miusu1 = x.s1.subtract(u1);
		BigInteger x2miusu2 = x.s2.subtract(u2);
		
		BigInteger x3 = x.s3.add(x1miusu1).add(x2miusu2).mod(ASE.modVal);
		x3 = x3.subtract(ASE.modVal);
		
		Shares newX = ASE.ase.new Shares(u1, u2, x3);
		
		return newX;
	}
	
	
	// private data transformation and encryption algorithms
	public static Shares[] ToBinAndEnc(int x, int beta) {
		
		int[] xBinary = Util.intToBinay(x, beta);
		Shares[] xBinaryShares = ASE.shareVec(xBinary);
		
		return xBinaryShares;
	}
	
	public static ArrayList<Shares[]> ToBinAndEncBatch(ArrayList<Integer> xList, int beta) {
		
		ArrayList<Shares[]> xListShares = new ArrayList<Shares[]>();
		for(int i = 0; i < xList.size(); i++) {
			xListShares.add(ToBinAndEnc(xList.get(i), beta));
		}
		return xListShares;
	}
	
}
