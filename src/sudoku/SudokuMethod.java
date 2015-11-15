package sudoku;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JTextField;

public class SudokuMethod {
	private char[][] sudokuOrigin=new char[9][9];       // store the generated origin sudoku board, which is answer
	private char[][] sudokuPlay=new char[9][9];         // store the generated part sudoku board, which can be played
	private char[][] sudokuDisplay=new char[9][9];      // store the copy of sudoku, use for display and get infor back in GUI
	private Integer[] numLeft = new Integer[9];               //  store the amount of available numbers that left 
	public Integer blankBlockLeft = 0;                     // store how many blank blocks left
	public Integer stepUserTried = 0;                      // store how many step user tried
	
	//== constructor==//
	SudokuMethod(){          
		generateSudoku(1);
	}
	
	//== constructor with fill in textField==//
	SudokuMethod(JTextField t[], int difficulty){    // t[] is for sudoku play zone      
		generateSudoku(difficulty);
		if(t.length!=81) return;
		for(int i=0;i<81;i++){
			t[i].setEditable(true);
			t[i].setForeground(Color.BLACK);
			initSudokuBlock(i, sudokuDisplay[i/9][i%9], t[i]);    // init each block zone
		}
	}
	
	//== check this input value of block is valid or not ==//
	public boolean checkBlockValidSudoku(int pos, Character c){
		int row=pos/9;
		int col=pos%9;      // use position to calculate the row and column
		for(int i=0;i<9;i++){         // check row and column
			if(i==col) continue;
			if(sudokuDisplay[row][i]==c) return false;
		}
		for(int i=0;i<9;i++){         // check row and column
			if(i==row) continue;      // avoid self check
			if(sudokuDisplay[i][col]==c) return false;
		}
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++){
				if(row/3*3+i==row && col/3*3+j==col) continue;
				if(sudokuDisplay[row/3*3+i][col/3*3+j]==c) 
					return false;
			}
		return true;
	}
	
	//== check this input value of block is valid or not, this will also affect the same row,col and cube block ==//
	//== even if only changed, all same raw, col and cube need to be checked, just to display which one is duplicate of this one==//
	public void checkValidSudoku(int pos, Character c, JTextField t[]){
		int row=pos/9;
		int col=pos%9;      // use position to calculate the row and column
		for (int i = 0; i < 9; i++) {
			if (t[row * 9 + i].isEditable()) {
				if (checkBlockValidSudoku(row * 9 + i, sudokuDisplay[row][i]))
					t[row * 9 + i].setForeground(Color.GREEN);
				else
					t[row * 9 + i].setForeground(Color.RED);
			}
			if (t[i * 9 + col].isEditable()) {
				if (checkBlockValidSudoku(i * 9 + col, sudokuDisplay[i][col]))
					t[i * 9 + col].setForeground(Color.GREEN);
				else
					t[i * 9 + col].setForeground(Color.RED);
			}
		}
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (t[(row / 3 * 3 + i) * 9 + (col / 3 * 3 + j)].isEditable()) {
					if (checkBlockValidSudoku((row / 3 * 3 + i) * 9
							+ (col / 3 * 3 + j),
							sudokuDisplay[row / 3 * 3 + i][col / 3 * 3 + j]))
						t[(row / 3 * 3 + i) * 9 + (col / 3 * 3 + j)]
								.setForeground(Color.GREEN);
					else
						t[(row / 3 * 3 + i) * 9 + (col / 3 * 3 + j)]
								.setForeground(Color.RED);
				}
					
	}
	
	//== check win or not when filled all the block==//
	public boolean checkSudokuWin(){
		Character c;
		for(int i=0;i<81;i++){
			c=sudokuDisplay[i/9][i%9];
			if(!checkBlockValidSudoku(i,c))
				return false;
		}
		return true;
	}
	
	//== dig holes is origin sudoku board to generate playable game
	private void digHoleSudoku(int difficulty){     
		Random r=new Random();
		int pos;
		blankBlockLeft=30+difficulty*5;
		for(int i=0;i<30+difficulty*5;i++){
			pos=r.nextInt(81);        // from 0 to 80
			if(sudokuDisplay[pos/9][pos%9]!='.')
				sudokuDisplay[pos/9][pos%9]='.';       // dig hole
			else         // if already digged
				--i;     // re-find position
		}
	}
	
	
	//== generate random sudoku with answer ==//
	public void generateSudoku(int difficulty){      
		for(int i=0;i<9;i++){                // check row , i is row, j is column
	    	for(int j=0;j<9;j++){
	    		sudokuDisplay[i][j]='.';
	    	}
		}
		// first use random r to generate the position of all 1
		Random r=new Random();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					int row = 0, col = 0;
					char temp=sudokuDisplay[i * 3 + row][j * 3 + col];
					//board[i * 3 + row][j * 3 + col] = '1'; // init for recur
					do {
						sudokuDisplay[i * 3 + row][j * 3 + col] = temp; // recur back
						row = r.nextInt(3);
						col = r.nextInt(3);
						temp=sudokuDisplay[i * 3 + row][j * 3 + col];
						sudokuDisplay[i * 3 + row][j * 3 + col] = '1'; // set new
					} while (!checkBlockValidSudoku((i * 3 + row) * 9 + (j * 3 + col), '1'));
				}
			}
		//== the idea to generate a random pattern sudoku is to solve the sudoku with only random 1 on the board ==//
		//== the solve method also use random variable to do recur and backtrack, make sure the sudoku is randomed ==//
		solveSudoku(sudokuDisplay);          
		for(int i=0;i<9;i++){                // check row , i is row, j is column
	    	for(int j=0;j<9;j++){
	    		sudokuOrigin[i][j]=sudokuDisplay[i][j];    // store the original suduku to sudokuOrigin
	    	}
		}		
		digHoleSudoku(difficulty);             // dig hole with difficulty		
		for(int i=0;i<9;i++){                // check row , i is row, j is column
	    	for(int j=0;j<9;j++){
	    		sudokuPlay[i][j]=sudokuDisplay[i][j];    // store the original suduku to sudokuOrigin
	    	}
		}
	}
	
	//== generate sudoku, here is just a test ==//
	public void generateSudoku2(){      // generate sudoku, here is just a test
		String[] s={"..9748...","7........",".2.1.9...","..7...24.",".64.1.59.",".98...3..","...8.3.2.","........6","...2759.."};
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				sudokuPlay[i][j]=s[i].charAt(j);
				sudokuDisplay[i][j]=s[i].charAt(j);
			}
		}
	}
	
	
	//== init GUI numLeft single block when game start==//
	public void initNumLeftBlock(JTextField n[]){       // n[] is for numleft zone	
		for(int i=0;i<9;i++)
			numLeft[i]=9;
		for(int i=0;i<81;i++){
			char c=sudokuDisplay[i/9][i%9];
			if(c!='.'){
				int j=sudokuDisplay[i/9][i%9]-'1'; // get the number that block has
				numLeft[j]--;
			}
		}
		for(Integer i=0;i<9;i++){
			Integer num=numLeft[i];
			n[i+9].setText(num.toString());      // set numleft value
		}
	}
	
	
	
	//== init GUI sudoku single block when game start==//
	private void initSudokuBlock(int pos, Character c, JTextField t){
		int row=pos/9;
		int col=pos%9;      // use position to calculate the row and column
		sudokuDisplay[row][col]=c;
		if(c!='.'){
			t.setEditable(false);      // original value , cant be modified
			t.setText(c.toString());
		}else{
			t.setText("");
		}
	}
	
	//== show the answer of this game ==//
	public void showSudokuAnswer(JTextField t[], JTextField n[]){
		sudokuDisplay=sudokuPlay.clone();
		solveSudoku(sudokuDisplay);
		Character c;
		for(int i=0;i<81;i++){
			if(t[i].isEditable()){
				t[i].setForeground(Color.GREEN);
				c=sudokuDisplay[i/9][i%9];
				t[i].setText(c.toString());
			}
		}
		for(int i=0;i<9;i++){
			numLeft[i]=0;
			n[i+9].setText("0");
		}
	}
	
	//== update GUI numLeft single block==//
	public void updateNumLeftBlock(Character last, Character now, JTextField t[]){
		if(last=='.'){          // '.' mean blank in that block when action triggered
			if(now=='.') return;
			else{
				int n=now-'1';
				numLeft[n]--;       // minus one
				t[n+9].setText(numLeft[n].toString());
			}
		}else{
			int l=last-'1';
			numLeft[l]++;
			t[l+9].setText(numLeft[l].toString());
			if(now!='.'){
				int n=now-'1';
				numLeft[n]--;       // minus one
				t[n+9].setText(numLeft[n].toString());
			}
		}
	}
	
	//== update GUI sudoku single block==//
	public void updateSudokuBlock(int pos, Character c, JTextField t){
		int row=pos/9;
		int col=pos%9;      // use position to calculate the row and column
		sudokuDisplay[row][col]=c;
		if(c!='.')
			t.setText(c.toString());
		else
			t.setText("");
	}
	
	
	//== below is a set of functions for solving sudoku ==//
	public boolean solveSudoku(char[][] board) {
//		int[][] Blank = new int[9][9];      // matrix for store the available blank spot and possible value
//		for(int i=0;i<9;i++)
//			for(int j=0;j<9;j++)
//				Blank[i][j]=0;
		Map<Integer,List<Integer>> Blank=sudokuFindBlank(board);
    	//Stack<Integer> pos=new Stack<Integer>();
    		return solveSudokuRecur(board,Blank) ;
    			   	    	
	}
	
	private Map<Integer,List<Integer>> sudokuFindBlank(char[][] board){     // return how many blank this sudoku has
		Map<Integer,List<Integer>> Blank=new HashMap<Integer,List<Integer>>();
		for(int i=1;i<10;i++){
			List<Integer> temp=new ArrayList<Integer>();
			Blank.put(i, temp);
		}
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++){
				if(board[i][j]=='.'){    // if it's blank
					int temp=0x01ff;     // use binary to represent, 1 means it's possible for value at that position
					int count=0;     // count for how many number's can use in blank space
				   //00000001,11111111   
				    //      9 87654321        
					for(int k=0;k<9;k++){     // for rows and column
						if(board[i][k]!='.')
							temp=temp&~(0x01<<(board[i][k]-'1'));  // get rid of used number
						if(board[k][j]!='.')
							temp=temp&~(0x01<<(board[k][j]-'1'));  // get rid of used number
					}
					for(int k=0;k<3;k++)    // for 3*3 cube
						for(int l=0;l<3;l++)
							if(board[(i/3)*3+k][(j/3)*3+l]!='.')
								temp=temp&~(0x01<<(board[(i/3)*3+k][(j/3)*3+l]-'1'));  // get rid of used number					
					while(temp>0){         // this is for get how many 1s in temp, binary
						temp=temp&(temp-1);
						count++;
					}
					List<Integer> t=Blank.get(count);
					t.add(i*9+j);     // store the position
					Blank.put(count, t);       // save blank with information of blank position and how many numbers can be choose in that blank
				}
			}    // finish checking blank, all blank should be represent as 001100001111... like this
		return Blank;
	}


	private boolean solveSudokuRecur(char[][] board, Map<Integer,List<Integer>> Blank){
		int count=1;
		while(Blank.get(count).isEmpty()) {
			count++;   // find the blank that has smaller number choice
			if (count==10)
				return true;        // Blank is empty, all blanks have been filled, return true
		}
		
		int pos;
		pos=Blank.get(count).get(0);
		Random r=new Random();
		int i=r.nextInt(9);      // i is from 1 to 9
		for(int k=0;k<9;k++){
			char j = (char)((i+k)%9+'1');
			if(checkPos(board,pos,j)){   // if number j is available
				Blank.get(count).remove(0);     // pop this position, which means marked as ok
				board[pos/9][pos%9]=j;   // fill the board
				if(!solveSudokuRecur(board,Blank)){     // if deep search is not vaild
					Blank.get(count).add(0, pos);   // add it back
					board[pos/9][pos%9]='.';   // erase the board
				}else{
					return true;         // if inside true, return true
				}
			}
		}
		return false;    // if tried every possible one, but still reach here, means not valid
	}

	private boolean checkPos(char[][] board, int pos, char j) {
		int row = pos / 9;
		int col = pos % 9;
		for (int k = 0; k < 9; k++) { // check row and col
			if (board[row][k] == j)
				return false; // find same other number in row
			if (board[k][col] == j)
				return false; // find same other number in col
		}
		for (int k = 0; k < 3; k++)
			// check for 3*3 cube
			for (int l = 0; l < 3; l++)
				if (board[(row / 3) * 3 + k][(col / 3) * 3 + l] == j)
					return false; // get rid of used number
		return true; // if available

	}




}
