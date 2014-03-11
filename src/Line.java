/**
 * The line that clears the blocks on the screen
 */
public class Line
{
	private int x; //Position of the line on the grid
	private final int SPEED = 4; //How fast to move the line each frame (in pixels)
	
	/**
	 * Constructor
	 */
	public Line()
	{
		x = 0;
	}
	
	/**
	 * Move the line
	 */
	public void move()
	{
		x += SPEED;
	}
	
	/**
	 * Move the line back to the start
	 */
	public void resetLine()
	{
		x = 0;
	}
	
	/**
	 * Returns x co-ordinate
	 * @return The x co-ordinate of the line, relative to where it is on the grid
	 */
	public int getX()
	{
		return x;
	}
}