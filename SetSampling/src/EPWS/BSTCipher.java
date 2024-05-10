package EPWS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;

import BuildingBlocks.ASE;
import BuildingBlocks.ASE.Shares;
import BuildingBlocks.Alias;
import BuildingBlocks.Alias.AliasCipher;
import BuildingBlocks.Alias.AliasPlain;
import BuildingBlocks.PrivateAlgs;
import EPWS.BSTPlain.InternalNode;
import EPWS.BSTPlain.LeafNode;
import EPWS.BSTPlain.Node;
import Test.Evaluation;


public class BSTCipher {
	
	public static BSTCipher bstCipher = new BSTCipher();
	
	public class CandidateResult {
		
		public ArrayList<Shares> xDatasetShares;
		public ArrayList<Shares> wDatasetShares;
		public ArrayList<AliasCipher[]> aliasCipherShares;
		public ArrayList<Shares> aliasWShares;
		
		public CandidateResult(ArrayList<Shares> xDatasetShares, ArrayList<Shares> wDatasetShares,
				ArrayList<AliasCipher[]> aliasCipherShares, ArrayList<Shares> aliasWShares) {
			this.xDatasetShares = xDatasetShares;
			this.wDatasetShares = wDatasetShares;
			this.aliasCipherShares = aliasCipherShares;
			this.aliasWShares = aliasWShares;
		}
		
	}
	
	public class NodeCipher {
		public boolean isLeaf;
	}
	
	public class LeafNodeCipher extends NodeCipher {
		
		public ArrayList<Shares[]> xDataBinaryShares;  
		public ArrayList<Shares> xDatasetShares;
		public ArrayList<Shares> wDatasetShares;
		
		public LeafNodeCipher(ArrayList<Shares[]> xDataBinaryShares, ArrayList<Shares> xDatasetShares,
				ArrayList<Shares> wDatasetShares) {
			
			this.xDataBinaryShares = xDataBinaryShares;
			this.xDatasetShares = xDatasetShares;
			this.wDatasetShares = wDatasetShares;
			this.isLeaf = true;
		}
	}
	
	public class InternalNodeCipher extends NodeCipher {
		
		public Shares bShares_node;
		public Shares[] vShares_node;
		public Shares[] lShares_node;
		public Shares[] uShares_node;
		public AliasCipher[] ACipher_node;
		public Shares wShares_A_node;
		public NodeCipher left;
		public NodeCipher right;
		
		public InternalNodeCipher(Shares bShares_node, Shares[] vShares_node, Shares[] lShares_node,
				Shares[] uShares_node, AliasCipher[] aCipher_node, Shares wShares_A_node, NodeCipher left,
				NodeCipher right) {
			super();
			this.bShares_node = bShares_node;
			this.vShares_node = vShares_node;
			this.lShares_node = lShares_node;
			this.uShares_node = uShares_node;
			this.ACipher_node = aCipher_node;
			this.wShares_A_node = wShares_A_node;
			this.left = left;
			this.right = right;
			this.isLeaf = false;
		}
	}
	
	public static NodeCipher bstEnc(Node node, int beta) {
		
		int bitLength = beta;
		
		if(node.isLeaf == true) {
			
			LeafNode leafNode = (LeafNode) node;
			ArrayList<Shares> xDatasetShares = ASE.share(leafNode.xDataset);
			ArrayList<Shares> wDatasetShares = ASE.share(leafNode.wDataset);
			ArrayList<Shares[]> xDataBinaryShares = PrivateAlgs.ToBinAndEncBatch(leafNode.xDataset, bitLength);
			
			LeafNodeCipher leafNodeCipher = bstCipher.new LeafNodeCipher(xDataBinaryShares, xDatasetShares, wDatasetShares);
			return leafNodeCipher;
			
		}else {
			
			InternalNode internalNode = (InternalNode) node;
			int v_node = internalNode.v_node;
			int l_node = internalNode.l_node;
			int u_node = internalNode.u_node;
			AliasPlain[] A_node = internalNode.A_node;
			int w_A_node = internalNode.w_A_node;
			Node left = internalNode.left;
			Node right = internalNode.right;
			
			
			Shares bShares_node = ASE.share(1);
			Shares[] vShares_node = PrivateAlgs.ToBinAndEnc(v_node, bitLength);
			Shares[] lShares_node = PrivateAlgs.ToBinAndEnc(l_node, bitLength);
			Shares[] uShares_node = PrivateAlgs.ToBinAndEnc(u_node, bitLength);
			
			AliasCipher[] ACipher_node = Alias.aliasEnc(A_node);
			Shares wShares_A_node = ASE.share(w_A_node);
			
			NodeCipher leftCipher;
			NodeCipher rightCipher;
			
			if(left != null) {
				leftCipher = bstEnc(left, bitLength);
			}else {
				leftCipher = null;
			}
			
			if(right != null) {
				rightCipher = bstEnc(right, bitLength);
			}else {
				rightCipher = null;
			}
			
			InternalNodeCipher internalNodeCipher = bstCipher.new InternalNodeCipher(bShares_node, vShares_node, lShares_node, uShares_node, ACipher_node, wShares_A_node, leftCipher, rightCipher); 
			
			return internalNodeCipher;
		}
	}
	
	public static ArrayList<Shares[]> QueryEnc(int l, int u, int bitLength) {
		
		ArrayList<Shares[]> queryShares = new ArrayList<Shares[]>();
		
		Shares[] lBinaryShares = PrivateAlgs.ToBinAndEnc(l, bitLength);
		Shares[] uBinaryShares = PrivateAlgs.ToBinAndEnc(u, bitLength);
		
		queryShares.add(lBinaryShares);
		queryShares.add(uBinaryShares);
		
		return queryShares;
	}
	
	public static Shares[] identitylv(Shares bShares, Shares[] lShares, Shares[] vShares, Key key) throws Exception {
		
		Shares[] result = new Shares[lShares.length];
		for(int i = 0; i < lShares.length; i++) {
			
			Shares tmp1 = ASE.ASE_mul(bShares, lShares[i], key);
			Shares tmp2 = ASE.ASE_sub(1, bShares);
			tmp2 = ASE.ASE_mul(tmp2, vShares[i], key);
			tmp2 = ASE.ASE_add(tmp1, tmp2);
			result[i] = tmp2;
		}
		
		return result;
	}
	
	public static CandidateResult bstSearchCipher(NodeCipher treeCipher, ArrayList<Shares[]> queryShares, Key key) throws Exception {
		
		ArrayList<Shares> cXShares = new ArrayList<Shares>();
		ArrayList<Shares> cWShares = new ArrayList<Shares>();
		ArrayList<AliasCipher[]> aliasXShares = new ArrayList<AliasCipher[]>();
		ArrayList<Shares> aliasWShares = new ArrayList<Shares>();
		
		int commSizeOfASEMul = 384; // 6 64-bits shares
		int commSizeOfASERecover = 128; // 2 64-bits shares
		int numOfASEMul = 0;
		int numOfASERecover = 0;
		int totalComm = 0;
		
		Stack<NodeCipher> stack = new Stack<NodeCipher>();
		stack.push(treeCipher);
		
		while(!stack.isEmpty()) {
			
			NodeCipher node = stack.pop();
			
			if(node.isLeaf == true) {
				
				LeafNodeCipher leafNodeCipher = (LeafNodeCipher) node;
				
				ArrayList<Shares[]> xDataBinaryShares = leafNodeCipher.xDataBinaryShares;  
				ArrayList<Shares> xDatasetShares = leafNodeCipher.xDatasetShares;
				ArrayList<Shares> wDatasetShares = leafNodeCipher.wDatasetShares;
				
				for(int i = 0; i < xDataBinaryShares.size(); i++) {
					
					// Communication Overhead in PRCT
					boolean flag = PrivateAlgs.PRCT(queryShares.get(0), queryShares.get(1), xDataBinaryShares.get(i), xDataBinaryShares.get(i), key);
					numOfASEMul = numOfASEMul + 4*key.beta + 2;
					numOfASERecover = numOfASERecover + 1;
					if(flag == true) {
						cXShares.add(xDatasetShares.get(i));
						cWShares.add(wDatasetShares.get(i));
					}
				}
				
			}else {
				
				InternalNodeCipher internalNodeCipher = (InternalNodeCipher) node;
				Shares bShares_node = internalNodeCipher.bShares_node;
				Shares[] vShares_node = internalNodeCipher.vShares_node;
				Shares[] lShares_node = internalNodeCipher.lShares_node;
				Shares[] uShares_node = internalNodeCipher.uShares_node;
				AliasCipher[] ACipher_node = internalNodeCipher.ACipher_node;
				Shares wShares_A_node = internalNodeCipher.wShares_A_node;
				NodeCipher left = internalNodeCipher.left;
				NodeCipher right = internalNodeCipher.right;
				
//				int lRecover = ASE.recoverBinary(lShares_node);
//				int rRecover = ASE.recoverBinary(uShares_node);
//				System.out.println("lRecover:" + lRecover);
//				System.out.println("rRecover:" + rRecover);
				
				// Communication Overhead in PRCT
				boolean flag = PrivateAlgs.PRCT(queryShares.get(0), queryShares.get(1), lShares_node, uShares_node, key);
				numOfASEMul = numOfASEMul + 4*key.beta + 2;
				numOfASERecover = numOfASERecover + 1;
				
//				System.out.println("flag:" + flag);
				if(flag == true) {
//					System.out.println("internal node x = " + ASE.recoverBinary(lShares_node) + ";" + ASE.recoverBinary(uShares_node));
					aliasXShares.add(ACipher_node);
					aliasWShares.add(wShares_A_node);
					
				}else {
					if(left != null) {
						
						// 2*beta*ASE_mul
						Shares[] z1Shares = identitylv(bShares_node, lShares_node, vShares_node, key);
						numOfASEMul = numOfASEMul + 2*key.beta;
						
						// 2*beta*ASE_mul
						Shares[] z2Shares = identitylv(bShares_node, vShares_node, uShares_node, key);
						numOfASEMul = numOfASEMul + 2*key.beta;
						
						// (2*beta + 1)ASE_mul + ASE_recover
						boolean comparezFlag = PrivateAlgs.PBDC(z2Shares, z1Shares, key);
						numOfASEMul = numOfASEMul + 2*key.beta + 1;
						numOfASERecover = numOfASERecover + 1;
						if (comparezFlag == false) {
							stack.add(left);
						}
					}
					if(right != null) {
						// 2*beta*ASE_mul
						Shares[] z1Shares = identitylv(bShares_node, vShares_node, lShares_node, key);
						numOfASEMul = numOfASEMul + 2*key.beta;
						// 2*beta*ASE_mul
						Shares[] z2Shares = identitylv(bShares_node, uShares_node, vShares_node, key);
						numOfASEMul = numOfASEMul + 2*key.beta;
						
						// (2*beta + 1)ASE_mul + ASE_recover
						boolean comparezFlag = PrivateAlgs.PBDC(z2Shares, z1Shares, key);
						numOfASEMul = numOfASEMul + 2*key.beta + 1;
						numOfASERecover = numOfASERecover + 1;
						if (comparezFlag == false) {
							stack.add(right);
						}
					}
				}
			}
			
		}
		
		totalComm = numOfASEMul*commSizeOfASEMul + numOfASERecover*commSizeOfASERecover;
		Evaluation.queryComm = Evaluation.queryComm + totalComm;
		
		CandidateResult candidateResult = bstCipher.new CandidateResult(cXShares, cWShares, aliasXShares, aliasWShares);
		
		return candidateResult;
	}  
	
	public static Shares resultVerify(CandidateResult candidateResult, int delta, int eta, Key key) throws Exception {
		
		int commSizeOfASEMul = 384; // 6 64-bits shares
		int commSizeOfASERecover = 128; // 2 64-bits shares
		int commSizeOfShare = 64; // 1 64-bits shares
		double numOfASEMul = 0;
		double numOfASERecover = 0;
		double numOfShare = 0;
		double totalComm = 0;
		
		ArrayList<Shares> cXShares = candidateResult.xDatasetShares;
		ArrayList<Shares> cWShares = candidateResult.wDatasetShares;
		ArrayList<AliasCipher[]> aliasXShares = candidateResult.aliasCipherShares;
		ArrayList<Shares> aliasWShares = candidateResult.aliasWShares;
		ArrayList<Shares> wShares = new ArrayList<Shares>();
		
		if(cWShares.size() == 0 && aliasWShares.size() ==0) {
			System.out.println("the size of cWShares and aliasWShares are zero");
			return ASE.share(0);
		}else if (cWShares.size() == 0) {
			for(int i = 0; i < aliasWShares.size(); i++) {
				wShares.add(aliasWShares.get(i));
			}
		}else if (aliasWShares.size() == 0) {
			for(int i = 0; i < cWShares.size(); i++) {
				wShares.add(cWShares.get(i));
			}
		}else {
			
			for(int i = 0; i < cWShares.size(); i++) {
				wShares.add(cWShares.get(i));
			}
			for(int i = 0; i < aliasWShares.size(); i++) {
				wShares.add(aliasWShares.get(i));
			}
		}
		
		ArrayList<Shares> wSumShares = new ArrayList<Shares>();
		wSumShares.add(wShares.get(0));
		for(int i = 1; i < wShares.size(); i++) {
			Shares tmp = ASE.ASE_add(wSumShares.get(i - 1), wShares.get(i));
			wSumShares.add(tmp);
		}
		
		
//		System.out.println("delta = " + delta);
		// 4*delta*share + delta*ASE_mul
		Shares rShares = PrivateAlgs.PURDG(delta, key);
		numOfShare = numOfShare + 4*key.delta;
		numOfASEMul = numOfASEMul + delta;
		
//		System.out.println("rShares = " + ASE.recover(rShares));
		
		int loc = 0;
		
		Shares rMulwSumShares = ASE.ASE_mul(rShares, wSumShares.get(wSumShares.size() - 1), key);
		numOfASEMul = numOfASEMul + 1;
		
		for(int i = 0; i < wSumShares.size(); i++) {
			Shares tmp = ASE.ASE_scalP(wSumShares.get(i), BigInteger.TWO.pow(delta));
			Shares zShares = ASE.ASE_sub(rMulwSumShares, tmp);
			
//			System.out.println("rMulwSumShares = " + ASE.recover(rMulwSumShares));
//			System.out.println("i = " + i + ";wSumShares = " + ASE.recover(wSumShares.get(i)));
			boolean flag = PrivateAlgs.PADC(zShares, eta); // 5 shares
			numOfShare = numOfShare + 5;
			
//			System.out.println("flag = " + flag);
			if(flag == true) {
				loc = i;
				continue;
			}
		}
		
//		System.out.println("loc = " + loc);
		
		
		if (cWShares.size() == 0) {
			
			AliasCipher[] aliasCipher = aliasXShares.get(loc);
			// Comm: (5*n + 4*delta + 5)*ASE_share + (4*n + delta + 4)*ASE_mul
			numOfShare = numOfShare + 5*aliasCipher.length + 4*key.delta + 5;
			numOfASEMul = numOfASEMul + 4*aliasCipher.length + key.delta + 4;
			
			totalComm = numOfShare*commSizeOfShare + numOfASERecover*commSizeOfASERecover + numOfASEMul*commSizeOfASEMul;
			Evaluation.queryComm = Evaluation.queryComm + totalComm;
			
			return Alias.aliasSampling(aliasCipher, key, delta, eta);
			
		}else if (aliasWShares.size() == 0) {
			
			totalComm = numOfShare*commSizeOfShare + numOfASERecover*commSizeOfASERecover + numOfASEMul*commSizeOfASEMul;
			Evaluation.queryComm = Evaluation.queryComm + totalComm;
			
			return cXShares.get(loc);
			
		}else {
			
			if(loc <= cWShares.size() - 1) {
				
				totalComm = numOfShare*commSizeOfShare + numOfASERecover*commSizeOfASERecover + numOfASEMul*commSizeOfASEMul;
				Evaluation.queryComm = Evaluation.queryComm + totalComm;
				
				return cXShares.get(loc);
			}else {
//				System.out.println("flag = true");
				AliasCipher[] aliasCipher = aliasXShares.get(loc - cWShares.size());
				// Comm: (5*n + 4*delta + 5)*ASE_share + (4*n + delta + 4)*ASE_mul
				numOfShare = numOfShare + 5*aliasCipher.length + 4*key.delta + 5;
				numOfASEMul = numOfASEMul + 4*aliasCipher.length + key.delta + 4;
				
				totalComm = numOfShare*commSizeOfShare + numOfASERecover*commSizeOfASERecover + numOfASEMul*commSizeOfASEMul;
				Evaluation.queryComm = Evaluation.queryComm + totalComm;
				
				return Alias.aliasSampling(aliasCipher, key, delta, eta);
			}
		}
		
		
	}
	
}
