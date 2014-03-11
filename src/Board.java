/**
 * Board
 * Holds a 2D array of blocks
 */
public class Board
{
	private Block[][] board; //The array on the screen
	
	/**
	 * Constructor to create an empty board
	 * @param h Height of the board
	 * @param w Width of the board
	 */
	public Board( int h, int w )
	{
		board = new Block[h][w];
		
		//Fill the board with empty blocks
		for( int i = 0; i < h; i++ )
		{
			for( int j = 0; j < w; j++ )
			{
				board[i][j] = Block.EMPTY;
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isBoardEmpty()
	{
		for( int i = 0; i < board.length; i++ )
			for( int j = 0; j < board[i].length; j++ )
				if( getPiece( i, j ).isFixed() )
					return false;
		
		return true;
	}
	
	/**
	 * Set a piece on the board
	 * @param row Row to set
	 * @param column Column to set
	 * @param b The block to set the piece to
	 */
	public void setPiece( int row, int column, Block b )
	{
		board[row][column] = b;
	}
	
	/**
	 * Get a piece on the board
	 * @param row The row to look at
	 * @param column The column to look at
	 * @return The piece at row and column
	 */
	public Block getPiece( int row, int column )
	{
		return board[row][column];
	}
}