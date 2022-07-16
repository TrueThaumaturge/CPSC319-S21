import java.util.*;
import java.io.*;

//The main class that runs the program.
//Creates a BinarySearchTree class and reads a text file, storing
//each word in the tree. Calls BinarySearchTree functions to
//display data about the text file's contents at the user's request.
public class CPSC319S21A3 {
	public String fileName;
	public BinarySearchTree bst; 
	
	//main that calls the other functions. nice and organized.
	public static void main(String[] args){
		CPSC319S21A3 prog = new CPSC319S21A3();
		String[] words = prog.readFile(); //step 1
		prog.bst = prog.createBST(words); //step 2
		prog.printInfo(); //step 3
		prog.userRequests(); //step 4 & 5
	}
	
	//reads a file using Scanner and returns it as an array of words
	public String[] readFile(){
		System.out.print("Enter the input file name: ");
		fileName = userInput();
		StringBuilder line = new StringBuilder();
		Scanner in = null;
		try{
			//opens stream to input file
			in = new Scanner(new File(fileName)); //delimiter is whitespace by default, which is what we want to separate into words
			while(in.hasNextLine()){
				line.append(in.nextLine() + " "); //stringbuilder stores each word separated by a space
			}
			in.close();
		}
		catch(IOException e){
			System.out.println("IO exception trying to read file "+fileName+".");
			e.printStackTrace();
			System.exit(1);
		}
		String[] words = line.toString().replaceAll("[^0-9a-zA-Z ]", " ").toLowerCase().split("\\s+");
		
		/*testing
		for(String word : words){
			System.out.println(word);
		}*/
		
		return words;
	}
	
	//collects the user input and stores it in the fileName field
	private String userInput(){
		Scanner s = new Scanner(System.in);
		String str = s.next();
		return str.trim();
	}
	
	//'pauses' the program by waiting for the user to input a newline
	private void pause(){
		System.out.println("[Press ENTER to continue]");
		Scanner s = new Scanner(System.in);
		s.nextLine();
	}
	
	//creates the BST from the list of words by calling the bst.add() member
	//to add the word to the tree. Most of the work is done there.
	public BinarySearchTree createBST(String[] words){
		BinarySearchTree bst = new BinarySearchTree();
		for(String word : words) {//adds each word to the tree
			if(!word.equals("")) //needed in the case that two whitespace characters are next to eachother
				bst.add(word);
		}
		return bst;
	}
	
	//traverses the tree by calling a recursive helper member to display:
	//	total number of words / nodes
	//	total number of unique words (freq = 1)
	//	most frequent word(s)
	//	maximum height of the tree
	public void printInfo(){
		bst.getInfo_Recursive(bst.root); //the part that does the actual work
		System.out.println("\nTotal number of words in " + fileName + ": " + bst.nodes);
		System.out.println("Number of unique words in " + fileName + ": " + bst.freq1Nodes);
		
		System.out.println("Most frequent word(s):");
		for(TreeNode node : bst.mostFreqWords)
			System.out.println("    " + node.word + " = " + node.freq + " times");
		
		System.out.println("Maximum height of the tree: " + bst.findHeight(bst.root));
		
	}
	
	//Allows the user to request the frequency of a word
	public void userRequests(){
		String response;
		while(true) {
			pause(); //pauses until user confirms input
			System.out.print("**Options:\n"
								+ "    freq : Find the frequency of a word\n"
								+ "    list : List every word from the file\n"
								+ "    info : Display the word info again\n"
								+ "    exit : Exit the program\n"
								+ "Enter a command: ");
			//collects user input and performs a function depending on their response
			response = userInput().toLowerCase();
			switch(response) {
				//allows the user to request info about a word, part 4
				case "freq":
					System.out.print("Enter a word to search for in " + fileName + ": ");
					String word = userInput().toLowerCase();
					TreeNode searchResult = bst.search(bst.root, word);
					if(searchResult != null)
						System.out.println("Word found! '" + word + "' appears " + searchResult.freq + " times in " + fileName);
					else
						System.out.println("Word '" + word + "' not found in file " + fileName);
					break;
				//allows the user to display the entire tree using any traversal method, part 5
				case "list":
					System.out.println("Select the BST traversal method by typing a command:\n"
											+ "    in : IN-ORDER traversal\n"
											+ "    pre : PRE-ORDER traversal\n"
											+ "    post : POST-ORDER traversal");
											
					String method = userInput().toUpperCase();
					if(!(method.equals("IN") || method.equals("PRE") || method.equals("POST")))
						throw new IllegalArgumentException("Argument must either be 'in', 'pre', or 'post'.");
					System.out.print(method+"-ORDER Output: ");
					bst.printTreeWordList(bst.root, method);
					System.out.println();
					break;
				//allows the user to re-display the info as shown previously, part 3
				case "info":
					System.out.println("\nTotal number of words in " + fileName + ": " + bst.nodes);
					System.out.println("Number of unique words in " + fileName + ": " + bst.freq1Nodes);
					System.out.println("Most frequent word(s):");
					for(TreeNode node : bst.mostFreqWords)
						System.out.println("    " + node.word + " = " + node.freq + " times");
					System.out.println("Maximum height of the tree: " + bst.findHeight(bst.root));
					break;
				//allows the user to exit the program
				case "exit":
					System.out.println("Exiting program.");
					System.exit(0);
				//throws an exception if the user response isn't one of the commands
				default:
					throw new IllegalArgumentException("Argument must either be 'freq', 'list', 'info' or 'exit'.");
			}
		}
	}
}

//The BinarySearchTree class that uses TreeNodes to form a BST.
//comes with helper functions to add to the tree and traverse it
//as needed for the main program
class BinarySearchTree {
	public TreeNode root = null;

	//stats for part 3 are stored as fields
	public int nodes = 0;
	public int freq1Nodes = 0;
	public ArrayList<TreeNode> mostFreqWords = new ArrayList<>();

	//Adds the word to the BST. If a node of the word already exists, increments its frequency.
	//Otherwise, creates a new node for the word in its proper place.
	public void add(String word){
		TreeNode newNode = new TreeNode(word);
		TreeNode tempRef = root;
		
		if(root == null) //special case for the first node in a tree
			root = newNode;
		else {
			//we go down the tree until we find the proper spot for the word
			while(true){
				//if the word already exists in the tree, just tick the freq up
				if(word.compareTo(tempRef.word) == 0) {
					tempRef.freq++;
					break;
				}
				//if the word comes before the current node's word, we go left
				else if(word.compareTo(tempRef.word) < 0) {
					//if we've reached a leaf we can place the new node
					if(tempRef.leftNode == null){
						tempRef.leftNode = newNode;
						break;
					}
					//otherwise keep going
					else
						tempRef = tempRef.leftNode;
				}
				//the word must come after the current node's word so we go right
				else{
					//if we've reached a leaf we can place the new node
					if(tempRef.rightNode == null) {
						tempRef.rightNode = newNode;
						break;
					}
					//otherwise keep going
					else
						tempRef = tempRef.rightNode;
				}
			}
		}
		return;
	}

	//traverses the tree to find the number of nodes, unique nodes, and the most frequent word
	//uses PRE-ORDER traversal
	public void getInfo_Recursive(TreeNode currentNode){
		if(currentNode != null) {	
			//3.1 - increments node count
			nodes++;
			//3.2 - increments unique node count if needed
			if(currentNode.freq == 1)
				freq1Nodes++;
			
			//3.3 - keeps track of the highest frequency words
			if(mostFreqWords.isEmpty()) //if it's the first word
				mostFreqWords.add(currentNode);
			else if(currentNode.freq > mostFreqWords.get(0).freq) { //if the word's frequency is larger
				mostFreqWords.clear();
				mostFreqWords.add(currentNode);
			}
			else if(currentNode.freq == mostFreqWords.get(0).freq) //if it shares a frequency
				mostFreqWords.add(currentNode);

			//left recursive call
			getInfo_Recursive(currentNode.leftNode);
			//right recursive call
			getInfo_Recursive(currentNode.rightNode);
		}
		return;
	}

	//finds the height of the tree using a recursive method
	//code slightly modified from Assignment 3 pdf https://d2l.ucalgary.ca/d2l/le/content/385020/viewContent/4759207/View
	public int findHeight(TreeNode currentNode){
		if(currentNode == null)
			return 0;
		else {
			int leftDepth = findHeight(currentNode.leftNode);
			int rightDepth = findHeight(currentNode.rightNode);

			if(leftDepth > rightDepth)
				return leftDepth + 1;
			else
				return rightDepth + 1;
		}
	}
	
	//searches for a TreeNode using a binary search and returns the TreeNode object when it is found
	//returns null if the search was unsuccessful.
	public TreeNode search(TreeNode currentNode, String word){
		//base case that stops the recursion - we've reached the end of the tree
		if(currentNode == null)
			return null;
		//stops the recursion if we find the word
		if(word.compareTo(currentNode.word) == 0)
			return currentNode;
		//if the word comes before the current node's word, we go left
		else if(word.compareTo(currentNode.word) < 0)
			return search(currentNode.leftNode, word);
		//both other cases handled so we go right
		else
			return search(currentNode.rightNode, word);
	}
	
	//generates and returns an ArrayList<String> by traversing the BST. Allows the user to pick the traversal method.
	public void printTreeWordList(TreeNode currentNode, String method){
		if(currentNode != null) {
			if(method.equals("IN")) { //IN-ORDER
				printTreeWordList(currentNode.leftNode, method); //left
				System.out.print(" " + currentNode.word); //work
				printTreeWordList(currentNode.rightNode, method); //right
			}
			else if(method.equals("PRE")) { //PRE-ORDER
				System.out.print(" " + currentNode.word); //work
				printTreeWordList(currentNode.leftNode, method); //left
				printTreeWordList(currentNode.rightNode, method); //right
			}
			else if(method.equals("POST")) { //POST-ORDER
				printTreeWordList(currentNode.leftNode, method); //left
				printTreeWordList(currentNode.rightNode, method); //right
				System.out.print(" " + currentNode.word); //work
			}
		}
	}
}

//TreeNode class that stores a word and its frequency.
//used by the BinarySearchTree
class TreeNode {
	public String word;
	public int freq;
	public TreeNode leftNode = null;
	public TreeNode rightNode = null;

	public TreeNode(String word){
		this.word = word;
		freq = 1;
	}
}