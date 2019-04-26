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
	// return 100 if p1 won, -100 if p2 won, 0 if draw
	// if no terminal state (no win/draw) , return -1
	// player is the player's whose actual turn it is (never changed)
	// checkTerminal prioritizes winning lines for player
	private int checkTerminal(List<positionTicTacToe> board, int player) {
	    boolean p1Win = false;
	    boolean p2Win = false;
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
				if (state0 == 1) {
					if (player == 1) {
						// The current player p1 has won, return immediately
						return 100;
					}
					// else, note p1 has won, but finish checking for p2 first
					p1Win = true;
				} else if(state0 == 2) {
					if (player == 2) {
						// The current player p2 has won, return immediately
						return -100;
					}
					// else, note p2 has won, but finish checking for p1 first
					p2Win = true;
				}
			}
		}

		if (p1Win) {
			return 100;
		}
		else if (p2Win) {
			return -100;
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

	// statically evaluate the board position
	// precondition: checkTerminal() was called before this, and it returned -1
	private int evaluate(List<positionTicTacToe> position) {
        return 0;
	}
	private void markPosition(positionTicTacToe pos, List<positionTicTacToe> board, boolean maximizingPlayer) {
		
		int player = 2;
		if (maximizingPlayer) {
			player = 1;	
		}
		int index = pos.x*16+pos.y*4+pos.z;
		board.get(index).state=player;
	}
	
	private int minimax(List<positionTicTacToe> modifiedBoard, int depth, int alpha, int beta, boolean maximizingPlayer, int player) {
		int terminalScore = checkTerminal(modifiedBoard, player);
		if (terminalScore != -1) {  // -1 means no terminal position
			return terminalScore;
		}
		if (depth == 0) {
			int eval = evaluate(modifiedBoard);
			return eval;
		}
			
		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;

		// loop over all unmarked board positions
		for (positionTicTacToe found: modifiedBoard) {
			if (found.state == 0) { // found unmarked position
				List<positionTicTacToe> copiedBoard = deepCopy(modifiedBoard);
				markPosition(found, copiedBoard, maximizingPlayer);

				// recursively call minimax on child position
				int score = minimax(copiedBoard, depth - 1, alpha, beta, !maximizingPlayer, player);
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
	// player is the actual player whose turn it is (never changes)
	private positionTicTacToe minimaxWrapper(List<positionTicTacToe> actualBoard, int depth, int alpha, int beta, boolean maximizingPlayer, int player) {
		// doesnt even need to check terminal pos

		int maxEval = Integer.MIN_VALUE;
		int minEval = Integer.MAX_VALUE;
		positionTicTacToe maxMove = new positionTicTacToe(2,3,0);
		positionTicTacToe minMove = new positionTicTacToe(2,3,0);
		// if we can't find a move (aka check terminal returns that the other person wins in every move)
		// then we need to pick one

		// loop over all unmarked board positions
		for (positionTicTacToe position: actualBoard) {
			if (position.state == 0) { // found unmarked position
				List<positionTicTacToe> copiedBoard = deepCopy(actualBoard);
				markPosition(position, copiedBoard, maximizingPlayer);

				// recursively call minimax on child position
				int score = minimax(copiedBoard, depth - 1, alpha, beta, !maximizingPlayer, player);
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
		// note, if other other player has multiple win conditions and we have none, we've lost.
		// so we may pick a move that doesn't even block any win conditions

		// this also explains why 1-3-0 happens! P2 knows that they lose whatever move they make, because they can see
		// depth moves into the future, and knows that they lose 100%
        // moral: you will always beat the other ai as long (same evaluator) if you have more depth!!!!
		positionTicTacToe myNextMove;

		if (player == 1) {

			myNextMove = minimaxWrapper(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, true, player);
		} else {
			myNextMove = minimaxWrapper(board, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, false, player);
//
//			do {
//				Random rand = new Random();
//				int x = rand.nextInt(4);
//				int y = rand.nextInt(4);
//				int z = rand.nextInt(4);
//				myNextMove = new positionTicTacToe(x,y,z);
//			} while(getStateOfPositionFromBoard(myNextMove,board)!=0);
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
