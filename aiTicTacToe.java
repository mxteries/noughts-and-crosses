import java.util.*;

public class aiTicTacToe {

	public int player; //1 for player 1 and 2 for player 2
	private List<List<positionTicTacToe>> allWinningLines = new ArrayList<>(); 
	private int getStateOfPositionFromBoard(positionTicTacToe position, List<positionTicTacToe> board)
	{
		//a helper function to get state of a certain position in the Tic-Tac-Toe board by given position TicTacToe
		int index = position.x*16+position.y*4+position.z;
		return board.get(index).state;
	}

	// isEnded from runTicTacToe, modified
    // Statically evaluate this board for wins for player 1 or 2
	// return "inf" if p1 won, "-inf" if p2 won, 0 if draw
	// if no terminal state (no win/draw) , return -1
	private int checkTerminal(List<positionTicTacToe> board) {
		//brute-force
		for(int i=0;i<allWinningLines.size();i++) {
			
			positionTicTacToe p0 = allWinningLines.get(i).get(0);
			positionTicTacToe p1 = allWinningLines.get(i).get(1);
			positionTicTacToe p2 = allWinningLines.get(i).get(2);
			positionTicTacToe p3 = allWinningLines.get(i).get(3);
			
			int state0 = getStateOfPositionFromBoard(p0,board);
			int state1 = getStateOfPositionFromBoard(p1,board);
			int state2 = getStateOfPositionFromBoard(p2,board);
			int state3 = getStateOfPositionFromBoard(p3,board);
			
			//if they have the same state (marked by same player) and they are not all marked.
			if(state0 == state1 && state1 == state2 && state2 == state3 && state0!=0) {
				System.out.println("Found a win for state: " + state0);
				if (state0 == 1) {
					// p1 has won!
					return Integer.MAX_VALUE;
				} else if(state0 == 2) {
					// p2 has won!
					return Integer.MIN_VALUE; 
				}
			}
		}
		for(int i=0;i<board.size();i++) {
			// if there's still unmarked positions
			if(board.get(i).state==0)
			{
				//game is not ended, continue
				return -1;
			}
		}
		return 0; //call it a draw
	}

	// copied from runTicTacToe.java
	private List<positionTicTacToe> deepCopy(List<positionTicTacToe> board)
	{
		//deep copy of game boards
		List<positionTicTacToe> copiedBoard = new ArrayList<positionTicTacToe>();

		for (positionTicTacToe p: board) {
			copiedBoard.add(new positionTicTacToe(p.x, p.y, p.z, p.state));

		}
		return copiedBoard;
	}

/* lecture notes
* because we have time limit: can use certain techniques
* progresive deepening: analyze game situation up to d=1,d=2,... until time is up
* look at lecture notes for suggestion of techniques
*/

	// statically evaluate the board position
	// simple idea: go through each board point and add 1 for every adjacent x and subtract 1 for every adjacent o (including position itself)
	private int evaluate(List<positionTicTacToe> position) {
		// else, determine whether the position is better for p1 or p2

		return 10;
		//Random rand = new Random();
		//return rand.nextInt(10) - 5;  // random num from -5 to 5
	}
	private void markPosition(positionTicTacToe pos, List<positionTicTacToe> board, boolean maximizingPlayer) {
		
		int player = 2;
		if (maximizingPlayer) {
			player = 1;	
		}
		int index = pos.x*16+pos.y*4+pos.z;
		board.get(index).state=player;
	}
	
	private int minimax(List<positionTicTacToe> modifiedBoard, int depth, int alpha, int beta, boolean maximizingPlayer) {
		int terminalScore = checkTerminal(modifiedBoard);
		if (terminalScore != -1) {  // -1 means no terminal position
			return terminalScore;
		}
		if (depth == 0) {
			return evaluate(modifiedBoard);
		}
			
		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;

		// loop over all unmarked board positions
		for (positionTicTacToe found: modifiedBoard) {
			if (found.state == 0) { // found unmarked position
				List<positionTicTacToe> copiedBoard = deepCopy(modifiedBoard);
				markPosition(found, copiedBoard, maximizingPlayer);

				// recursively call minimax on child position
				int score = minimax(copiedBoard, depth - 1, alpha, beta, !maximizingPlayer);
				if (maximizingPlayer) {
					maxEval = Math.max(maxEval, score);
					alpha = Math.max(alpha, score);
				} else {
					minEval = Math.min(minEval, score);
					beta = Math.min(beta, score);
				}
				if (beta <= alpha) {
					break;
				}
			}

		}
		if (maximizingPlayer) {
			return maxEval;
		}
		return minEval;

	}

	// should not get called with a depth of 0
	// there's a problem with how my wrapper and minimax interact -- why is it always player2??
	private positionTicTacToe minimaxWrapper(List<positionTicTacToe> actualBoard, int depth, int alpha, int beta, boolean maximizingPlayer) {
		// doesnt even need to check terminal pos

		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;
		positionTicTacToe maxMove = new positionTicTacToe(0,0,0);
		positionTicTacToe minMove = new positionTicTacToe(0,0,0);

		// loop over all unmarked board positions
		for (positionTicTacToe position: actualBoard) {
			if (position.state == 0) { // found unmarked position
				List<positionTicTacToe> copiedBoard = deepCopy(actualBoard);
				markPosition(position, copiedBoard, maximizingPlayer);

				// recursively call minimax on child position
				int score = minimax(copiedBoard, depth - 1, alpha, beta, !maximizingPlayer);
				if (maximizingPlayer) {
					if (score > maxEval) {
						maxEval = score;
						maxMove = position;
					}
					alpha = Math.max(alpha, score);
				} else {
					if (score < minEval) {
						minEval = score;
						minMove = position;
					}
					beta = Math.min(beta, score);
				}

				if (beta <= alpha) {
					break;
				}
			}

		}
		if (maximizingPlayer) {
			return maxMove;
		}
		return minMove;
	}


	// chooses a move for player 1 using minimax and alpha-beta pruning
	// chooses a move for p2 at random
	public positionTicTacToe myAIAlgorithm(List<positionTicTacToe> board, int player)
	{
		//TODO: this is where you are going to implement your AI algorithm to win the game. The default is an AI randomly choose any available move.
		// maybe f the tree, well just copy the board, then minimax that. then we just need a function to evaluate the position given a board

		positionTicTacToe myNextMove;

		// copy board
		List<positionTicTacToe> minimaxBoard = deepCopy(board);
		if (player == 1) {
			// minimax(minimaxBoard, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
			myNextMove = minimaxWrapper(minimaxBoard, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		} else {
			do {
				Random rand = new Random();
				int x = rand.nextInt(4);
				int y = rand.nextInt(4);
				int z = rand.nextInt(4);
				myNextMove = new positionTicTacToe(x,y,z);
			} while(getStateOfPositionFromBoard(myNextMove,board)!=0);
		}
		return myNextMove;
	}

	private List<List<positionTicTacToe>> initializeWinningLines()
	{
		//create a list of winning line so that the game will "brute-force" check if a player satisfied any	winning condition(s).
		List<List<positionTicTacToe>> winningLines = new ArrayList<List<positionTicTacToe>>();

		//48 straight winning lines
		//z axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,j,0,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,j,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//y axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,j,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,j,-1));
				winningLines.add(oneWinCondtion);
			}
		//x axis winning lines
		for(int i = 0; i<4; i++)
			for(int j = 0; j<4;j++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,j,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,j,-1));
				winningLines.add(oneWinCondtion);
			}

		//12 main diagonal winning lines
		//xz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,0,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,1,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,2,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//yz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,0,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,3,-1));
				winningLines.add(oneWinCondtion);
			}
		//xy plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,0,i,-1));
				oneWinCondtion.add(new positionTicTacToe(1,1,i,-1));
				oneWinCondtion.add(new positionTicTacToe(2,2,i,-1));
				oneWinCondtion.add(new positionTicTacToe(3,3,i,-1));
				winningLines.add(oneWinCondtion);
			}

		//12 anti diagonal winning lines
		//xz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,i,3,-1));
				oneWinCondtion.add(new positionTicTacToe(1,i,2,-1));
				oneWinCondtion.add(new positionTicTacToe(2,i,1,-1));
				oneWinCondtion.add(new positionTicTacToe(3,i,0,-1));
				winningLines.add(oneWinCondtion);
			}
		//yz plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(i,0,3,-1));
				oneWinCondtion.add(new positionTicTacToe(i,1,2,-1));
				oneWinCondtion.add(new positionTicTacToe(i,2,1,-1));
				oneWinCondtion.add(new positionTicTacToe(i,3,0,-1));
				winningLines.add(oneWinCondtion);
			}
		//xy plane-4
		for(int i = 0; i<4; i++)
			{
				List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
				oneWinCondtion.add(new positionTicTacToe(0,3,i,-1));
				oneWinCondtion.add(new positionTicTacToe(1,2,i,-1));
				oneWinCondtion.add(new positionTicTacToe(2,1,i,-1));
				oneWinCondtion.add(new positionTicTacToe(3,0,i,-1));
				winningLines.add(oneWinCondtion);
			}

		//4 additional diagonal winning lines
		List<positionTicTacToe> oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,0,0,-1));
		oneWinCondtion.add(new positionTicTacToe(1,1,1,-1));
		oneWinCondtion.add(new positionTicTacToe(2,2,2,-1));
		oneWinCondtion.add(new positionTicTacToe(3,3,3,-1));
		winningLines.add(oneWinCondtion);

		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,0,3,-1));
		oneWinCondtion.add(new positionTicTacToe(1,1,2,-1));
		oneWinCondtion.add(new positionTicTacToe(2,2,1,-1));
		oneWinCondtion.add(new positionTicTacToe(3,3,0,-1));
		winningLines.add(oneWinCondtion);

		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(3,0,0,-1));
		oneWinCondtion.add(new positionTicTacToe(2,1,1,-1));
		oneWinCondtion.add(new positionTicTacToe(1,2,2,-1));
		oneWinCondtion.add(new positionTicTacToe(0,3,3,-1));
		winningLines.add(oneWinCondtion);

		oneWinCondtion = new ArrayList<positionTicTacToe>();
		oneWinCondtion.add(new positionTicTacToe(0,3,0,-1));
		oneWinCondtion.add(new positionTicTacToe(1,2,1,-1));
		oneWinCondtion.add(new positionTicTacToe(2,1,2,-1));
		oneWinCondtion.add(new positionTicTacToe(3,0,3,-1));
		winningLines.add(oneWinCondtion);

		return winningLines;

	}
	public aiTicTacToe(int setPlayer)
	{
		player = setPlayer;
		allWinningLines = initializeWinningLines();
	}
}
