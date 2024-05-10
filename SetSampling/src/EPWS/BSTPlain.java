package EPWS;

import java.util.ArrayList;
import java.util.Arrays;
import BuildingBlocks.Util;
import BuildingBlocks.Alias;
import BuildingBlocks.Alias.AliasPlain;

public class BSTPlain {
	
	public static BSTPlain bstPlain = new BSTPlain();
	
	public class Node {
		public boolean isLeaf;
	}
	
	public class LeafNode extends Node {
		
		public ArrayList<Integer> xDataset;
		public ArrayList<Integer> wDataset;
		
		public LeafNode(ArrayList<Integer> xDataset, ArrayList<Integer> wDataset) {
			this.xDataset = xDataset;
			this.wDataset = wDataset;
			this.isLeaf = true;
		}
	}
	
	public class InternalNode extends Node {
		
		public int v_node;
		public int l_node;
		public int u_node;
		public AliasPlain[] A_node;
		public int w_A_node;
		public Node left;
		public Node right;
		
		public InternalNode(int v_node, int l_node, int u_node, AliasPlain[] a_node, int w_A_node, Node left, Node right) {
			
			this.v_node = v_node;
			this.l_node = l_node;
			this.u_node = u_node;
			this.A_node = a_node;
			this.w_A_node = w_A_node;
			this.left = left;
			this.right = right;
			this.isLeaf = false;
		}
		
	}
	
	public static Node bSTBuilding(ArrayList<Integer> xDataset, ArrayList<Integer> wDataset) {
		
		int[] result = Util.median(xDataset);
		int l_node = result[0];
		int median = result[1];
		int u_node = result[2];

		if(median == u_node) {
			LeafNode leafNode = bstPlain.new LeafNode(xDataset, wDataset);
			return leafNode;
		}else {
			
			int w_node = Util.sum(wDataset);
			AliasPlain[] aliasPlain = Alias.constructAlias(xDataset, wDataset);
			
			ArrayList<Integer> leftXDataset = new ArrayList<Integer>();
			ArrayList<Integer> leftWDataset = new ArrayList<Integer>();
			ArrayList<Integer> rightXDataset = new ArrayList<Integer>();
			ArrayList<Integer> rightWDataset = new ArrayList<Integer>();
			
			for(int i = 0; i < xDataset.size(); i++) {
				
				if(xDataset.get(i) <= median) {
					leftXDataset.add(xDataset.get(i));
					leftWDataset.add(wDataset.get(i));
				}else {
					rightXDataset.add(xDataset.get(i));
					rightWDataset.add(wDataset.get(i));
				}
			}
			Node leftNode;
			Node rightNode;
			if(leftXDataset.size() != 0) {
				leftNode = bSTBuilding(leftXDataset, leftWDataset);
			}else {
				leftNode = null;
			}
			if(rightXDataset.size() != 0) {
				rightNode = bSTBuilding(rightXDataset, rightWDataset);
			}else {
				rightNode = null;
			}
			InternalNode internalNode = bstPlain.new InternalNode(median, l_node, u_node, aliasPlain, w_node, leftNode, rightNode);
			return internalNode;
		}
	}
	
}
