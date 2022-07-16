//Quentin Jennings // 30089570
//CPSC 319 Summer 2021

import java.io.*;
import java.util.*;

//main class that actually runs the program.
//reads an input file specified by command line and puts it into a sorted 1d array
//uses the 1d array to make an arraylist of singly linked lists that groups anagrams together
//uses the linked lists to generate a final output as specified, grouping anagrams in the same line
//output is sorted alphabetically because the initial 1d array is sorted and it just works out
public class CPSC319S21A2 {
	
	public String inputFileName;
	public String outputFileName;
	public ArrayList<String> wordList;
	public ArrayList<LinkedList> linkedWordList = new ArrayList<LinkedList>();
	
	//main that calls the other functions
	public static void main(String[] args) {
		CPSC319S21A2 prog = new CPSC319S21A2(args[0], "output.txt");
		prog.wordList = prog.readFile();
		prog.linkedWordList = prog.makeLinkedLists();
		prog.printOutput();
		
	}
	
	
	//Simple constructor that takes the command line input as the input file name
	//and an output file name we can change easily as an argument
	public CPSC319S21A2(String inputFileName, String outputFileName) {
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
	}
	
	//reads the lines of the file and puts them into a sorted wordList
	public ArrayList<String> readFile () {
		ArrayList<String> wordList = new ArrayList<String>();
		
		BufferedReader in = null;
		try{
			//opens stream to input file
			in = new BufferedReader(new FileReader(inputFileName));
			String line;
			//reacs each line and adds them to the word list
			while((line = in.readLine()) != null){
				wordList.add(line.trim());
			}
			in.close();
		}
		catch(IOException e){
			System.out.println("IO exception trying to read file "+inputFileName+".");
			e.printStackTrace();
		}
		//then we sort the wordlist before returning it
		Collections.sort(wordList);
		return wordList;
	}
	
	//Makes a sorted 1D array of anagram linked lists using the assignment's
	//suggested procedure.
	public ArrayList<LinkedList> makeLinkedLists() {
		ArrayList<LinkedList> linkedWordList = new ArrayList<LinkedList>();
		
		//iterates for each word in the sorted list of words
		for(String word : wordList) {
			//sorts the characters in the current word
			char[] arraySortedWord = word.toCharArray();
			Arrays.sort(arraySortedWord);
			
			//used for the next step
			int i;
			boolean foundAnagram = false;
			
			//looks for an anogram of the current word by comparing it to all the current nodes and their sorted words
			for(i = 0; i < linkedWordList.size(); i++) {
				//if the two character-sorted words are equal, that makes them anagrams! we can quit the loop now
				if(Arrays.equals(arraySortedWord, linkedWordList.get(i).sortedWord)) {
					foundAnagram = true;
					break;
				}
			}
			
			//if there's an anogram, add it to that linked list
			if(foundAnagram) {
				//testing line
				//System.out.println("Found anagram for "+word+", adding to linked list index "+i);
				linkedWordList.get(i).appendNode(word);
			}
			//otherwise, add a new linked list for the word and its potential anograms
			else {
				//testing line
				//System.out.println("No found anagram for "+word+", making new linked list at index "+i+".");
				linkedWordList.add(new LinkedList(arraySortedWord)); //adds the new linked list
				linkedWordList.get(linkedWordList.size()-1).appendNode(word); //adds the first element to the linked list
			}
		}
		
		return linkedWordList;
	}
	
	//generates the output into a file
	public void printOutput() {
		StringBuilder outputString = new StringBuilder();
		BufferedWriter out = null;
			
		//iterates through each linked list to generate the output
		for(LinkedList list : linkedWordList) {
			Node currentNode = list.head;
			
			//iterates through each node in the linked list
			while(true) {
				//adds the current node's word to the string and moves to the next
				outputString.append(currentNode.word);
				currentNode = currentNode.next;
				
				//breaks the loop once the end is found and starts a new line
				if(currentNode == null) {
					outputString.append("\n");
					break;
				}
				//puts spaces between each word
				else
					outputString.append(" ");
			}
		}
	
		//file stuff must be in try/catch block
		try{
			//opens output file stream and writes the StringBuilder line to the file
			out = new BufferedWriter(new FileWriter(outputFileName));
			out.write(outputString.toString().trim());
			out.close();
		}
		catch(IOException e){
			System.out.println("IO exception trying to print file "+outputFileName+".");
			e.printStackTrace();
		}
		
	}
}

//Singly Linked List class that contains a function to add nodes to the end of the list
class LinkedList {
	public Node head = null;
	public char[] sortedWord; //easy access for checking anograms
	
	//constructor
	public LinkedList(char[] sortedWord){
		this.sortedWord = sortedWord;
	}
	
	//inserts node at the end of the list, resulting in alphabetical order because the base array is already sorted
	public void appendNode(String word){
		Node newNode = new Node(word);
			
		//if the list is empty
		if(head == null)
			head = newNode;
		//if the list isn't empty, iterate to the end and add the element there
		else {
			Node currentNode = head;
			//iterates to end
			while (currentNode.next != null)
				currentNode = currentNode.next;
				
			//adds the new node to the end
			currentNode.next = newNode;
		}
	}
}

//Node class for the linked lists. Each node stores a string and the next node reference.
class Node {
	public String word;
	public Node next;
		
	//constructor
	public Node(String word){
		this.word = word;
		next = null;
	}
}