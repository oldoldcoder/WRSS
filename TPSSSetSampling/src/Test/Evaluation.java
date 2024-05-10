package Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import BuildingBlocks.DataProcessing;
import BuildingBlocks.Util;
import BuildingBlocks.TPSS.Shares;
import BuildingBlocks.Alias.AliasCipher;
import EPWS.BSTCipher;
import EPWS.BSTPlain;
import EPWS.Key;
import EPWS.BSTCipher.CandidateResult;
import EPWS.BSTCipher.InternalNodeCipher;
import EPWS.BSTCipher.NodeCipher;
import EPWS.BSTPlain.Node;


public class Evaluation {
	
	public static Key key;
	public static double queryComm; 
	
	public static NodeCipher outsourcing(ArrayList<Integer> xDataset, ArrayList<Integer> wDataset, int betaVal) {
		
		int deltaVal = 20;
		int eta = 30;
		Key key = Key.InitKey(betaVal, deltaVal, eta);
		
		Node tree = BSTPlain.bSTBuilding(xDataset, wDataset);
		NodeCipher treeCipher = BSTCipher.bstEnc(tree, key.beta);
		return treeCipher;
		
	}
	
	public static int sizeOfBSTCipher(NodeCipher treeCipher, int beta) {
		
		Stack<NodeCipher> nodeStack = new Stack<NodeCipher>();
		nodeStack.push(treeCipher);
		int sizeOfEachShare = 384; // 6 64-bit data
		int numShares = 0;
		int numNode = 0;
		
		while(!nodeStack.isEmpty()) {
			
			NodeCipher node = nodeStack.pop();
			numNode = numNode + 1;
			
			if(node.isLeaf == true) {	
				numShares = numShares + beta + 2;
			}else {
				
				InternalNodeCipher internalNodeCipher = (InternalNodeCipher) node;
				AliasCipher[] aliasCiphers = internalNodeCipher.ACipher_node;
				numShares = numShares + beta*3 + 2 + aliasCiphers.length*4;
				
				NodeCipher left = internalNodeCipher.left;
				NodeCipher right = internalNodeCipher.right;
				if(left != null) {
					nodeStack.push(left);
				}
				if(right != null) {
					nodeStack.push(right);
				}
				
				
//				System.out.println("numShares:" + numShares);
			}
		}
		
		int bitSizeOfTree = numShares*sizeOfEachShare + numNode;
		
//		System.out.println("bitSizeOfTree:" + bitSizeOfTree);
		
		return bitSizeOfTree;
	}
	
	public static Shares queryProcessing(NodeCipher treeCipher, ArrayList<Shares[]> queryCipher) throws Exception {
		
		CandidateResult candidateResult = BSTCipher.bstSearchCipher(treeCipher, queryCipher, key);
		Shares result = BSTCipher.resultVerify(candidateResult, key.delta, key.eta, key);
		
		return result; 		
	}
	
	public static void outsourcingEvalWithN() throws Exception {
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int cycle = 50;
		
		int[] n = {20000, 30000, 40000, 50000, 60000};
		int betaVal = 16;
		
		// Outsourcing time with n
		for(int i = 0; i < n.length; i++) {
			
			int nVal = n[i];
			ArrayList<int[]> database = DataProcessing.readData(filename, splitString, nVal);
			ArrayList<Integer> xDataset = new ArrayList<Integer>();
			ArrayList<Integer> wDataset = new ArrayList<Integer>();
			for(int j = 0; j < nVal; j++) {
				xDataset.add(database.get(j)[1]); 
				wDataset.add(database.get(j)[0]);
			}
			
			double time = 0.0;
			double bitSizeOfTree = 0;
			for(int l = 0; l < cycle; l++) {
				
				double start = System.currentTimeMillis();
				NodeCipher nodeCipher = outsourcing(xDataset, wDataset, betaVal);
				double end = System.currentTimeMillis();
				
				time = time + (end - start);
				bitSizeOfTree = bitSizeOfTree + sizeOfBSTCipher(nodeCipher, betaVal);
			}
			
			time = time/cycle;
			bitSizeOfTree = bitSizeOfTree/cycle/1024/1024/8;
			
			System.out.println("n = " + nVal + ";" + "Outsourcing time is " + time + " ms, Size of tree is " + bitSizeOfTree + " MB");		
		}
		
	}
	
	public static void outsourcingEvalWithBeta() throws Exception {
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int cycle = 50;
		
		int nVal = 30000;
		int[] beta = {16, 19, 22, 25, 28};
		
		// Outsourcing time with n
		for(int i = 0; i < beta.length; i++) {
			
			int betaVal = beta[i];
			ArrayList<int[]> database = DataProcessing.readData(filename, splitString, nVal);
			ArrayList<Integer> xDataset = new ArrayList<Integer>();
			ArrayList<Integer> wDataset = new ArrayList<Integer>();
			for(int j = 0; j < nVal; j++) {
				xDataset.add(database.get(j)[1]); 
				wDataset.add(database.get(j)[0]);
			}
			
			double time = 0.0;
			double bitSizeOfTree = 0;
			
			for(int l = 0; l < cycle; l++) {
				double start = System.currentTimeMillis();
				NodeCipher nodeCipher = outsourcing(xDataset, wDataset, betaVal);
				double end = System.currentTimeMillis();
				
				time = time + (end - start);
				bitSizeOfTree = bitSizeOfTree + sizeOfBSTCipher(nodeCipher, betaVal);
			}
			
			time = time/cycle;
			bitSizeOfTree = bitSizeOfTree/cycle/1024/1024/8;
			
			System.out.println("beta = " + betaVal + ";" + "Outsourcing time is" + time + " ms, Size of tree is " + bitSizeOfTree + " MB");		
		}
		
	}
	
	public static void queryEncWithBeta() {
		
		Random rnd = new Random();
		int[] beta = {16, 19, 22, 25, 28};
		int sizeOfEachShare = 384;
		
		int cycle = 1000;
		// Query encryption
		for(int i = 0; i < beta.length; i++) {
			
			int betaVal = beta[i];
			
			int l = (int) (rnd.nextInt() % Math.pow(2, 16));
			l = Math.abs(l);
			int u = l + 10;
			
			double start = System.currentTimeMillis();
			for(int j = 0; j < cycle; j++) {
				ArrayList<Shares[]> queryCipher = BSTCipher.QueryEnc(l, u, betaVal); 
			}
			double end = System.currentTimeMillis();
			double time = (end - start)/cycle;
			
			double bitSizeOfTree = 2*betaVal*sizeOfEachShare;
			bitSizeOfTree = bitSizeOfTree/8;
			
			System.out.println("beta = " + betaVal + "; Query encryption time is " + time + " ms, Size of tree is " + bitSizeOfTree + " B");		
		}
				
		
	}
	
	public static void queryProcessingEvalWithN() throws Exception {
		
		Random rnd = new Random();
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int cycle = 10;
		
		int[] n = {20000, 30000, 40000, 50000, 60000};
		int betaVal = 16;
		int delta = 20;
		int eta = 30;
		key = Key.InitKey(betaVal, delta, eta);
		
		// Outsourcing time with n
		for(int i = 0; i < n.length; i++) {
			
			int nVal = n[i];
			ArrayList<int[]> database = DataProcessing.readData(filename, splitString, nVal);
			ArrayList<Integer> xDataset = new ArrayList<Integer>();
			ArrayList<Integer> wDataset = new ArrayList<Integer>();
			for(int j = 0; j < nVal; j++) {
				xDataset.add(database.get(j)[1]); 
				wDataset.add(database.get(j)[0]);
			}
			NodeCipher nodeCipher = outsourcing(xDataset, wDataset, betaVal);
			int loc = rnd.nextInt() % nVal;
			loc = Math.abs(loc);
			
//			System.out.println("finish encryption");
			
			double time = 0.0;
			double bitSizeOfTree = 0;
			for(int j = 0; j < cycle; j++) {
//				System.out.println("j = " + j);
				
				int l = 1500;
				int u = l + 500;
				ArrayList<Shares[]> queryCipher = BSTCipher.QueryEnc(l, u, betaVal);
				
				double start = System.currentTimeMillis();
				queryComm = 0.0;
				Shares resultShares = queryProcessing(nodeCipher, queryCipher);
				double end = System.currentTimeMillis();
				
				time = time + (end - start);
				bitSizeOfTree = bitSizeOfTree + queryComm/1024/8;
			}
			
			time = time/cycle;
			bitSizeOfTree = bitSizeOfTree/cycle/1024;
			
			System.out.println("n = " + nVal + ";" + "Query Processing time is " + time + " ms, Communication overhead is " + bitSizeOfTree + " MB");		
		}
		
	}
	
	public static void queryProcessingEvalWithBeta() throws Exception {
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int cycle = 10;
		
		int nVal = 30000;
		int[] beta = {16, 19, 22, 25, 28};
		
		// Outsourcing time with n
		for(int i = 0; i < beta.length; i++) {
			
			int betaVal = beta[i];
			int delta = 20;
			int eta = 30;
			key = Key.InitKey(betaVal, delta, eta);
			
			ArrayList<int[]> database = DataProcessing.readData(filename, splitString, nVal);
			ArrayList<Integer> xDataset = new ArrayList<Integer>();
			ArrayList<Integer> wDataset = new ArrayList<Integer>();
			for(int j = 0; j < nVal; j++) {
				xDataset.add(database.get(j)[1]); 
				wDataset.add(database.get(j)[0]);
			}
			NodeCipher nodeCipher = outsourcing(xDataset, wDataset, betaVal);
			
			double time = 0.0;
			double bitSizeOfTree = 0;
			for(int j = 0; j < cycle; j++) {
//				System.out.println("j = " + j);
				
				int l = 1500;
				int u = l + 500;
				ArrayList<Shares[]> queryCipher = BSTCipher.QueryEnc(l, u, betaVal);
				
				double start = System.currentTimeMillis();
				queryComm = 0.0;
				Shares resultShares = queryProcessing(nodeCipher, queryCipher);
				double end = System.currentTimeMillis();
				
				time = time + (end - start);
				bitSizeOfTree = bitSizeOfTree + queryComm/1024/8;
			}
			
			time = time/cycle;
			bitSizeOfTree = bitSizeOfTree/cycle/1024;
			
			System.out.println("beta = " + betaVal + ";" + "Query Processing time is " + time + " ms, Communication overhead is " + bitSizeOfTree + " MB");		
		}
		
	}
	
	public static void queryProcessingEvalWithRange() throws Exception {
		
		String filename = "src/cleanData.txt";
		String splitString = ",";
		int cycle = 10;
		
		int nVal = 30000;
		int betaVal = 16;
		int delta = 20;
		int eta = 30;
		key = Key.InitKey(betaVal, delta, eta);
		
		int[] range = {200, 400, 600, 800, 1000};
		
		
		// Outsourcing time with n
		for(int i = 0; i < range.length; i++) {
			
			ArrayList<int[]> database = DataProcessing.readData(filename, splitString, nVal);
			ArrayList<Integer> xDataset = new ArrayList<Integer>();
			ArrayList<Integer> wDataset = new ArrayList<Integer>();
			for(int j = 0; j < nVal; j++) {
				xDataset.add(database.get(j)[1]); 
				wDataset.add(database.get(j)[0]);
			}
			NodeCipher nodeCipher = outsourcing(xDataset, wDataset, betaVal);
			
			double time = 0.0;
			double bitSizeOfTree = 0;
			for(int j = 0; j < cycle; j++) {
//				System.out.println("j = " + j);
				
				int l = 1500;
				int u = l + range[i];
				ArrayList<Shares[]> queryCipher = BSTCipher.QueryEnc(l, u, betaVal);
				
				double start = System.currentTimeMillis();
				queryComm = 0.0;
				Shares resultShares = queryProcessing(nodeCipher, queryCipher);
				double end = System.currentTimeMillis();
				
				time = time + (end - start);
				bitSizeOfTree = bitSizeOfTree + queryComm/1024/8;
			}
			
			time = time/cycle;
			bitSizeOfTree = bitSizeOfTree/cycle/1024;
			
			System.out.println("range = " + range[i] + ";" + "Query Processing time is " + time + " ms, Communication overhead is " + bitSizeOfTree + " MB");		
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Outsourcing time with N");
		outsourcingEvalWithN();
		
		System.out.println("Outsourcing time with beta");
		outsourcingEvalWithBeta();
		
		System.out.println("Query encryption time with beta");
		queryEncWithBeta();
		
		System.out.println("Query processing with N");
		queryProcessingEvalWithN();
		
		System.out.println("Query processing with beta");
		queryProcessingEvalWithBeta();
		
		System.out.println("Query processing with range");
		queryProcessingEvalWithRange();
		
	}
	
	
	
}
