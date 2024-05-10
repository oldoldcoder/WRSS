package BuildingBlocks;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Paillier {

	   /*
	   key (n, g) of Paillier PKE.
	   */
	   public class PublicKey {
	       
		   private BigInteger n, g;
		   
	       public PublicKey(BigInteger n, BigInteger g) {
	    	   
	           this.n = n;
	           this.g = g; 
	           
	       }
	       
	       public BigInteger getN() {
	           return n;
	       }
	       
	       public BigInteger getG() {
	           return g;
	       } 	
	       
	    }
	   
	   /*
	    * @ClassName: PrivateKey
	    * @Description: This is a class for storing the private key (lambda, mu) of Paillier PKE.
	   */
	   public class PrivateKey {
	       
		   private BigInteger lambda, mu;
		   
	       public PrivateKey(BigInteger lambda, BigInteger mu) {
	    	   
	           this.lambda = lambda;
	           this.mu = mu;
	           
	        }
	       public BigInteger getLambda() {
	    	   
	           return lambda;
	           
	       }
	       public BigInteger getMu() {
	    	   
	           return mu;
	           
	       } 
	   }
	   
	   private final int CERTAINTY = 64;
	   private PublicKey pubkey;
	   private PrivateKey prikey;
	   
	   /*
	    * @Title: getPubkey
	    * @Description: This function returns the generated
	                    public key.
	    * @return PublicKey The public key used to encrypt the data.
	    */
	   
	   public PublicKey getPubkey() {
		   
	       return pubkey;
	       
	   }
	   
	   public PrivateKey getPrikey() {
		   
		    return prikey;
		    
		}
	   
	   
	   // Key generation
	   public void keyGeneration(int k) {
		
		    BigInteger p_prime, q_prime, p, q;
		    
		    do {
		   
		        p_prime = new BigInteger(k, CERTAINTY, new SecureRandom());
		        p = (p_prime.multiply(BigInteger.valueOf(2))).add(BigInteger.ONE);
		        
		    }while (!p.isProbablePrime(CERTAINTY));
		
		    do {
		    	do {
		    		
		            q_prime = new BigInteger(k, CERTAINTY,new SecureRandom());
		            
		    	} while (p_prime.compareTo(q_prime) == 0);
		    	
		    	q = (q_prime.multiply(BigInteger.valueOf(2))).add(BigInteger.ONE);
		    	
		    } while (!q.isProbablePrime(CERTAINTY));
		    
		    BigInteger n = p.multiply(q);
		    
		    BigInteger nsquare = n.pow(2);
		    // a generator g=(1+n) in Z*_(n^2)
		    BigInteger g = BigInteger.ONE.add(n);
		    // lambda = lcm(p-1, q-1) = p_prime*q_prime
		    BigInteger lambda = BigInteger.valueOf(2).multiply(p_prime).multiply(q_prime);
		    // mu = (L(g^lambda mod n^2))^{-1} mod n
		    BigInteger mu = Lfunction(g.modPow(lambda, nsquare), n).modInverse(n);
		    pubkey = new PublicKey(n, g);
		    prikey = new PrivateKey(lambda, mu);
	   }    
	   
	   private static BigInteger Lfunction(BigInteger mu, BigInteger n) {
		   
		   return mu.subtract(BigInteger.ONE).divide(n);
		   
	   }
	   
	   public static BigInteger encrypt(BigInteger m, PublicKey pubkey) throws Exception {
		   
		    BigInteger n = pubkey.getN();
		    BigInteger nsquare = n.pow(2);
		    BigInteger g = pubkey.getG();
		    
		    BigInteger r = randomZStarN(n);
		    
		    return (g.modPow(m, nsquare).multiply(r.modPow(n, nsquare))).mod(nsquare);
		}
		    
	    public static BigInteger decrypt(BigInteger c, PublicKey pubkey, PrivateKey prikey) throws Exception {
	       
	    	BigInteger n = pubkey.getN();
	    	BigInteger nsquare = n.pow(2);
	    	BigInteger lambda = prikey.getLambda();
	    	BigInteger mu = prikey.getMu();
	    	if (!belongToZStarNSquare(c, nsquare)) {
	           throw new Exception("Paillier.decrypt(BigInteger c, PrivateKey prikey): ciphertext c is not in Z*_(n^2)");
	        }
	    	
	    	return Lfunction(c.modPow(lambda, nsquare), n).multiply(mu).mod(n);
	    }
	   
	    
	    public static BigInteger add(BigInteger c1, BigInteger c2, PublicKey pubkey) {
	    	
	    	BigInteger nsquare = pubkey.getN().pow(2);
	    	
	    	return c1.multiply(c2).mod(nsquare);
	    }
	    
	    public static BigInteger mul(BigInteger c, BigInteger m, PublicKey pubkey) {
	    	
	    	BigInteger nsquare = pubkey.getN().pow(2);
	    	return c.modPow(m, nsquare);
	    	
	    }
	    
	    public static BigInteger selfBlind(BigInteger c, BigInteger r, PublicKey pubkey) {
	        
	    	BigInteger n = pubkey.getN();
	        BigInteger nsquare = n.pow(2);
	        return c.multiply(r.modPow(n, nsquare)).mod(nsquare);
	        
	    }
	    
	    public static BigInteger randomZStarN(BigInteger n) {
	    	
	        BigInteger r;
	        
	        do {
	            r = new BigInteger(n.bitLength(), new SecureRandom());
	        } while (r.compareTo(n) >= 0 || r.gcd(n).intValue() != 1);
	        
	        return r;
	    }
	    
	    private static boolean belongToZStarN(BigInteger m, BigInteger n) {
    	    if (m.compareTo(BigInteger.ZERO) < 0 || m.compareTo(n) >= 0 || m.gcd(n).intValue() != 1) {
    	        
    	    	return false;
    	    }
    	    return true;
    	}
	    
	    private static boolean belongToZStarNSquare(BigInteger c, BigInteger nsquare){
	    	    
	    	if (c.compareTo(BigInteger.ZERO) < 0 || c.compareTo(nsquare) >= 0 || c.gcd(nsquare).intValue() != 1) {
	    	        return false;
	    	}
	    	    return true;
	    }

	    public static BigInteger cipher_mul(BigInteger c1, BigInteger c2, PublicKey pubkey, PrivateKey prikey) throws Exception {
	    	
	    	// S1 computes E(m_1+r1),  E(m_1+r1),
	    	
	    	BigInteger n = pubkey.getN();
	        BigInteger nsquare = n.pow(2);
	    	
	    	SecureRandom secureRandom = new SecureRandom();
	    	BigInteger r1 = new BigInteger(64, secureRandom);
	    	BigInteger r2 = new BigInteger(64, secureRandom);
	    	
	    	BigInteger cr1 = Paillier.encrypt(r1, pubkey);
	    	BigInteger cr2 = Paillier.encrypt(r2, pubkey);
	    	
	    	BigInteger cm1Pr1 = Paillier.add(c1, cr1, pubkey);
	    	BigInteger cm2Pr2 = Paillier.add(c2, cr2, pubkey);
	    	
	    	// recover r1Pr1 and m2Pr2
	    	
	    	BigInteger m1Pr1 = Paillier.decrypt(cm1Pr1, pubkey, prikey);
	    	BigInteger m2Pr2 = Paillier.decrypt(cm2Pr2, pubkey, prikey);
	    	
	    	// Compute m1Pr1*m2Pr2
	    	
	    	BigInteger m1Pr1Mulm2Pr2 = m1Pr1.multiply(m2Pr2);
	    	
	    	BigInteger cr1Pr1Mulm2Pr2 = Paillier.encrypt(m1Pr1Mulm2Pr2, pubkey);
	    	
	    	// (m1 + r1)(m2 + r2) = m1*m2 + m1*r2 + m2*r1 + r1*r2
	    	
	    	BigInteger cm1r2 = Paillier.mul(c1, r2, pubkey);
	    	BigInteger cm2r1 = Paillier.mul(c2, r1, pubkey);
	    	
	    	BigInteger r1r2 = r1.multiply(r2);
	    	
	    	BigInteger cr1r2 = Paillier.encrypt(r1r2, pubkey);
	    	
	    	BigInteger cm1m2 = Paillier.add(cr1Pr1Mulm2Pr2, cm1r2.modInverse(nsquare), pubkey);
	    	cm1m2 = Paillier.add(cm1m2, cm2r1.modInverse(nsquare), pubkey);
	    	cm1m2 = Paillier.add(cm1m2, cr1r2.modInverse(nsquare), pubkey);
	    	
	    	return cm1m2;
	    	
	    }
	    
	    
	    
	    public static void main(String[] args) throws Exception {
	        
	    	Paillier paillier = new Paillier();
	    	
	    	int k = 512;
	    	int cycle = 1000;
	    	
	    	
	    		
    		// KeyGeneration
	        paillier.keyGeneration(k);
	        Paillier.PublicKey pubkey = paillier.getPubkey();
	        Paillier.PrivateKey prikey = paillier.getPrikey();
	        
	        // Encryption and Decryption
	        
	        BigInteger m1 = BigInteger.valueOf(3);
	        BigInteger m2 = BigInteger.valueOf(5);
	        
	        BigInteger c1 = Paillier.encrypt(m1, pubkey);
	        BigInteger c2 = Paillier.encrypt(m2, pubkey);
	        
	        BigInteger cm1m2 = cipher_mul(c1, c2, pubkey, prikey);
	        
	        BigInteger m1m2 = Paillier.decrypt(cm1m2, pubkey, prikey);
	        
	        System.out.println(m1m2);
	        
	        
	        
	    }
	        

	
}
