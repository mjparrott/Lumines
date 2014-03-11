import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to keep track of high scores
 */
public class HighScore
{
	//index 0 is the highest score
	private ArrayList<String> names = new ArrayList<String>(); //Names of people who got high scores
	private ArrayList<Integer> scores = new ArrayList<Integer>(); //The actual high scores
	private String file; //The file that the high scores were read from
	
	/**
	 * Read in the high scores and store them
	 * @param txtFile The text file to read the high scores from
	 * @throws Exception
	 */
	public HighScore( String txtFile ) throws Exception
	{
		Scanner in = new Scanner( new FileReader( txtFile ) );
		
		//Add all of the high scores in the file
		while( in.hasNext() )
		{
			names.add( in.next() );
			scores.add( in.nextInt() );
		}
		
		
		file = txtFile;
		//Close the file
		in.close();
	}
	
	/**
	 * Whether or not the score is a high score
	 * @param n The score to check
	 * @return If n is a high score
	 */
	public boolean shouldEnter( int n )
	{
		for( int i = 0; i < names.size(); i++ )
		{
			if( n > scores.get(i) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * Insert the score given into the high score list
	 * @param s The name of the person who got the score
	 * @param n The score they got
	 */
	public void insertScore( String s, int n )
	{
		int[] tempN = getScores();
		String[] tempS = getNames();
		for( int i = 0; i < scores.size(); i++ )
		{
			if( scores.get(i) < n )
			{
				for( int j = i+1; j < scores.size(); j++ )
				{
					scores.set( j, tempN[j - 1] );
					names.set( j, tempS[j - 1] );
				}
				scores.set( i, n );
				names.set( i, s );
				break;
			}
		}
	}
	
	/**
	 * Resave the high scores
	 */
	public void close() throws Exception
	{
		PrintWriter out = new PrintWriter( new FileWriter( file ) );
		
		//Put all of the high score names and scores into the file
		//Name separted by a space then the score
		//Prevents having spaces in the name
		for( int i = 0; i < scores.size(); i++ )
		{
			out.println( names.get(i) + " " + scores.get(i) );
		}
		
		out.close();
	}
	
	/**
	 * Return the scores as an array
	 * @return The high score list
	 */
	public int[] getScores()
	{
		int[] arr = new int[scores.size()];
		for( int i = 0; i < arr.length; i++ )
			arr[i] = scores.get(i);
		
		return arr;
	}
	
	/**
	 * Return the names as an array
	 * @return The names on the high score list
	 */
	public String[] getNames()
	{
		String[] arr = new String[names.size()];
		for( int i = 0; i < arr.length; i++ )
			arr[i] = names.get(i);
		
		return arr;
	}
}