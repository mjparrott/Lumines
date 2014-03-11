/**
 * One block on the grid
 */
public enum Block 
{
	//All possiblities of the Block type
	EMPTY, COLOUR_ONE, COLOUR_TWO, COLOUR_ONE_INPLACE, COLOUR_TWO_INPLACE, COLOUR_ONE_CLEAR, COLOUR_TWO_CLEAR;
	
	/**
	 * The next type of block
	 * @return The next block type
	 */
	public Block getNext()
	{
		if( this == COLOUR_ONE )
			return COLOUR_ONE_INPLACE;
		else if( this == COLOUR_TWO )
			return COLOUR_TWO_INPLACE;
		else if( this == COLOUR_ONE_INPLACE )
			return COLOUR_ONE_CLEAR;
		else if( this == COLOUR_TWO_INPLACE )
			return COLOUR_TWO_CLEAR;
		else
			return this;
	}
	
	/**
	 * See if the block is colour one
	 * @return If the block is colour one
	 */
	public boolean isColourOne()
	{
		return this == COLOUR_ONE || this == COLOUR_ONE_INPLACE || this == COLOUR_ONE_CLEAR;
	}
	
	/**
	 * See if the block is colour two
	 * @return If the block is colour two
	 */
	public boolean isColourTwo()
	{
		return this == COLOUR_TWO || this == COLOUR_TWO_INPLACE || this == COLOUR_TWO_CLEAR;
	}
	
	/**
	 * See if the block is fixed in place
	 * @return If the block is fixed in place
	 */
	public boolean isFixed()
	{
		return this == COLOUR_ONE_INPLACE || this == COLOUR_ONE_CLEAR ||
		this == COLOUR_TWO_INPLACE || this == COLOUR_TWO_CLEAR;
	}
	
	/**
	 * See if the block should be cleared by the line
	 * @return If the block should be cleared
	 */
	public boolean isClear()
	{
		return this == COLOUR_ONE_CLEAR || this == COLOUR_TWO_CLEAR;
	}
	
	/**
	 * Check for the colour against another block
	 * @param b Block to check with
	 * @return If the two blocks are the same colour
	 */
	public boolean equalColour( Block b )
	{
		return ( ( this == b ) || ( b.getNext() == this ) || ( this.getNext() == b ) ) 
			&& isFixed() && b.isFixed();
	}
	
	/**
	 * See if the block is not going to be cleared
	 * @return If the block is not going to be cleared
	 */
	public boolean isNotCleared()
	{
		return this != COLOUR_ONE_CLEAR && this != COLOUR_TWO_CLEAR;
	}
}