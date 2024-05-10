package Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import BuildingBlocks.Util;
import BuildingBlocks.ASE;
import BuildingBlocks.ASE.Shares;
import EPWS.BSTCipher;
import EPWS.BSTCipher.CandidateResult;
import EPWS.BSTCipher.NodeCipher;
import EPWS.BSTPlain;
import EPWS.Key;
import EPWS.BSTPlain.Node;

public class Main {
	
	
	
	
	public static void main(String[] args) throws Exception {
		
		
		int n = 2000;
		int max = 50;
		ArrayList<Integer> xDataset = Util.genRandList(n, max);
		ArrayList<Integer> wDataset = Util.genRandList(n, max);
		
//		Util.printTwoList(xDataset, wDataset);
		
//		int beta = (int) Math.ceil((Math.log(max) / Math.log(2)));
//		int wSum = Util.sum(wDataset);
//		int delta = (int) Math.ceil((Math.log(wSum) / Math.log(2)));
//		int eta = 30;
//		
//		Key key = Key.InitKey(beta, delta, eta);
//		
//		Node tree = BSTPlain.bSTBuilding(xDataset, wDataset);
//		NodeCipher treeCipher = BSTCipher.bstEnc(tree, key.beta);
//		
//		Random rnd = new Random();
//		for(int i = 0; i < 10; i++) {
//			
//			int l = Math.abs(rnd.nextInt() % max);
//			int u = Math.abs(rnd.nextInt() % max);
//			
//			if(u < l) {
//				int tmp = l;
//				l = u;
//				u = tmp;
//			}
//			
//			System.out.print("l:" + l + "\t");
//			System.out.println("u:" + u);
//			
//			
//			ArrayList<Shares[]> queryCipher = BSTCipher.QueryEnc(l, u, beta);
//			CandidateResult candidateResult = BSTCipher.bstSearchCipher(treeCipher, queryCipher, key);
//			
//			Shares result = BSTCipher.resultVerify(candidateResult, delta, eta, key); 
//			
//			BigInteger sampledVal = ASE.recover(result);
//			
//			System.out.println(sampledVal);
//		}
		
		boolean flag = true;
		System.out.println();
		
		
	}

}
