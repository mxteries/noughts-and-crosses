all: game
clean:
	rm -f *.class

game: runTicTacToe.java aiTicTacToe.java positionTicTacToe.java
	javac runTicTacToe.java
