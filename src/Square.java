import java.util.Random;

/**
 * Square
 * 2x2 Square of blocks
 */
public class Square
{
	private Block[][] blocks = new Block[2][2]; //What blocks are in the square
	private int row; //The row of the top left block of the square
	private int column; //The column of the top left block of the square
	private int movingDown; //Number of frames since the piece was moved down
	private long lastRotate = 0; //Time when the piece was last rotated
	
	/**
	 * Default constructor to create a new square of blocks
	 */
	public Square()
	{
		Random rnd = new Random();
		
		//Create a random arrangement of blocks
		for( int i = 0; i < 2; i++ )
			for( int j = 0; j < 2; j++ )
				blocks[i][j] = Block.values()[ rnd.nextInt(2) + 1 ];
		
		//Set the position to the middle of the board
		column = 8;
		row = -2;
		movingDown = 0;
	}
	
	/**
	 * Rotate the blocks in the 
	 */
	public void rotate()
	{
		//If enough time has passed since last rotation then
		if( System.currentTimeMillis() - lastRotate > 100 )
		{
			//Perform a clockwise rottation of the blocks
			Block[][] tempBlocks = new Block[2][2];
			for( int i = 0; i < 2; i++ )
				for( int j = 0; j < 2; j++ )
					tempBlocks[i][j] = blocks[i][j];
			
			//Rotate clockwise
			blocks[0][1] = tempBlocks[0][0];
			blocks[1][1] = tempBlocks[0][1];
			blocks[1][0] = tempBlocks[1][1];
			blocks[0][0] = tempBlocks[1][0];
			
			lastRotate = System.currentTimeMillis();
		}
	}
	
	/**
	 * Increase the number of frames since the square last moved down
	 */
	public void moveDownAdd()
	{
		movingDown++;
	}
	
	/**
	 * Move the square down one row
	 */
	public void moveDown()
	{
		row++;
	}
	
	/**
	 * Move the square one column to the left
	 */
	public void moveLeft()
	{
		column--;
	}
	
	/**
	 * Move the square one column to the right
	 */
	public void moveRight()
	{
		column++;
	}

	/**
	 * @return The blocks
	 */
	public Block[][] getBlocks()
	{
		return blocks;
	}

	/**
	 * @param Blocks the blocks to set
	 */
	public void setBlocks(Block[][] blocks)
	{
		this.blocks = blocks;
	}

	/**
	 * @return the row
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row)
	{
		this.row = row;
	}

	/**
	 * @return the column
	 */
	public int getColumn()
	{
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column)
	{
		this.column = column;
	}

	/**
	 * @return the movingDown
	 */
	public int getMovingDown()
	{
		return movingDown;
	}

	/**
	 * @return the lastRotate
	 */
	public long getLastRotate()
	{
		return lastRotate;
	}
}