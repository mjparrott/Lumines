import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The interface for Lumines
 * @author Michael Parrott
 */
public class LuminesGUI extends JFrame implements ActionListener
{
	//Constants
	private final int SQUARE_WIDTH = 20; //Width of one block (in pixels)
	private final int BOARD_WIDTH = 16; //The width of the board in blocks
	private final int BOARD_HEIGHT = 10; //The height of the board in blocks
	private final int FRAME_RATE = 12; //Frames per second
	
	//Instance variables - gameplay related
	private boolean running = true; //Whether or not the game is still running
	private LuminesInput input = new LuminesInput( this ); //Keyboard input for the game
	private Square currentSquare = new Square(); //Square that the user is controlling
	private Square[] nextSquares = new Square[3]; //What the next squares will be
	private Board luminesBoard = new Board( BOARD_HEIGHT, BOARD_WIDTH ); //The board of blocks
	private Line line = new Line(); //The line to clear the blocks
	private long startTime; //What time the current game started
	private int score = 0; //The score of the player
	private HighScore highScores; //Holds the high scores
	
	//GUI Components
	private JLabel title; //Title label
	private JLabel scoreLabel; //Score label
	private JLabel time; //Time for the current game
	private JPanel luminesGrid; //Grid for the blocks
	private JPanel nextPieces; //Shows the next 3 pieces coming
	private JPanel highScorePanel;
	private JLabel[] highScoreLabels = new JLabel[5];
	
	/**
	 * Default constructor for LuminesGUI
	 * Sets up the interface
	 */
	public LuminesGUI() throws Exception
	{
		//Do initialization required for frame
		setTitle( "Lumines: By Michael Parrott" );
		setSize( 800, 600 );
		setResizable( false );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setIconImage( Toolkit.getDefaultToolkit().getImage("LuminesIcon.jpg") );
		
		//Set up the layout of Lumines
		setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.BOTH;
		title = new JLabel( "Lumines" );
		title.setFont( new Font( "SansSerif", Font.BOLD, 38 ) );
		add( title, c );
		
		c.gridy = 1;
		scoreLabel = new JLabel( "Score:" );
		scoreLabel.setFont( new Font( "Serif", Font.PLAIN, 30 ) );
		add( scoreLabel, c );
		
		c.gridy = 2;
		c.weighty = 0.1;
		time = new JLabel( "Time: " );
		time.setFont( scoreLabel.getFont() );
		add( time, c );
		
		luminesGrid = new JPanel();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.9;
		c.weighty = 0.75;
		c.insets = new Insets( 15, 15, 15, 15 );
		c.ipadx = 5;
		luminesGrid.setVisible( true );
		add( luminesGrid, c );
		
		nextPieces = new JPanel();
		c.insets = new GridBagConstraints().insets;
		c.weightx = .2;
		
		c.gridx = 2;
		c.gridy = 1;
		nextPieces.setVisible( true );
		add( nextPieces, c ); 
		
		c.gridy = 2;
		c.weightx = 0.2;
		c.weighty = 0.1;
		
		highScorePanel = new JPanel();
		highScorePanel.setLayout( new GridLayout( 6, 1 ) );
		JLabel highScoreTitle = new JLabel( "High scores:" );
		highScoreTitle.setFont( new Font( "Serif", Font.ITALIC, 20 ) );
		highScorePanel.add( highScoreTitle );
		highScores = new HighScore( "HighScores.txt" );
		for( int i = 0; i < highScoreLabels.length; i++ )
		{
			highScoreLabels[i] = new JLabel( i + 1 + ". " + highScores.getNames()[i] + ": " + highScores.getScores()[i] );
			highScorePanel.add( highScoreLabels[i] );
		}
		add( highScorePanel, c );
		
		JMenuBar bar = new JMenuBar();
		JMenu help = new JMenu( "Help" );
		JMenuItem howToPlay = new JMenuItem( "How to play" );
		help.add( howToPlay );
		bar.add( help );
		setJMenuBar( bar );
		
		setVisible( true );
		
		//Do game initialization required
		for( int i = 0; i < 3; i++ )
		{
			nextSquares[i] = new Square();
		}
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Method to start the game
	 * @exception Thread.sleep
	 */
	public void run() throws Exception
	{
		while( running )
		{
			//Keep track of the start time of the frame
			long start = System.currentTimeMillis();
			
			//Update and draw the changes
			update();
			draw();
			
			//Get the total frame time
			long frameTime = System.currentTimeMillis() - start;
			
			//If the frame was faster than the expected frame rate then
			if( frameTime < ( 1000 / FRAME_RATE ) )
				//Stop the game until enough time has been taken
				Thread.sleep( (long)( 1000 / FRAME_RATE ) - frameTime );
		}
	}
	
	/**
	 * Get keyboard input and determine what to do with it
	 */
	private void update() throws Exception
	{
		//Erase the previous location of the square
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			for( int j = 0; j < BOARD_WIDTH; j++ )
			{
				if( luminesBoard.getPiece( i, j ) == Block.COLOUR_ONE || luminesBoard.getPiece( i, j ) == Block.COLOUR_TWO )
					luminesBoard.setPiece( i, j, Block.EMPTY );
			}
		}
		
		//Rotate the square clockwise if up is pressed
		if( input.isKeyPressed( KeyEvent.VK_UP ) )
			currentSquare.rotate();
		
		/* Move the square down if they press the down arrow or it is time for the square to
		move down on its own */
		currentSquare.moveDownAdd();
		if( currentSquare.getMovingDown() % FRAME_RATE == 0 )
		{
			currentSquare.moveDown();
		}
		else if( input.isKeyPressed( KeyEvent.VK_DOWN ) )
			currentSquare.moveDown();
		
		//Move the square left or right if possible when the arrows keys are pressed
		if( input.isKeyPressed( KeyEvent.VK_LEFT ) && isClear( currentSquare.getRow(), currentSquare.getColumn() - 1 ))
			currentSquare.moveLeft();
		
		if( input.isKeyPressed( KeyEvent.VK_RIGHT ) && isClear( currentSquare.getRow(), currentSquare.getColumn() + 1 ) )
			currentSquare.moveRight();
		
		//Prevent the square from going outside the widths of the board
		if( currentSquare.getColumn() < 0 )
			currentSquare.setColumn( 0 );
		if( currentSquare.getColumn() > 14 )
			currentSquare.setColumn( 14 );
		
		//If the square has fallen onto the board then
		if( currentSquare.getRow() >= 0 )
		{
			//If the square is going to be below the bottom of the grid then
			if( currentSquare.getRow() > 8 )
			{
				//Move it back up
				currentSquare.setRow( currentSquare.getRow() - 1 );
				
				//Solidify the position of the square
				for( int i = 0; i < 2; i++ )
				{
					for( int j = 0; j < 2; j++ )
					{
						luminesBoard.setPiece( currentSquare.getRow() + i, currentSquare.getColumn() + j, currentSquare.getBlocks()[i][j].getNext() );
					}
				}
				
				//Create a new square
				newSquare();
				//Check to see if the newly placed square made a 2x2 square of same colour blocks
				setClear();
			}
			//If the square is going to get cut in half 
			else if( luminesBoard.getPiece( currentSquare.getRow() + 1, currentSquare.getColumn() ) != Block.EMPTY 
					|| luminesBoard.getPiece( currentSquare.getRow() + 1, currentSquare.getColumn() + 1 ) != Block.EMPTY )
			{
				//Move it back up a row
				currentSquare.setRow( currentSquare.getRow() - 1 );
				
				//Check both sides for how low to go
				for( int j = 0; j < 2; j++ )
				{
					if( luminesBoard.getPiece( currentSquare.getRow() + 2, currentSquare.getColumn() + j ) == Block.EMPTY )
					{
						int curRow = currentSquare.getRow() + 2;
						boolean done = false;
						for( int i = curRow+1; i < BOARD_HEIGHT; i++ )
						{
							if( luminesBoard.getPiece( i, currentSquare.getColumn() + j ) != Block.EMPTY )
							{
								luminesBoard.setPiece( i - 1, currentSquare.getColumn() + j, currentSquare.getBlocks()[1][j].getNext() );
								luminesBoard.setPiece( i - 2, currentSquare.getColumn() + j, currentSquare.getBlocks()[0][j].getNext() );
								done = true;
								break;
							}
						}
						if( !done )
						{
							luminesBoard.setPiece( 9, currentSquare.getColumn() + j, currentSquare.getBlocks()[1][j].getNext() );
							luminesBoard.setPiece( 8, currentSquare.getColumn() + j, currentSquare.getBlocks()[0][j].getNext() );
						}
					}
					else
					{
						if( currentSquare.getRow() >= 0 )
							luminesBoard.setPiece( currentSquare.getRow(), currentSquare.getColumn() + j, currentSquare.getBlocks()[0][j].getNext() );
						
						if( currentSquare.getRow() + 1 >= 0 )
							luminesBoard.setPiece( currentSquare.getRow()+1, currentSquare.getColumn() + j, currentSquare.getBlocks()[1][j].getNext() );
					}
				}
				
				newSquare();
				setClear();
			}
			
			//Show the square on the screen
			if( currentSquare.getRow() >= 0 )
				for( int i = 0; i < 2; i++ )
					for( int j = 0; j < 2; j++ )
						luminesBoard.setPiece( currentSquare.getRow()+i, currentSquare.getColumn()+j, currentSquare.getBlocks()[i][j] );
		}
		//If the square is still above the board
		else
		{
			for( int i = 0; i < 2; i++ )
				for( int j = 0; j < 2; j++ )
					if( currentSquare.getRow()+i >= 0 && currentSquare.getColumn() + j >= 0 
							&& luminesBoard.getPiece( currentSquare.getRow() + i, currentSquare.getColumn() + j ) != Block.EMPTY )
					{
						//Tell the player they lost
						JOptionPane.showMessageDialog( null, "You lose!" );
						
						//Start up a new game
						newGame();
					}
		}
		
		//Move the line over
		line.move();
		
		//If the line just passed a column of blocks then
		if( line.getX() % SQUARE_WIDTH == 0 )
		{
			//Clear the column of blocks it just passed
			doClear( line.getX() / SQUARE_WIDTH - 1 );
		}
		
		//If the line has reached the end of the board then
		if( line.getX() > ( SQUARE_WIDTH * BOARD_WIDTH ) )
			//Move it back to the start
			line.resetLine();
		
		//Figure out the time in minutes and seconds
		long timePlayed = System.currentTimeMillis() - startTime;
		long min = timePlayed / 60000;
		long sec = ( timePlayed % 60000 ) / 1000;
		if( sec < 10 )
			time.setText( "Time: " + min + ": 0" + sec );
		else
			time.setText( "Time: " + min + ": " + sec );
		
		scoreLabel.setText( "Score: " + score );
	}
	
	/**
	 * Labelling the blocks to be cleared
	 */
	public void setClear()
	{
		boolean continueClear = true;
		
		while( continueClear )
		{
			continueClear = false;
			Block[][] tempBoard = new Block[BOARD_HEIGHT][BOARD_WIDTH];
			for( int i = 0; i < BOARD_HEIGHT; i++ )
			{
				for( int j = 0; j < BOARD_WIDTH; j++ )
				{
					tempBoard[i][j] = luminesBoard.getPiece( i, j );
				}
			}
			
			for( int i = 0; i < BOARD_HEIGHT - 1; i++ )
			{
				for( int j = 0; j < BOARD_WIDTH - 1; j++ )
				{
					if( luminesBoard.getPiece( i, j ).isFixed() )
					{
						Block pos = luminesBoard.getPiece( i, j );
						if( pos.equalColour( luminesBoard.getPiece( i + 1, j ) ) 
								&& pos.equalColour( luminesBoard.getPiece( i, j + 1 ) ) 
								&& pos.equalColour( luminesBoard.getPiece( i + 1, j + 1 ) ) )
						{
							if( tempBoard[i][j].isNotCleared() )
							{
								continueClear = true;
								tempBoard[i][j] = tempBoard[i][j].getNext();
							}
							
							if( tempBoard[i+1][j].isNotCleared() )
							{
								continueClear = true;
								tempBoard[i+1][j] = tempBoard[i+1][j].getNext();
							}
							
							if( tempBoard[i][j+1].isNotCleared() )
							{
								continueClear = true;
								tempBoard[i][j+1] = tempBoard[i+1][j+1].getNext();
							}
							
							if( tempBoard[i+1][j+1].isNotCleared() )
							{
								continueClear = true;
								tempBoard[i+1][j+1] = tempBoard[i+1][j+1].getNext();
							}
							
						}
					}
				}
			}
			
			for( int i = 0; i < BOARD_HEIGHT; i++ )
			{
				for( int j = 0; j < BOARD_WIDTH; j++ )
				{
					luminesBoard.setPiece( i, j, tempBoard[i][j] );
				}
			}
		}
	}
	
	/**
	 * Start a new game of lumines
	 */
	public void newGame() throws Exception
	{
		//Empty the board
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			for( int j = 0; j < BOARD_WIDTH; j++ )
			{
				luminesBoard.setPiece( i, j, Block.EMPTY );
			}
		}
		
		//Get new squares
		currentSquare = new Square();
		for( int i = 0; i < 3; i++ )
		{
			nextSquares[i] = new Square();
		}
		
		//Get a new line
		line = new Line();
		//Set the values of all keys to false
		input.clearKeys();
		
		//Enter the high score in if they got a new high score
		if( highScores.shouldEnter( score ) )
		{
			String s = JOptionPane.showInputDialog( "Congratulations! You got a high score. Enter your name:" );
			highScores.insertScore( s, score );
			highScores.close();
			updateHighScores();
		}
		score = 0;
		
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Clear the column of any blocks that need to be cleared
	 * @param column The column to look at
	 */
	public void doClear( int column )
	{
		/*
		 * The actual clearing of the blocks, rather than just labeling them to be cleared
		 */
		int clearCount = 0;
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			if( luminesBoard.getPiece( i, column ).isClear() )
			{
				clearCount++;
			}
		}
		
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			if( luminesBoard.getPiece( i, column ).isClear() )
			{
				for( int k = i; k >= 1; k-- )
				{
					if( luminesBoard.getPiece( k, column ).isFixed() )
						luminesBoard.setPiece( k, column, luminesBoard.getPiece( k - 1, column ) );
				}
			}
		}
		
		score += clearCount;
		
		for( int i = 0; i < clearCount; i++ )
		{
			luminesBoard.setPiece( i, column, Block.EMPTY );
		}
		
		if( luminesBoard.isBoardEmpty() && clearCount != 0 )
			score += 15;
		
		setClear();
	}
	
	/**
	 * Check to see if there is a block at the position
	 * @param row Row to look at
	 * @param column Column to look at
	 * @return Whether or not there is a square at row and column
	 */
	public boolean isClear( int row, int column )
	{
		for( int i = 0; i < 2; i++ )
		{
			for( int j = 0; j < 2; j++ )
			{
				if( column+j < BOARD_WIDTH && column+j >= 0 && row+i < BOARD_HEIGHT && row+i >= 0 
						&& luminesBoard.getPiece( row+i, column+j ) != Block.EMPTY )
					return false;
			}
		}
		
		return true;
	}
	
	
	
	/**
	 * Gives you a new square to use
	 */
	public void newSquare()
	{
		currentSquare = nextSquares[0];
		nextSquares[0] = nextSquares[1];
		nextSquares[1] = nextSquares[2];
		nextSquares[2] = new Square();
	}
	
	/**
	 * Do the drawing on the frame
	 */
	private void draw()
	{
		//Area around the grid to make room for new pieces
		int xPlus = SQUARE_WIDTH * 2;
		int yPlus = SQUARE_WIDTH * 2;
		
		BufferedImage gridImage = new BufferedImage( luminesGrid.getWidth(), luminesGrid.getHeight(), BufferedImage.TYPE_INT_RGB );
		Graphics grid = gridImage.getGraphics();
		grid.setColor( Color.white );
		grid.clearRect( 0, 0, gridImage.getWidth(), gridImage.getHeight() );
		
		BufferedImage gridPanelImage = new BufferedImage( luminesGrid.getWidth(), luminesGrid.getHeight(), BufferedImage.TYPE_INT_RGB );
		Graphics gridPanel = gridPanelImage.getGraphics();
		gridPanel.setColor( Color.white );
		gridPanel.clearRect( 0, 0, gridPanelImage.getWidth(), gridPanelImage.getHeight() );
		
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			for( int j = 0; j < BOARD_WIDTH; j++ )
			{
				grid.setColor( new Color( 0, 0, 0 ) );
				grid.drawRect( ( j * SQUARE_WIDTH ), ( i * SQUARE_WIDTH ), SQUARE_WIDTH, SQUARE_WIDTH );
				if( luminesBoard.getPiece( i, j ) == Block.EMPTY )
				{
					grid.setColor( new Color( 255, 255, 255 ) );
					grid.fillRect( 1 + ( j * SQUARE_WIDTH ), 1 + ( i * SQUARE_WIDTH ), SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
				}
				else if( luminesBoard.getPiece( i, j ) == Block.COLOUR_ONE || luminesBoard.getPiece( i, j ) == Block.COLOUR_ONE_INPLACE )
				{
					grid.setColor( new Color( 255, 0, 0 ) );
					grid.fillRect( 1 + ( j * SQUARE_WIDTH ), 1 + ( i * SQUARE_WIDTH ), SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
				}
				else if( luminesBoard.getPiece( i, j ) == Block.COLOUR_TWO || luminesBoard.getPiece( i, j ) == Block.COLOUR_TWO_INPLACE )
				{
					grid.setColor( new Color( 0, 255, 0 ) );
					grid.fillRect( 1 + ( j * SQUARE_WIDTH ), 1 + ( i * SQUARE_WIDTH ), SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
				}
				else if( luminesBoard.getPiece( i, j ) == Block.COLOUR_ONE_CLEAR )
				{
					grid.setColor( new Color( 128, 0, 0 ) );
					grid.fillRect( 1 + ( j * SQUARE_WIDTH ), 1 + ( i * SQUARE_WIDTH ), SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
				}
				else if( luminesBoard.getPiece( i, j ) == Block.COLOUR_TWO_CLEAR )
				{
					grid.setColor( new Color( 0, 128, 0 ) );
					grid.fillRect( 1 + ( j * SQUARE_WIDTH ), 1 + ( i * SQUARE_WIDTH ), SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
				}
			}
		}
		//Draw the line that clears the blocks
		grid.setColor( new Color( 0, 0, 255 ) );
		grid.drawLine( line.getX(), 0, line.getX(), 200 );
		
		gridPanel.drawImage( gridImage, xPlus, yPlus, this );
		
		if( currentSquare.getRow() < 0 )
		{
			for( int i = 0; i < 2; i++ )
			{
				for( int j = 0; j < 2; j++ )
				{
					if( currentSquare.getBlocks()[i][j] == Block.COLOUR_ONE )
					{
						gridPanel.setColor( new Color( 255, 0, 0 ) );
						gridPanel.fillRect( xPlus + 1 + ( currentSquare.getColumn() * SQUARE_WIDTH ) + (j * SQUARE_WIDTH), 
								i * SQUARE_WIDTH + ( currentSquare.getRow() + 2 ) * SQUARE_WIDTH + 1,
								SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
					}
					else
					{
						gridPanel.setColor( new Color( 0, 255, 0 ) );
						gridPanel.fillRect( xPlus + 1 + ( currentSquare.getColumn() * SQUARE_WIDTH ) + (j * SQUARE_WIDTH), 
								i * SQUARE_WIDTH + ( currentSquare.getRow() + 2 ) * SQUARE_WIDTH + 1,
								SQUARE_WIDTH - 1, SQUARE_WIDTH - 1 );
					}
				}
			}
		}
		
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			for( int j = 0; j < BOARD_WIDTH; j++ )
			{
				grid.setColor( new Color( 0, 0, 0 ) );
				grid.drawRect( ( j * SQUARE_WIDTH ), ( i * SQUARE_WIDTH ), SQUARE_WIDTH, SQUARE_WIDTH );
			}
		}
		
		luminesGrid.getGraphics().drawImage( gridPanelImage, 0, 0, this );
		
		//Draw the next squares
		Graphics nextSquaresGraphics = nextPieces.getGraphics();
		int xAdd = 30;
		int yAdd = 50;
		nextSquaresGraphics.drawString( "Next pieces:", xAdd - 10, 30 );
		
		for( int i = 0; i < nextSquares.length; i++ )
		{
			//Start from top left, clockwise
			if( nextSquares[i].getBlocks()[0][0] == Block.COLOUR_ONE )
				nextSquaresGraphics.setColor( Color.red );
			else {
				nextSquaresGraphics.setColor( Color.green );
			}
			nextSquaresGraphics.fillRect( xAdd, yAdd + i * 50, SQUARE_WIDTH, SQUARE_WIDTH );
			nextSquaresGraphics.setColor( Color.black );
			nextSquaresGraphics.drawRect( xAdd, yAdd + i * 50, SQUARE_WIDTH, SQUARE_WIDTH);
			
			if( nextSquares[i].getBlocks()[0][1] == Block.COLOUR_ONE )
				nextSquaresGraphics.setColor( Color.red );
			else {
				nextSquaresGraphics.setColor( Color.green );
			}
			
			nextSquaresGraphics.fillRect( xAdd + SQUARE_WIDTH, yAdd + i * 50, SQUARE_WIDTH, SQUARE_WIDTH );
			nextSquaresGraphics.setColor( Color.black );
			nextSquaresGraphics.drawRect( xAdd + SQUARE_WIDTH, yAdd + i * 50, SQUARE_WIDTH, SQUARE_WIDTH);
			
			if( nextSquares[i].getBlocks()[1][0] == Block.COLOUR_ONE )
				nextSquaresGraphics.setColor( Color.red );
			else {
				nextSquaresGraphics.setColor( Color.green );
			}
			nextSquaresGraphics.fillRect( xAdd, yAdd + i * 50 + SQUARE_WIDTH, SQUARE_WIDTH, SQUARE_WIDTH );
			nextSquaresGraphics.setColor( Color.black );
			nextSquaresGraphics.drawRect( xAdd, yAdd + i * 50 + SQUARE_WIDTH, SQUARE_WIDTH, SQUARE_WIDTH);
			
			if( nextSquares[i].getBlocks()[1][1] == Block.COLOUR_ONE )
				nextSquaresGraphics.setColor( Color.red );
			else {
				nextSquaresGraphics.setColor( Color.green );
			}
			nextSquaresGraphics.fillRect( xAdd + SQUARE_WIDTH, yAdd + i * 50 + SQUARE_WIDTH, SQUARE_WIDTH, SQUARE_WIDTH );
			nextSquaresGraphics.setColor( Color.black );
			nextSquaresGraphics.drawRect( xAdd + SQUARE_WIDTH, yAdd + i * 50 + SQUARE_WIDTH, SQUARE_WIDTH, SQUARE_WIDTH);
		}
	}
	
	/**
	 * Update the high score labels with new scores
	 */
	private void updateHighScores()
	{
		for( int i = 0; i < highScoreLabels.length; i++ )
		{
			highScoreLabels[i].setText( i + 1 + ". " + highScores.getNames()[i] + ": " + highScores.getScores()[i] );
		}
	}
	
	/**
	 * Print the contents of the board onto the console for analysis
	 */
	public void printBoard()
	{
		for( int i = 0; i < BOARD_HEIGHT; i++ )
		{
			for( int j = 0; j < BOARD_WIDTH; j++ )
			{
				System.out.print( luminesBoard.getPiece( i, j ) + " " );
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		
	}
}