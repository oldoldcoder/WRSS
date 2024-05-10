package BuildingBlocks;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

import BuildingBlocks.TPSS.Shares;
import BuildingBlocks.TPSS.VecShares;
import EPWS.Key;

public class Alias {
	
public static Alias alias = new Alias();
	
	public class AliasPlain {
		
		public int ele1;
		public int wgt1;
		public int ele2;
		public int wgt2;
		
		public AliasPlain(int ele1, int wgt1, int ele2, int wgt2) {
		
			this.ele1 = ele1;
			this.wgt1 = wgt1;
			this.ele2 = ele2;
			this.wgt2 = wgt2;
			
		}
		
	}
	
	public class AliasCipher {
		
		public Shares ele1Shares;
		public Shares wgt1Shares;
		public Shares ele2Shares;
		public Shares wgt2Shares;
		
		public AliasCipher(Shares ele1Shares, Shares wgt1Shares, Shares ele2Shares, Shares wgt2Shares) {
		
			this.ele1Shares = ele1Shares;
			this.wgt1Shares = wgt1Shares;
			this.ele2Shares = ele2Shares;
			this.wgt2Shares = wgt2Shares;
			
		}
	}
	
	public static AliasPlain[] constructAlias(ArrayList<Integer> xDataset, ArrayList<Integer> wDataset) {
		
		AliasPlain[] aliasPlain = new AliasPlain[xDataset.size()];
		int wSum = Util.sum(wDataset);
		
//		System.out.println("wSum = " + wSum);
		
		int n = xDataset.size();
		
		LinkedList<Integer> nlXData = new LinkedList<Integer>();
		LinkedList<Integer> nlWData = new LinkedList<Integer>();
		LinkedList<Integer> gXData = new LinkedList<Integer>();
		LinkedList<Integer> gWData = new LinkedList<Integer>();
		
		for(int i = 0; i < wDataset.size(); i++) {
			if(wDataset.get(i)*n <= wSum) {
				nlXData.add(xDataset.get(i));
				nlWData.add(n*wDataset.get(i));
			}else {
				gXData.add(xDataset.get(i));
				gWData.add(n*wDataset.get(i));
			}
		}
		int i = 0; 
		while(!nlXData.isEmpty() && !gXData.isEmpty()) {
			
			int ele1 = nlXData.getFirst(); nlXData.removeFirst();
			int wgt1 = nlWData.getFirst(); nlWData.removeFirst();
			
			int ele2;
			int wgt2;
			
			if(wgt1 < wSum) {
				ele2 = gXData.getFirst(); 
				gXData.removeFirst();
				wgt2 = wSum - wgt1;
				int wgt2Ply = gWData.getFirst() - wgt2; 
				gWData.removeFirst();
				
				if(wgt2Ply <= wSum) {
					nlXData.add(ele2);
					nlWData.add(wgt2Ply);
				}else if (wgt2Ply > wSum) {
					gXData.add(ele2);
					gWData.add(wgt2Ply);
				}
			}else {
				ele2 = ele1;
				wgt2 = 0;
			}

			aliasPlain[i] = alias.new AliasPlain(ele1, wgt1, ele2, wgt2);
			i++;
		}
		
		while(!nlXData.isEmpty()) {
			int ele1 = nlXData.getFirst(); nlXData.removeFirst();
			int wgt1 = nlWData.getFirst(); nlWData.removeFirst();
			aliasPlain[i] = alias.new AliasPlain(ele1, wgt1, ele1, 0);
			i++;
		} 
		
//		if(i < xDataset.size()) {
//			System.out.println("i and xDataset.size:" + i + "," + xDataset.size());
//			
//			System.out.println("nlXData.size():" + nlXData.size());
//			System.out.println("nlWData.size():" + nlWData.size());
//			System.out.println("wSum:" + wSum);
//			
//		}
		
		return aliasPlain;
	}
	
	public static AliasCipher[] aliasEnc(AliasPlain[] aliasPlain) {
		
		AliasCipher[] aliasCiphers = new AliasCipher[aliasPlain.length];
		
		for(int i = 0; i < aliasPlain.length; i++) {
			
			int ele1 = aliasPlain[i].ele1;
			int wgt1 = aliasPlain[i].wgt1;
			int ele2 = aliasPlain[i].ele2;
			int wgt2 = aliasPlain[i].wgt2;
			
			Shares ele1Shares = TPSS.share(ele1);
			Shares wgt1Shares = TPSS.share(wgt1);
			Shares ele2Shares = TPSS.share(ele2);
			Shares wgt2Shares = TPSS.share(wgt2);
			
			aliasCiphers[i] = alias.new AliasCipher(ele1Shares, wgt1Shares, ele2Shares, wgt2Shares);
			
		}
		
		return aliasCiphers;
		
	}
	
	public static Shares aliasSampling(AliasCipher[] aliasCiphers, Key key, int delta, int eta) throws Exception {
		
		int n = aliasCiphers.length;
		
		int bitLength = delta;
//		private identity vector generation algorithm
		VecShares vecShares = PrivateAlgs.PIVG(n, key); // Comm: 5*n Shares
		
		Shares ele1Shares = TPSS.share(0);
		Shares wgt1Shares = TPSS.share(0);
		Shares ele2Shares = TPSS.share(0);
		Shares wgt2Shares = TPSS.share(0);
		
		// 4*n*ASE_Mul
		// Subtotal: 5*n Shares + 4*n*ASE_mul
		for(int i = 0; i < n; i++) {
			
			Shares vecShare = TPSS.ase.new Shares(vecShares.s1Vec[i], vecShares.s2Vec[i], vecShares.s3Vec[i]);
			
			Shares tmpEle1Shares = TPSS.ASE_mul(vecShare, aliasCiphers[i].ele1Shares, key);
			ele1Shares = TPSS.ASE_add(tmpEle1Shares, ele1Shares);
			
			Shares tmpWgt1Shares = TPSS.ASE_mul(vecShare, aliasCiphers[i].wgt1Shares, key);
			wgt1Shares = TPSS.ASE_add(tmpWgt1Shares, wgt1Shares);
			
			Shares tmpEle2Shares = TPSS.ASE_mul(vecShare, aliasCiphers[i].ele2Shares, key);
			ele2Shares = TPSS.ASE_add(tmpEle2Shares, ele2Shares);
			
			Shares tmpWgt2Shares = TPSS.ASE_mul(vecShare, aliasCiphers[i].wgt2Shares, key);
			wgt2Shares = TPSS.ASE_add(tmpWgt2Shares, wgt2Shares);
			
		}
		
//		System.out.println("ele1Shares = " + ASE.recover(ele1Shares));
//		System.out.println("wgt1Shares = " + ASE.recover(wgt1Shares));
//		System.out.println("ele2Shares = " + ASE.recover(ele2Shares));
//		System.out.println("wgt2Shares = " + ASE.recover(wgt2Shares));
		
		// Subtotal: 5*n Shares + 4*n*ASE_mul + 8*delta* share + delta*ASE_mul
		Shares rShares = PrivateAlgs.PURDG(bitLength, key); //2*delta*ASE_share + delta*ASE_mul 
		
		
//		System.out.println("rShares = " + ASE.recover(rShares));
		
		
		Shares wgtShares = TPSS.ASE_add(wgt1Shares, wgt2Shares);
		Shares z1Shares = TPSS.ASE_mul(rShares, wgtShares, key); // 1 ASE_mul
		
//		System.out.println("bitLength = " + bitLength);
		
		Shares z2Shares = TPSS.ASE_scalP(wgt1Shares, BigInteger.TWO.pow(bitLength)); 
		
//		System.out.println("z1Shares = " + ASE.recover(z1Shares));
//		System.out.println("z2Shares = " + ASE.recover(z2Shares));
		
		
		Shares zShares = TPSS.ASE_sub(z2Shares, z1Shares);
		
		Shares[] gammaShares = PrivateAlgs.PADC_aliasSampling(zShares, eta); // 5 shares
		
		Shares gamma1AddGamma3Shares = TPSS.ASE_add(gammaShares[0], gammaShares[1]);
		Shares gamma1MulGamma3Shares = TPSS.ASE_mul(gammaShares[0], gammaShares[1], key); // 1 ASE_mul
		gamma1MulGamma3Shares = TPSS.ASE_scalP(gamma1MulGamma3Shares, -2);
		
		Shares gamma1ORGamma3Shares = TPSS.ASE_add(gamma1AddGamma3Shares, gamma1MulGamma3Shares);
		
//		System.out.println("gamma1ORGamma3Shares = " + ASE.recover(gamma1ORGamma3Shares));
		
		Shares tmp1Shares = TPSS.ASE_mul(ele1Shares, gamma1ORGamma3Shares, key); // 1 ASE_mul
		
//		System.out.println("tmp1Shares = " + ASE.recover(tmp1Shares));
		
		Shares tmp2Shares = TPSS.ASE_sub(1, gamma1ORGamma3Shares);
		
//		System.out.println("tmp2Shares = " + ASE.recover(tmp2Shares));
		
		tmp2Shares = TPSS.ASE_mul(tmp2Shares, ele2Shares, key); // 1 ASE_mul
		
//		System.out.println("tmp2Shares = " + ASE.recover(tmp2Shares));
		
		tmp1Shares = TPSS.ASE_add(tmp1Shares, tmp2Shares);
		
//		System.out.println("tmp1Shares = " + ASE.recover(tmp1Shares));
		
		// Total: 5*n Shares + 4*n*ASE_mul + 8*delta* share + delta*ASE_mul + 4*ASE_mul + 5*ASE_share
		// = (5*n + 8*delta + 5)*ASE_share + (4*n + delta + 4)*ASE_mul
		
		return tmp1Shares;
		
	}
	
	public static void main(String[] args) {
		
		
		int n = 20; 
		int max = 100;
		ArrayList<Integer> xDataset = Util.genRandList(n, max);
		ArrayList<Integer> wDataset = Util.genRandList(n, max);
		Util.printTwoList(xDataset, wDataset);
		
		AliasPlain[] aliasPlain = constructAlias(xDataset, wDataset);
		Util.printAlias(aliasPlain);
		
		
		
	}

}
