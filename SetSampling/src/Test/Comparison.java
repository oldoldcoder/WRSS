package Test;

import java.math.BigInteger;
import java.util.Random;

import BuildingBlocks.ASE;
import BuildingBlocks.ASE.Shares;
import BuildingBlocks.Paillier;
import BuildingBlocks.Paillier.PrivateKey;
import BuildingBlocks.Paillier.PublicKey;
import BuildingBlocks.ThreePSS;
import EPWS.Key;

public class Comparison {
	
	public static PublicKey pubkey;
    public static PrivateKey prikey;
    public static Key key;
	
	public static void encComparison() throws Exception {
		
		double start, end;
		
		double timeASE = 0.0;
		double timeTPSS = 0.0;
		double timePaillier = 0.0;
		
		Random rnd = new Random();
		
		int cycle = 1000;
		int[] data1 = new int[cycle];
		int[] data2 = new int[cycle];
		for(int i = 0; i < cycle; i++) {
			
			int m1 = rnd.nextInt() % 1000;
			data1[i] = Math.abs(m1);
			
			int m2 = rnd.nextInt() % 1000;
			data1[i] = Math.abs(m2);
			
		}
		
		// ASE performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ASE.share(data1[i]);
			
		}
		end = System.nanoTime();
		timeASE = (end - start)/1000000/1000;
		timeTPSS = timeASE;
		
		// Paillier performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger c = Paillier.encrypt(BigInteger.valueOf(data1[i]), pubkey);
			
		}
		end = System.nanoTime();
		timePaillier = (end - start)/1000000/1000;
		
		
		System.out.println("timeASE:" + timeASE);
		System.out.println("timeTPSS:" + timeTPSS);
		System.out.println("timePaillier:" + timePaillier);
		
	}
	
	
	public static void addComparison() throws Exception {
		
		double start, end;
		
		double timeASE = 0.0;
		double timeTPSS = 0.0;
		double timePaillier = 0.0;
		
		Random rnd = new Random();
		
		int cycle = 1000;
		Shares[] data1Share = new Shares[cycle];
		Shares[] data2Share = new Shares[cycle];
		
		BigInteger[] c1 = new BigInteger[cycle];
		BigInteger[] c2 = new BigInteger[cycle];
		
		
		for(int i = 0; i < cycle; i++) {
			
			int m1 = rnd.nextInt() % 1000;
			data1Share[i] = ASE.share(m1);
			c1[i] = Paillier.encrypt(BigInteger.valueOf(m1), pubkey);
			
			int m2 = rnd.nextInt() % 1000;
			data2Share[i] = ASE.share(m2);
			c2[i] = Paillier.encrypt(BigInteger.valueOf(m2), pubkey);
			
			
		}
		
		// ASE performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ASE.ASE_add(data1Share[i], data2Share[i]);
			
		}
		end = System.nanoTime();
		timeASE = (end - start)/1000000/1000;
		
		// TPSS performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ThreePSS.ThreePSS_add(data1Share[i], data2Share[i]);
			
		}
		end = System.nanoTime();
		timeTPSS = (end - start)/1000000/1000;
		
		// Paillier performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger c = Paillier.add(c1[i], c2[i], pubkey);
			
		}
		end = System.nanoTime();
		timePaillier = (end - start)/1000000/1000;
		
		
		System.out.println("timeASE:" + timeASE);
		System.out.println("timeTPSS:" + timeTPSS);
		System.out.println("timePaillier:" + timePaillier);
		
	}
	
	
	public static void scalPComparison() throws Exception {
		
		double start, end;
		
		double timeASE = 0.0;
		double timeTPSS = 0.0;
		double timePaillier = 0.0;
		
		Random rnd = new Random();
		
		int cycle = 1000;
		Shares[] data1Share = new Shares[cycle];
		BigInteger[] c1 = new BigInteger[cycle];
		
		
		int[] data2 = new int[cycle];
		
		
		
		
		for(int i = 0; i < cycle; i++) {
			
			int m1 = rnd.nextInt() % 1000;
			data1Share[i] = ASE.share(m1);
			c1[i] = Paillier.encrypt(BigInteger.valueOf(m1), pubkey);
			
			int m2 = rnd.nextInt() % 1000;
			data2[i] = m2;
			
		}
		
		// ASE performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ASE.ASE_scalP(data1Share[i], data2[i]);
			
		}
		end = System.nanoTime();
		timeASE = (end - start)/1000000/1000;
		
		// TPSS performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ThreePSS.ThreePSS_scalP(data1Share[i], data2[i]);
			
		}
		end = System.nanoTime();
		timeTPSS = (end - start)/1000000/1000;
		
		// Paillier performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger c = Paillier.mul(c1[i], BigInteger.valueOf(data2[i]), pubkey); 
			
		}
		end = System.nanoTime();
		timePaillier = (end - start)/1000000/1000;
		
		
		System.out.println("timeASE:" + timeASE);
		System.out.println("timeTPSS:" + timeTPSS);
		System.out.println("timePaillier:" + timePaillier);
		
	}
	
	public static void mulComparison() throws Exception {
		
		double start, end;
		
		double timeASE = 0.0;
		double timeASEWithoutNoise = 0.0;
		double timeTPSS = 0.0;
		double timePaillier = 0.0;
		
		Random rnd = new Random();
		
		int cycle = 1000;
		Shares[] data1Share = new Shares[cycle];
		Shares[] data2Share = new Shares[cycle];
		
		BigInteger[] c1 = new BigInteger[cycle];
		BigInteger[] c2 = new BigInteger[cycle];
		
		
		for(int i = 0; i < cycle; i++) {
			
			int m1 = rnd.nextInt() % 1000;
			m1 = Math.abs(m1);
			data1Share[i] = ASE.share(m1);
			c1[i] = Paillier.encrypt(BigInteger.valueOf(m1), pubkey);
			
			int m2 = rnd.nextInt() % 1000;
			m2 = Math.abs(m2);
			data2Share[i] = ASE.share(m2);
			c2[i] = Paillier.encrypt(BigInteger.valueOf(m2), pubkey);
			
		}
		
		// ASE performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ASE.ASE_mul(data1Share[i], data2Share[i], key);
			
		}
		end = System.nanoTime();
		timeASE = (end - start)/1000000/1000;
		
		
		// ASE without noise performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ASE.ASE_mul_Without_Random(data1Share[i], data2Share[i], key);
			
		}
		end = System.nanoTime();
		timeASEWithoutNoise = (end - start)/1000000/1000;
		
		// TPSS performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			Shares xShares = ThreePSS.ThreePSS_mul(data1Share[i], data2Share[i], key);
			
		}
		end = System.nanoTime();
		timeTPSS = (end - start)/1000000/1000;
		
		// Paillier performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger c = Paillier.cipher_mul(c1[i], c2[i], pubkey, prikey); 
			
		}
		end = System.nanoTime();
		timePaillier = (end - start)/1000000/1000;
		
		
		System.out.println("timeASE:" + timeASE);
		System.out.println("timeASEWithoutNoise:" + timeASEWithoutNoise);
		System.out.println("timeTPSS:" + timeTPSS);
		System.out.println("timePaillier:" + timePaillier);
		
	}
	
	
	public static void decComparison() throws Exception {
		
		double start, end;
		
		double timeASE = 0.0;
		double timeTPSS = 0.0;
		double timePaillier = 0.0;
		
		Random rnd = new Random();
		
		int cycle = 1000;
		Shares[] data1Share = new Shares[cycle];
		BigInteger[] c1 = new BigInteger[cycle];
		
		for(int i = 0; i < cycle; i++) {
			
			int m1 = rnd.nextInt() % 1000;
			m1 = Math.abs(m1);
			data1Share[i] = ASE.share(m1);
			c1[i] = Paillier.encrypt(BigInteger.valueOf(m1), pubkey);
			
		}
		
		// ASE performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger x = ASE.recover(data1Share[i]);
			
		}
		end = System.nanoTime();
		timeASE = (end - start)/1000000/1000;
		timeTPSS = timeASE;
		
		// Paillier performance
		start = System.nanoTime();
		for(int i = 0; i < cycle; i++) {
			
			BigInteger c = Paillier.decrypt(c1[i], pubkey, prikey);
			
		}
		end = System.nanoTime();
		timePaillier = (end - start)/1000000/1000;
		
		
		System.out.println("timeASE:" + timeASE);
		System.out.println("timeTPSS:" + timeTPSS);
		System.out.println("timePaillier:" + timePaillier);
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		
		Paillier paillier = new Paillier();
    	
    	int k = 512;
    	
		// KeyGeneration
        paillier.keyGeneration(k);
        pubkey = paillier.getPubkey();
        prikey = paillier.getPrikey();
        
        int beta = 10;
        int delta = 10;
        int eta = 30;
        key = Key.InitKey(beta, delta, eta);
        
        System.out.println("1. Computational Cost of Encryption:");
        encComparison();
		
        System.out.println("2. Computational Cost of Addition:");
        addComparison();
        
        System.out.println("3. Computational Cost of Scalar Product:");
        scalPComparison();
        
        System.out.println("4. Computational Cost of multiplication:");
        mulComparison();
        
        System.out.println("5. Computational Cost of decryption:");
        decComparison();
        
        
	}

}
