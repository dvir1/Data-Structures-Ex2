import java.util.Iterator;

/**
 * FibonacciHeap
 * 
 * Second project in Data Structures, in collaboration with another students
 * 
 * Authors:
 * Dvir
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap {
	private HeapList trees;
	private static int totalLinks;
	private static int totalCuts;
	private static int marks;
	private HeapNode min;
	private int size;
	
	public FibonacciHeap(){
		this.trees = new HeapList();
		this.min = new HeapNode(Integer.MAX_VALUE);
		marks = 0;
		this.size = 0;
	}
	
	/**
	 * increase marks of the Heap
	 * used for potential function
	 * @param i how much to increase by
	 */
	private static void increaseMarks(int i) {
		marks += i;
	}

	/**
	 * public boolean empty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean empty() {
		return this.trees.size() == 0;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 */
	public HeapNode insert(int key) {
		HeapNode newNode = new HeapNode(key);
		this.trees.add(newNode);
		this.size++;
		this.min=chooseTheSmallerNode(this.min,newNode);
		
		
		return newNode;
	}
	
	/**
	 * Returns number of trees in the root list of the heap
     * @return number of trees in root list
	 */
	private int numberOfTrees() {
		return this.trees.size();
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin()
    {
		//##part I
    	if (this.empty()){
    		return;
    	}
    	if(this.size()==1){
    		emptyTheHeap();
    		return;
    	}
    	removeMinNodeAndUpgradeItsChildren();
    	
    	//##part II
    	double boundingRatio=1.4404;
    	double maxTreeRank = (Math.log(this.size())/Math.log(2))*boundingRatio;
    	//according to slide 84 the maximal tree rank is bounded by maxTreeRank
    	int roundedMaxTreeRank = (int) Math.floor(maxTreeRank)+1;
    	HeapNode[] Bins = new HeapNode[roundedMaxTreeRank];
    	successiveLinking(Bins);
    	
    	//##part III
    	UpdateNewRootsAndMinNode(Bins);
    	
    	
    }
	
	/**emptying the list**/
	public void emptyTheHeap(){
		
		this.trees = new HeapList();
		marks = 0;
		this.size=0;
		min = null;
	}
    /**slide 37-38
      * removing the minimal root node from the list trees and upgrading it's list of
      * first children  to be new roots
      * Notice!: the field min becomes null after calling this function
     * **/
    public void removeMinNodeAndUpgradeItsChildren(){
    	
    	
    	HeapNode minNode = this.min;
    	
    	this.trees.remove(minNode);
    	for(HeapNode child :minNode.children){
    		child.parent=null;
    		this.trees.add(child);
    	}
    	this.min=null;
    	this.size--;
    	
    }
    /**Commit successive linking to the roots in  this.trees in the array
      * HeapNode[]Bins - according to slides 38-47 **/
    public void successiveLinking(HeapNode[]Bins){
    	for(HeapNode root:this.trees){
    		continuousAddition(Bins,root,root.getRank());
    		
    	}
    }
    /**insert root in Bins[index]
      * if the bin Bins[index] has a root in it the function will link the 2 trees
      * and will continue the successive linking as described in slides 38-47 in the
      * next bins (much like binary addition)**/
    public void continuousAddition(HeapNode[]Bins,HeapNode root,int index){
    	if(Bins[index]==null){
    		Bins[index]=root;
    		return;
    	}
    	continuousAddition(Bins,linkTrees(root,Bins[index]),index+1);
    	Bins[index]=null;
    	
    	
    }
    /**slide 12
     * @pre trees contains root1 and root2
     * returns the new root of the new tree**/
    public HeapNode linkTrees(HeapNode root1,HeapNode root2){
    	if(root1.getRank()!=root2.getRank()){
    		System.out.print("problem! trying to link unmatched ranks");
    		return null;
    	}
    	HeapNode smallRoot;
		HeapNode bigRoot;
    	if (root1.getKey()<=root2.getKey()){
    		 smallRoot=root1;
    		 bigRoot=root2;
    	}
    	else{
    		 smallRoot=root2;
    		 bigRoot=root1;
    	}
    	smallRoot.children.add(bigRoot);
    	bigRoot.parent=smallRoot;
    	totalLinks++;
    	return smallRoot;
    }
    /**updating the list this.trees to have the trees in the array bins
      * while looping over the roots of the heap, the method maintain and
      * update this.min**/
    public void UpdateNewRootsAndMinNode(HeapNode[] bins){
    	
    	boolean searchForMin=this.min==null;
    	HeapNode tmpMin = new HeapNode(Integer.MAX_VALUE);
    	
    	this.trees = new HeapList();
    	for(int i=0;i<bins.length;i++){
    		if(bins[i]!=null){
    			bins[i].parent=null;
    			this.trees.add(bins[i]);
    			if(searchForMin){
    				tmpMin=chooseTheSmallerNode(tmpMin,bins[i]);
    			}
    		}
    	}
    	if(searchForMin){
    		this.min=tmpMin;
    	}
    	
    }
    /**returns the node who has the smaller key among a,b**/
    public HeapNode chooseTheSmallerNode(HeapNode a,HeapNode b){
    	if(a.key<=b.key){
    		return a;
    	}
    	return b;
    }

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 * If heap is empty returns null
	 */
	public HeapNode findMin() {
		if (!this.empty())
			return min;
		return null;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {
		if (heap2.size()>0)
		{
			this.trees.concat(heap2.trees); // concatenate two lists
			increaseSize(heap2.size()); // update num of nodes
			this.min = chooseTheSmallerNode(min, heap2.min); // update min
		}
	}

	/**
	 * Increases the number of nodes in heap by size2 
	 * @param size2
	 */
	private void increaseSize(int size2) {
		this.size += size2;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return size; // should be replaced by student code
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		int[] arr = new int[getMaxRank()+1]; // include max rank in array 
		for (HeapNode node : trees)
			arr[node.getRank()] += 1;
		return arr; // to be replaced by student code
	}
	
	/**
	 * Finds the highest rank of a root in the heap
	 * @return Max rank of tree in the heap's roots
	 */
	private int getMaxRank() {
		int max = 0;
		for (HeapNode node : trees) {
			max = Math.max(node.getRank(), max);
		}
		return max;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 * should probably implement this with decreaseKey+deleteMin
	 */
	public void delete(HeapNode x) {
		this.decreaseKey(x,x.key-(this.min.key-1));
		this.deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		HeapNode parent = x.getParent();
		x.setKey(x.getKey()-delta);
		if (parent != null && x.getKey() < parent.getKey()) {
			cut(parent, x);
			x.unmark();
			cascadingCuts(parent);
		}
		if (x.getKey() < findMin().getKey()) {
			this.min = x;
		}
	}
	/**
	 * performs cascading cuts operation recursively
	 * @param y the node from which to start the cascading cuts
	 */
	private void cascadingCuts(HeapNode y) {
		HeapNode z = y.getParent();
		if (z != null) {
			if (!y.isMarked()) {
				y.mark();
			}
			else {
				cut(z, y);
				cascadingCuts(z);
			}
		}		
	}
	/**
	 * Cuts child from parent, adding the child to the list of roots.
	 * @param parent
	 * @param child
	 */
	private void cut(HeapNode parent, HeapNode child) {
		parent.cut(child);
		trees.add(child);
		child.unmark();
		FibonacciHeap.increaseCuts(1);
		
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		return this.numberOfTrees()+2*marks;
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {
		return totalLinks;
	}
	
	/**
	 * 
	 * @param node the node to check
	 * @return true iff node is a root of the heap
	 */
	private boolean isRoot(HeapNode node) {
		return trees.contains(node);
	}
	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {
		return totalCuts; // should be replaced by student code
	}
	/**
	 * increases the number of cuts performed on the heap
	 * @param i - number of cuts to add
	 */
	public static void increaseCuts(int i) {
		totalCuts += i;
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {
		private HeapNode parent;
		private HeapList children;
		private HeapNode next; // next node
		private HeapNode prev; // prev node
		public int key;
		private boolean mark;
		
		public HeapNode(int key, HeapList children, boolean mark, HeapNode next, HeapNode prev) {
			this.key = key;
			this.children = children;
			this.next = next;
			this.prev = prev;
		}
		
		public HeapNode(int key) {
			this(key, new HeapList(), false, null, null);
		}
		
		public void unmark() {
			if (this.isMarked()){
				this.mark = false;
				FibonacciHeap.increaseMarks(-1);
			}
			
		}

		public void setKey(int i) {
			this.key = i;
			
		}

		public int getKey() {
			return this.key;
		}
		
		/**
		 * check if node is marked, for cascading cuts
		 * @return true iff node has been marked
		 */
		public boolean isMarked() {
			return mark;
		}
		
		/**
		 * Mark the current node, if it's not a root node
		 * Assumes node is unmarked 
		 */
		public void mark() {
			if (!this.isMarked() && !FibonacciHeap.this.isRoot(this)){
				this.mark = true;
				FibonacciHeap.increaseMarks(1);
			}
		}
		
		/**
		 * Cuts the child from the parent
		 * assumes this == child.parent
		 * @param child
		 * @return child
		 */
		public void cut(HeapNode child) {
			this.children.remove(child);
			child.removeParent();
		}
		
		/**
		 * remove's this node's parent
		 */
		private void removeParent() {
			this.setParent(null);
		}
		
		private void setParent(HeapNode newParent) {
			this.parent = newParent;
		}

		public HeapNode getParent() {
			return parent;
		}

		public int getRank() {
			return this.children.size();
		}
		
		private HeapNode getNext() {
			return next;
		}
		
		private void setNext(HeapNode next) {
			this.next = next;
		}
		
		private HeapNode getPrev() {
			return prev;
		}
		
		private void setPrev(HeapNode prev) {
			this.prev = prev;
		}
	}
	/**
	 * A self implemented list to contain HeapNodes 
	 *
	 */
	public class HeapList implements Iterable<HeapNode>{
		/*
		 * listSize=0 iff first=last=null
		 */
		private int listSize; // the size of the list
		private HeapNode first; // pointer to the beginning of the list
		private HeapNode last; // pointer to the end of the list
		
		/**
		 * Constractor
		 */
		public HeapList() {
			listSize = 0;
			first = last = null;
		}

		/**
		 * concatenating list2 at the end of current list in O(1) in WC
		 * @pre list2.size()>0
		 */
		public void concat(HeapList list2) {
			// change the pointers
			this.last.setNext(list2.first);
			this.first.setPrev(list2.last);
			list2.first.setPrev(this.last);
			list2.last.setNext(this.first);
			
			this.last = list2.last; // update last
			
			this.listSize += list2.size(); // update number of trees in trees list
		}

		/**
		 * Returns the size of the list
		 */
		public int size() {
			return listSize;
		}
		
		/**
		 * Checks if node is in the list
		 */
		public boolean contains(HeapNode node) {
			// check if node is the first or last node, at most 2 nodes in list
			if (node==first || node==last)
				return true;
			else // at least 3 nodes in list
			{
				// scan the list
				HeapNode current = first.getNext();
				while (current!=last)
				{
					if (current==node)
						return true;
					current = current.getNext();
				}
				return false;
			}
		}
		
		/**
		 * Removes node from list
		 */
		public void remove(HeapNode node) {
			// cut node from next and prev, then connect them
			HeapNode nextNode = node.getNext();
			HeapNode prevNode = node.getPrev();
			nextNode.setPrev(prevNode);
			prevNode.setNext(nextNode);
			node.setNext(null);
			node.setPrev(null);
			// update first and last
			if (listSize==1)
			{
				first = last = null;
			}
			else
			{
				if (node==first)
					first = nextNode;
				else if (node==last)
					last = prevNode;
			}
			decSize(); // decrease list size by 1
		}
		
		/**
		 * Decreases list size by 1
		 */
		private void decSize() {
			if (listSize>0)
				listSize--;
			else
				System.out.println("invalid decSize() call");
		}

		/**
		 * Inserts newNode at the end of list
		 */
		public void add(HeapNode newNode) {
			// update pointers
			if (listSize==0)
			{
				newNode.setNext(newNode);
				newNode.setPrev(newNode);
				first = last = newNode;
			}
			else
			{
				newNode.setNext(first);
				newNode.setPrev(last);
				first.setPrev(newNode);
				last.setNext(newNode);
				last = newNode;
			}
			incSize(); // increase list size by 1
		}
		
		/**
		 * Increases list size by 1
		 */
		private void incSize() {
			listSize++;
		}

		@Override
		/**
		 * Implemented to enable for loops
		 * Returns HeapListIterator item
		 */
		public Iterator<HeapNode> iterator() {
			return new HeapListIterator();
		}
		
		/**
		 * Implemented to enable for loops
		 */
		public class HeapListIterator implements Iterator<HeapNode>
		{
			private HeapNode nextNode; // pointer to next node in list
			private boolean firstIteration; // indicates whether it's the first iteration
			
			/**
			 * Constractor
			 */
			public HeapListIterator() {
				nextNode = first;
				firstIteration = true;
			}

			@Override
			/**
			 * Checks if nodes left in list
			 */
			public boolean hasNext() {
				if (listSize>0)
				{
					if (firstIteration) // always return first item
					{
						firstIteration = false;
						return true;
					}
					return nextNode != first;
				}
				else
					return false;
			}

			@Override
			/**
			 * Returns the next node in list
			 */
			public HeapNode next() {
				HeapNode current = nextNode;
				nextNode = nextNode.getNext();
				return current;
			}
			
			/**
			 * Unimplemented method, as learned in Software1
			 */
			public void remove() { throw new UnsupportedOperationException(); }
		}
	}
}
