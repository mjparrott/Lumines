import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * Keyboard input for Lumines
 */
public class LuminesInput implements KeyListener
{
	boolean[] keys = new boolean[256]; //Holds the current state of the all of the keyboard keys
	
	/**
	 * Setup the KeyListener for the GUI component
	 * @param c Component to get keyboard input from
	 */
	public LuminesInput( Component c )
	{
		c.addKeyListener( this );
	}
	
	/**
	 * Resets the values of all of the keys pressed to false
	 * Fixes a problem where the program thinks the key is still pressed after you lose
	 */
	public void clearKeys()
	{
		Arrays.fill( keys, false );
	}
	
	/**
	 * Check to see if the key with keycode key is currently pressed
	 * @param key The keycode of the key to check for
	 * @return Whether or not the key is currently pressed
	 */
	public boolean isKeyPressed( int key )
	{
		if( key >= 0 && key < keys.length )
			return keys[key];
		else
			return false;
	}
	
	/**
	 * Sets the state of the key in e to pressed
	 */
	public void keyPressed( KeyEvent e )
	{
		if( e.getKeyCode() >= 0 && e.getKeyCode() < keys.length )
			keys[e.getKeyCode()] = true;
	}
	
	/**
	 * Sets the state of the key in e to released
	 */
	public void keyReleased( KeyEvent e )
	{
		if( e.getKeyCode() >= 0 && e.getKeyCode() < keys.length )
			keys[e.getKeyCode()] = false;
	}
	
	/**
	 * Necessary for KeyListener interface
	 */
	public void keyTyped( KeyEvent e )
	{
	}
}