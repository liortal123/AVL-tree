/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	
	private IAVLNode root;
	private IAVLNode min; //node with min key in tree
	private IAVLNode max; //node with max key in tree
	private int size; //the size of the tree
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
    return root==null; 
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	if (empty())
		return null;
	IAVLNode node = root;
	while (node.isRealNode())  
	{
		if (node.getKey()==k) 
			return node.getValue();
		if (node.getKey()>k)
			node=node.getLeft();
		else node =node.getRight();
	}
	return null; 
  }
  /**
   * private IAVLNode searchNode(int k,amount)
   * returns the node with key k, if not in tree returns virtual node
   * updating the nodes's size in the search route from root to k (not including k)
   * if called by insert amount is 1
   * if called by delete amount is -1
   * if called for a normal search amount is 0
  **/
  private IAVLNode searchNode(int k, int amount) {
	  IAVLNode node = root;
	  while (node.isRealNode())
			{
			if (node.getKey()==k)
				return node; //k was found returning the node
			node.setSize(node.getSize()+amount); //updating node's size to size+ amount
			if (k<node.getKey())  //normal search
				node = node.getLeft();
			else node = node.getRight();
			}
	return node; //returning a virtual node
  }

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	 IAVLNode newNode = new AVLNode(k,i); //creating a new leaf
	 if (empty()) { //if the tree is empty updating the new root
			this.root=newNode;//newNode default subtree size is 1
			this.size = 1; //updating the necessary fields
			this.min = newNode;
			this.max = newNode;
			return 0;
	 }

	//finding the place to be inserted
	IAVLNode node = searchNode(k,0); //we don't know if k is already in tree so we don't update sizes yet.  
	if (node.isRealNode())//k was already in tree. 
		return -1;
	 
	 //if we are here k is not in the tree already. needs to be inserted
	 //node is a virtual Node, setting parent and child
	 IAVLNode parent = node.getParent();
	 node = newNode;
	 if (k<parent.getKey())
		 parent.setLeft(node);
	 else parent.setRight(node);
	 
	 //after k was inserted to tree we update size of each node
	 //in the root from root to k, and update the size of the overall tree
	 searchNode(k, 1);
	 size ++;
	 //updating min and max
	 if (min!=null && max!=null) {   //in split and join, it's possible to call insert 
		 if (k<min.getKey()) 		//while min and max are still null (to be updated later)
			 min = newNode;
		 if (k>max.getKey())
			 max = newNode;
	 }
	 return balanceIn(node);

   }
   private int balanceIn(IAVLNode child) { 
	   int count=0;
	   IAVLNode parent = child.getParent();
	   while (parent!=null && parent.getHeight()==child.getHeight()) {
		   char childType = 'R'; //checking if child is a right child or a left child
			 if (parent.getLeft().getKey() == child.getKey())
				 childType = 'L';
			int caseNum = checkCaseIn(child,parent,childType);
				 if (caseNum==0) { //case 0 need to promote parent
					 parent.setHeight(parent.getHeight()+1);  //promoting parent
					 child = parent; //problem could now be between parent and his parent
					 parent = child.getParent(); //updating child and parent
					 count++;
				 }
				 else if (caseNum==1) {
					 parent.setHeight(parent.getHeight()-1);//demoting parent
					 if (childType=='L') 
						 rightRotation(parent); //doing a rotation between parent and left child 
					 else leftRotation(parent); //doing a rotation between parent and right child 
					 return count+2; //problem solved returning the count
						 
				 }
				 else if (caseNum==2){
					 parent.setHeight(parent.getHeight()-1);//updating the new ranks- demote parent
					 child.setHeight(child.getHeight()-1); //demote child
					 if (childType=='L') {
						 child.getRight().setHeight(child.getRight().getHeight()+1);//promote right child
						 leftRotation(child);
						 rightRotation(parent);
					 }
					 else { 
						 child.getLeft().setHeight(child.getLeft().getHeight()+1);//promote left child
						 rightRotation(child);
						 leftRotation(parent);
					 }
					 return count+5; //problem solved returning the count
				 } 
				 else {//caseNum == 3
					 child.setHeight(child.getHeight()+1);//promoting child
					 if (childType=='L') 
						 rightRotation(parent);
					 else leftRotation(parent);
					 count = count + 2;
					//problem not solved pointers changed during rotation
					 parent = child.getParent();  //updating the new parent 
				 }
			 }
		return count;	
		 
   }
   
   private int checkCaseIn(IAVLNode child, IAVLNode parent, char type) { //'L' means left child, 'R' means right	   
	   if (type=='L') {
		   if (parent.getHeight() - (parent.getRight()).getHeight() == 1)
			   return 0;  //case zero need to promote parent
		   else { //rank difference is 2
			   if (child.getHeight()-child.getLeft().getHeight()==1 && child.getHeight()-child.getRight().getHeight()==1)//case from forum
				   return 3;
			   if (child.getHeight()-(child.getLeft()).getHeight() == 1)
				   return 1; // single rotation
			   return 2; //double rotation
		   }
	   }//right child
	   if (parent.getHeight() - (parent.getLeft()).getHeight() == 1)
		   return 0;  //case zero need to promote parent
	   else { //rank difference is 2
		   if (child.getHeight()-child.getLeft().getHeight()==1 && child.getHeight()-child.getRight().getHeight()==1)//case from forum
			   return 3;
		   if (child.getHeight()-(child.getLeft()).getHeight() == 1)
			   return 2; // double rotation
		   return 1;
	   }
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   if (empty()) 
			return -1;
	   //find node to delete
	   IAVLNode node = searchNode(k, 0); //we don't know if k in tree so we don't update sizes on the route yet
	   if (!node.isRealNode()) //if node was not found
		   return -1;

	   //updating min and max
	   if (min!=null &&  max!=null) {
		   if (k==min.getKey())
			   min = successor(node);
		   if (k==max.getKey())
			   max = predecessor(node);
	   }
	   
	   size--;
	   node = delteNode(node); //node that returns is the node to start fixing the rank problem (could be suc, or child)
	   if (node==null)
		   return 0;
	   return balanceDel (node);
   }
   
   private IAVLNode delteNode(IAVLNode node) {
	   String nodeType = nodeType(node); // nodeType() returns "leaf" or "unary" or "other" 
	   IAVLNode parent = node.getParent();
	   if (parent!=null) { //deleting node is not the root
		   char childType = 'R';
		   if (parent.getLeft().getKey() == node.getKey())
			   childType = 'L';
		 
		   if (nodeType.equals("leaf"))
			   node = deleteLeaf(node,childType);
		 
		   else if (nodeType.equals("unary")) 
			   node = deleteUnary(node,childType);
	
		   //nodeType is with two children			
		   else node = deleteOther(node,childType); //the node that returns is the successor child, could be virtual  
		   return node;   
	   }
	   //parent == null, delete root
	   else return deleteRoot(node,nodeType);
	   }
   
   private IAVLNode deleteRoot(IAVLNode node, String nodeType) {
	   if (size==0) {//root is a leaf (we already decreased size by one in main delete
		   root = null;
		   return null; } // not necessary to balance
	   
	   if (nodeType.equals("unary")) {
		   //there are only 2 nodes in the tree before deletion of the root, updating the new root
		   if (node.getRight().isRealNode()) {
			   this.root = node.getRight();
			   root.setParent(null);
		   }
		   else { 
			   this.root = node.getLeft();
			   root.setParent(null);
		   }
		   return null; // not necessary to balance
	   }
	   
	   //root has 2 children
	   IAVLNode suc = successor(node); //to find successor we go right and all the way left 
	   //need to update all the nodes in route from root to suc, to size--
	   searchNode(suc.getKey(), -1);
		 //successor can only have right child - means successor is unary (right child can be virtual)
		 if (suc.getParent().getLeft().getKey()==suc.getKey()) //suc is left child
			 suc.getParent().setLeft(suc.getRight());
		 else suc.getParent().setRight(suc.getRight()); //suc is a right child
		 IAVLNode returnNode = suc.getRight(); //return node is node to start balancing. could be a virtual node
		 
		 //suc was deleted from tree because no one points at him
		 //updating the suc to have his parent size,height and children
		 suc.setHeight(node.getHeight()); //suc will replace node, we update suc rank.. 
		 suc.setLeft(node.getLeft());
		 suc.setRight(node.getRight());
		 suc.setSize(node.getSize());
		 suc.setParent(null);
		 this.root = suc;
		 return returnNode;
   }
	   
   private IAVLNode deleteLeaf(IAVLNode node,char childType) {
	   searchNode(node.getKey(), -1); //need to update all the nodes in route from root to node, to size--
	   IAVLNode parent = node.getParent();
	   IAVLNode virtNode = new AVLNode(); //creating a new virtual node
	   if (childType=='R')  
			parent.setRight(virtNode);  
	   else 
		   parent.setLeft(virtNode);  
	   return virtNode;
   }
   
   private IAVLNode deleteUnary (IAVLNode node, char childType) {
	   searchNode(node.getKey(), -1); //need to update all the nodes in route from root to node, to size--
	   IAVLNode parent = node.getParent();
	   if (childType=='R') {
		   //the node is unary and we check if it has right or left child
	   if (node.getRight().isRealNode()) { //has right child
			 parent.setRight(node.getRight()); //deleting node, linking node.rightchild to node.parent
			 node = node.getRight(); } //keeping the new node, to fix ranking problem 
	   else { //unary node, if doesn't have a right child, it has left child
			 parent.setRight(node.getLeft()); //deleting node, linking node.leftchild to node.parent
			 node = node.getLeft(); } //keeping the new node, to fix ranking problem 
		 }
	   else { //childType=='L'
		   if (node.getRight().isRealNode()) { //has right child
				 parent.setLeft(node.getRight()); //deleting node, linking node.rightchild to node.parent
				 node = node.getRight(); } //keeping the new node, to fix ranking problem 
		   else { //unary node, if doesn't have a right child, it has left child
				 parent.setLeft(node.getLeft()); //deleting node, linking node.leftchild to node.parent
				 node = node.getLeft(); } //keeping the new node, to fix ranking problem 
			 }
	   return node;
   }
   
   private IAVLNode deleteOther(IAVLNode node, char childType) {
	   IAVLNode suc = successor(node);//to find suc we go to right child and all the way left
	   searchNode(suc.getKey(), -1); //need to update all the nodes in route from suc to node, to size--
	   IAVLNode parent = node.getParent();
		 //successor can only have right child - means suc is unary (could be virtual)
		 if (suc.getParent().getLeft().getKey()==suc.getKey()) //suc is left child
			 suc.getParent().setLeft(suc.getRight());
		 else suc.getParent().setRight(suc.getRight()); //suc is a right child
		 IAVLNode returnNode = suc.getRight(); //returnNode is the node to start balancing. could be a virtual child
		 //suc was deleted because no on points at him anymore
		 
		 suc.setHeight(node.getHeight()); //suc will replace node, we update suc rank
		 suc.setLeft(node.getLeft());
		 suc.setRight(node.getRight());
		 suc.setSize(node.getSize());
		 if (childType=='R') 
			 parent.setRight(suc);
		 else parent.setLeft(suc);
		 return returnNode;
   }
   
   private String nodeType(IAVLNode node) {
	   if (!node.getLeft().isRealNode() && !node.getRight().isRealNode())
		   return "leaf";
	   if (!node.getLeft().isRealNode() || !node.getRight().isRealNode())
		   return "unary";
	   return "other";
   }
   
   
   private int balanceDel(IAVLNode node) { //need to fix rank from node upwards
	   int count=0;
	   IAVLNode parent = node.getParent();
	   if (parent==null) 
		   return count;
	   //checking parent rank difference with both children
	   int difLeft = parent.getHeight()-parent.getLeft().getHeight();
	   int difRight = parent.getHeight()-parent.getRight().getHeight();
	   while (difLeft==3 || difRight == 3 || (difLeft==difRight && difRight==2)) { 
		   char childType = 'R';
		   if (parent.getLeft().getKey() == node.getKey())
			   childType = 'L';
		   int checkCase = checkCaseDel(node);
		   if (checkCase == 1) {
			   parent.setHeight(parent.getHeight()-1); //demoting parent
			   count++; //problem could be between parent and his parent, updating parent and node outside the ifs. 
		   }
		   else if (checkCase==2) {
			   		parent.setHeight(parent.getHeight()-1); //demote parent
			   		if (childType=='L') {
			   			parent.getRight().setHeight(parent.getHeight()+1); //promote node's brother
			   			leftRotation(parent);
			   		}
			   		else {//childType=='R'
			   			parent.getLeft().setHeight(parent.getLeft().getHeight()+1); //promote node's brother
			   			rightRotation(parent);
			   }
			   return count+3;    
		   }
		   else if (checkCase==3) {
			   parent.setHeight(parent.getHeight()-2); //counting as 2 balance operations
			   if (childType=='L')
				   leftRotation(parent);
			   else //childType=='R'
				   rightRotation(parent);
			   count = count + 3; 
			   //after rotation node is one level deeper in tree
			   //that is why we move one level up in tree
			   parent = parent.getParent(); 
		   }
		   else { //checkCase==4
			   parent.setHeight(parent.getHeight()-2); //counting as 2 balance operations
			   if (childType=='L') {
				   parent.getRight().setHeight(parent.getRight().getHeight()-1); //parent right child demote
				   parent.getRight().getLeft().setHeight(parent.getRight().getLeft().getHeight()+1); //parent left child promote
				   rightRotation(parent.getRight());
				   leftRotation(parent);
			   }
			   else {//childType=='R'
				   parent.getLeft().setHeight(parent.getLeft().getHeight()-1); //
				   parent.getLeft().getRight().setHeight(parent.getLeft().getRight().getHeight()+1);
				   leftRotation(parent.getLeft());
				   rightRotation(parent);
			   }
			   //after rotation node is one level deeper in tree
			   //that is why we move one level up in tree
			   parent = parent.getParent();   
			   count = count + 6;
		   }
		   node = parent; //updating node and parent, to check the problem higher in the tree
		   parent = parent.getParent();
		   if (parent==null) break; //we finished balancing the tree, because parent is root
		   difLeft = parent.getHeight()-parent.getLeft().getHeight();
		   difRight = parent.getHeight()-parent.getRight().getHeight();
	   }
	  
	   return count;
   }
   
   private int checkCaseDel(IAVLNode node) {//cases are numbered as in AVL presentation
	   IAVLNode parent = node.getParent();
	   int difLeft = parent.getHeight()-parent.getLeft().getHeight();
	   int difRight = parent.getHeight()-parent.getRight().getHeight();
	   if (difLeft==difRight) //and difRight==2 
		   return 1; //case 1- demote
	   if (difLeft == 3) { //rightChildDif =1
			   IAVLNode rightChild = parent.getRight();
			   //dif between right child and his children: 
			   difRight = rightChild.getHeight()-rightChild.getRight().getHeight();
			   difLeft = rightChild.getHeight()-rightChild.getLeft().getHeight();
			   if (difLeft==1 && difRight==1)
				   return 2; //case 2- single rotation
			   else if (difLeft==2 && difRight==1) 
				   return 3; //case 3- single rotation
			   else //(difLeft==1 &&difRight==2)
				   return 4; //case 4-double rotation   
			   }
	   else { //difRight==3 && leftChildDif =2
			   IAVLNode leftChild = parent.getLeft();
			   //dif between left child and his children: 
			   difRight = leftChild.getHeight()-leftChild.getRight().getHeight();
			   difLeft = leftChild.getHeight()-leftChild.getLeft().getHeight();
			   if (difLeft==1 && difRight==1)
				   return 2; //case 2- single rotation
			   else if (difRight==2 && difLeft==1) 
				   return 3; //case 3- single rotation
			   else //(difRight==1 &&difLeft==2)
				   return 4; //case 4-double rotation   
		   }   
	   }
   
   private IAVLNode successor(IAVLNode node) {
	   IAVLNode suc= node.getRight();
	   if (suc.isRealNode()) {//if node has right child the successor is there
		   while (suc.getLeft().isRealNode())
			   suc = suc.getLeft();
		   return suc; }
	   
	   //while node is a right child we need to move upwards until node is a left child
	   IAVLNode rightChild = node;
	   IAVLNode parent = node.getParent();
	   while (parent!=null && parent.getRight().getKey()==rightChild.getKey()) {
		   rightChild = parent;
		   parent = parent.getParent();
	   }
	   return parent;
   }
   
   private IAVLNode predecessor(IAVLNode node) {
	   IAVLNode pre = node.getLeft();
	   if (pre.isRealNode()) { //if node has left child predecessor is there
		   while (pre.getRight().isRealNode())
			   pre = pre.getRight();
		   return pre; }
	   
	   //while node is a left child we need to move upwards until node is a right child
	   IAVLNode leftChild = node;
	   IAVLNode parent = node.getParent();
	   while (parent!=null && parent.getLeft().getKey()==leftChild.getKey()) {
		   leftChild = parent;
		   parent = parent.getParent();
	   }
	   return parent;
	   }
	  
   

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if (empty())
		   return null;
	   return this.min.getValue(); 
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (empty())
		   return null;
	   return this.max.getValue(); 
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
      int[] arr = new int[size]; 
      if (!empty())
    	  toArrayRec(root, arr,null, 0, 'k');
      return arr;               
  }
  
  /** private int toArrayRec(IAVLNode node, int[] arrKeys, String[] arrInfo, int i, char type)
   * Recursive function. type indicate whether it's keys or info (k-for keys i-for info)
   * Changing one of the arrays in place, 
   * the array that is changed matches to type (i/k accordingly)
   * the other arr is null
   * i indicates the next empty place in arr
  **/
  private int toArrayRec(IAVLNode node, int[] arrKeys, String[] arrInfo, int i, char type) { //complexity- O(n) 
	  if (!node.isRealNode())
		  return i;
	  i = toArrayRec(node.getLeft(),arrKeys,arrInfo,i,type);
	  if (type=='k')
	  	 arrKeys[i] = node.getKey();
	  else
	  	 arrInfo[i] = node.getValue();
	  i++;
	  i = toArrayRec(node.getRight(),arrKeys,arrInfo,i,type);
	  return i;
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
	  String[] arr = new String[size]; 
      if (!empty())
      	toArrayRec(root,null,arr, 0, 'v');
      return arr;                   
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return size;
   }
   

     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return root;
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   IAVLNode node = searchNode(x,0); //assuming node is not null
	   AVLTree t1 = new AVLTree();
	   AVLTree t2 =  new AVLTree();
	   if (node.getLeft().isRealNode()) {
		   t1.root = node.getLeft();
		   t1.size = t1.root.getSize();
	   }
	   if (node.getRight().isRealNode()) {
		   t2.root = node.getRight();
		   t2.size = t2.root.getSize();
	   }
	   ///t1 and t2 can still be empty but join can handle this case
	   while (node.getParent()!=null) {
		   IAVLNode parent = node.getParent();
		   AVLTree tmp = new AVLTree();
		   char childType = 'R';
		   if (parent.getLeft().getKey()==node.getKey())
			   childType = 'L';
		   if (childType == 'L') {
			   if (parent.getRight().isRealNode()) {//right child not Virtual
				   tmp.root = parent.getRight();
				   tmp.root.setParent(null);
				   tmp.size = root.getSize();
				   parent = tmp.new AVLNode(parent.getKey(),parent.getValue());
				   //copying a new copy of node parent separate of tree 
			   }
			   t2.join(parent,tmp); //t1 or tmp or both could be empty
		   }
		   else {//childType=='R'
			   if (parent.getLeft().isRealNode()) {
				   tmp.root = parent.getLeft();
				   tmp.root.setParent(null);
				   tmp.size = root.getSize();
				   parent = tmp.new AVLNode(parent.getKey(),parent.getValue());
			   }
			   t1.join(parent,tmp);
		   }
		   node = node.getParent();	//getting the original node.parent		   
	   }
	   if (!t1.empty()) {//updating min, max, and size
		   t1.updateMinMax();
		   t1.size = t1.getRoot().getSize();
	   }
	   
	   if (!t2.empty()) { //updating min,max, and size
		   t2.updateMinMax();
		   t2.size = t2.getRoot().getSize();
	   }	
	   
	   return new AVLTree[]{t1,t2}; 
   }
   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (rank difference between the tree and t)
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys()
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   if (t.empty() && this.empty()) { //both tree are empty
		  this.insert(x.getKey(), x.getValue());
		  return 1;  //min,max,size are updated in insert
	   }
	   if (t.empty()) {
		  int rankDiff = this.getRoot().getHeight()+2;
		  this.insert(x.getKey(), x.getValue());
		  return rankDiff; //min,max,size are updated in insert
	   }
	   if (this.empty()) {
		   int rankDiff = t.getRoot().getHeight()+2;
		   t.insert(x.getKey(), x.getValue());
		   this.root = t.getRoot();
		   this.size = t.size;
		   this.min = t.min;
		   this.max = t.max;
		   return rankDiff;
	   }
	   //if have 2 trees that are not empty
	   AVLTree t1 = this; //changing the reference names for clarity
 	   AVLTree t2 = t;
	   boolean t1SmallerKeys = true; //checking whether keys in t1 are smaller than x 
	   // if true t1 is left subtree of x, else right subtree of x
	   if (t1.getRoot().getKey()>x.getKey()) {
		   t1SmallerKeys = false;
		   t1.min = t.min;}
	   else t1.max = t.max;
	   
	   int rankt1 = t1.getRoot().getHeight();
	   int rankt2 = t2.getRoot().getHeight();  
	   boolean t1LowerRank = true; //checking if t1 rank is lower
	   if (rankt1==rankt2) { //all that necessary is to link t1 and t2 to x
		   x.setHeight(rankt1+1);
		   if (t1SmallerKeys) {
			   x.setLeft(t1.getRoot());
			   x.setRight(t2.getRoot());
		   }
		   else {
			   x.setRight(t1.getRoot());
			   x.setLeft(t2.getRoot());
		   }
		   t1.root = x;
		   x.setParent(null);
		   x.setSize(x.getLeft().getSize()+x.getRight().getSize()+1);
		   t1.size = x.getSize();
		   return 1;
	   }
	   if (rankt1>rankt2)
		   t1LowerRank = false;
	   
	   
	   t1.helpJoin(x,t2,t1SmallerKeys,t1LowerRank, rankt1, rankt2);
	   size = root.getSize();
	   return Math.abs(rankt2-rankt1)+1; 
   }
	   
   private void helpJoin(IAVLNode x, AVLTree t2,boolean t1SmallerKeys, boolean t1LowerRank, int rankt1, int rankt2) {
		AVLTree t1 = this;
		IAVLNode node = t2.getRoot();
		int rank = rankt1;
		if ((t1SmallerKeys && t1LowerRank) || (!t1SmallerKeys && !t1LowerRank)){
			//both cases need to go left from root (tree with bigger rank) until node with rank of root (tree with small rank)
			if (!t1SmallerKeys) {
				node = t1.getRoot();
				rank = rankt2;
			}
			while(node.getHeight()>rank) {
				node = node.getLeft();
			}
			//node is as same rank as the root of tree with small rank (smaller tree) or minus 1
			x.setHeight(rank+1);
			node.getParent().setLeft(x);
			x.setRight(node); 
			if (t1SmallerKeys) {
				x.setLeft(t1.getRoot());
				//we need to update all the nodes sizes in t2 from root to x's parent
				//to increase size by t1.size+1
				t2.searchNode(x.getKey(),t1.getRoot().getSize()+1);
				t1.root = t2.root;
			}
			else {
				t1.searchNode(x.getKey(), t2.getRoot().getSize()+1);
				x.setLeft(t2.getRoot());
				
			}
		}
		//both cases need to go right from root (tree with bigger rank)
		else { // until node with rank of root (tree with small rank)
			if (t1SmallerKeys) {
				node = t1.getRoot();
				rank = rankt2;
			}
			while(node.getHeight()>rank) {
				node = node.getRight();
			}
			x.setHeight(rank+1);
			node.getParent().setRight(x);
			x.setLeft(node); 
			if (t1SmallerKeys) { //root is still t1
				x.setRight(t2.getRoot());
				t1.searchNode(x.getKey(),t2.getRoot().getSize()+1);
			}
			else {
				x.setRight(t1.getRoot());
				t2.searchNode(x.getKey(),t1.getRoot().getSize()+1);
				t1.root = t2.root;
			}		
		}
		//rank problem could be between x and parent
		x.setSize(x.getLeft().getSize()+x.getRight().getSize()+1);
		t1.balanceIn(x);
   }
   
   private void updateMinMax() { //updating the new min and max in O(logn) time (the height of AVLTree) 
	   IAVLNode minNode = root;
	   while (minNode.isRealNode()) 
		   minNode = minNode.getLeft();
	   min = minNode.getParent();//when the while loop ends minNode is a virtual node
	   IAVLNode maxNode = root;
	   while (maxNode.isRealNode())
		   maxNode = maxNode.getRight();
	   max = maxNode.getParent(); //when the while loop ends maxNode is a virtual node
   }
   
   
   /*
	 * assuming: x is not a virtual child
	 * and x left child is not a virtual child
	 * the func is doing a right rotation between x and y
	 * complexity: O(1)
	 */
   private void rightRotation(IAVLNode x) {//doing a rotation between x and left child (y)
	   IAVLNode y = x.getLeft();
	   IAVLNode tmpParentX= x.getParent(); 
	   if (tmpParentX!=null) { //x could be the root
		   if (tmpParentX.getLeft().getKey()==x.getKey()) //check if X is a left child or a right child
			   tmpParentX.setLeft(y);
		   else tmpParentX.setRight(y);
	   }
	  IAVLNode tmpRightChildY= y.getRight();
	  y.setRight(x);
	  x.setLeft(tmpRightChildY);
	  if (tmpParentX==null) { //if x was the root y is the new root
		  root = y;
		  root.setParent(null);
	  }
	  //updating x and y new sizes
	  x.setSize(x.getLeft().getSize()+x.getRight().getSize()+1);
	  y.setSize(y.getLeft().getSize()+y.getRight().getSize()+1);
   }
   
   private void leftRotation(IAVLNode x) { //doing a rotation between x and right child (y)
	   IAVLNode y= x.getRight();
	   IAVLNode tmpParentX= x.getParent();
	   if (tmpParentX!=null) { //x could be the root
		   if (tmpParentX.getLeft().getKey()==x.getKey()) //check if X is a left child or a right child
			   tmpParentX.setLeft(y);
		   else tmpParentX.setRight(y);
	   }
	   IAVLNode tmpLeftChildY= y.getLeft();
	   y.setLeft(x);
	   x.setRight(tmpLeftChildY);
	   if (tmpParentX==null) { //if x was the root y is the new root
			  root = y;
			  root.setParent(null);
	   }
	 //updating x and y new sizes
	   x.setSize(x.getLeft().getSize()+x.getRight().getSize()+1);
	   y.setSize(y.getLeft().getSize()+y.getRight().getSize()+1);
   }
   

   
	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
    	public int getSize(); //Returns the subtree size
    	public void setSize(int size); //setting the subtree size
	}
	
	

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
  public class AVLNode implements IAVLNode{
	  private int key;
	  private String info;
	  private int rank;
	  private IAVLNode left;
	  private IAVLNode right;
	  private IAVLNode parent;
	  private int size = 1;
	    
	    public AVLNode() {//virtual node
	    	this.key=-1;
	    	this.rank = -1;
	    	this.size = 0; //size of a virtual node is zero, because it's not a node in the tree
	    }
	    public AVLNode (int key, String info) {
	  		this.key = key;
	  		this.info = info;
	  		this.rank =0;
	  		this.setLeft(new AVLNode());
	  		this.setRight(new AVLNode());
	  		this.size=1;	
	  	}
	  	
		public int getKey()
		{
			return key; 
		}
		public String getValue()
		{
			return info;
		}
		public void setLeft(IAVLNode node)
		{
			 this.left = node;
			 node.setParent(this);
		}
		public IAVLNode getLeft()
		{
			return left;//if the instance is virtual: left=null. returns null
				
		}
		public void setRight(IAVLNode node)
		{
			 this.right = node;
			 node.setParent(this);
		}
		public IAVLNode getRight() 
		{
			return right; //if the instance is virtual: right=null. returns null
		}
		public void setParent(IAVLNode node)
		{
			 this.parent = node; 
		}
		public IAVLNode getParent()
		{
			return parent; //could be null
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			return key!=-1; 
		}
	    public void setHeight(int height)
	    {
	      rank = height;
	    }
	    public int getHeight()
	    {
	      return rank; 
	    }
	    
	    public int getSize() {
	    	return size;
	    }
	    
	    public void setSize(int size) {
	    	this.size=size;
	    }
  }

}
  

