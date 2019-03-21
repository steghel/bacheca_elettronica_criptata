package client;

import java.util.*;
import java.io.*;
public class Console
{	private static BufferedReader tas = new BufferedReader(new InputStreamReader(System.in));
	private static PrintWriter vid = new PrintWriter(System.out);
	public static char leggiUnCarattere()
	{ 	try { int i; char c;
		      i = tas.read();
		      if (i == -1) throw new Exception("marca EndStream"); 
		      c = (char)i; return c;   
		    }
		catch(Exception e){System.out.println(e.getMessage());
				   return '\0';}
	}
	public static void scriviUnCarattere(char c)
	{	vid.write(c);
		vid.flush();
	}
	private static Scanner tastiera = new Scanner(System.in);
	private static PrintWriter video = new PrintWriter(System.out, true);
	// letture	
	public static boolean leggiBooleano()
	{	return tastiera.nextBoolean();	}
	public static char leggiCarattere()
	{	try {	String s = tastiera.next();
			if (s.length() > 1) throw new Exception("troppi caratteri");
			return s.charAt(0);
		    }
		catch(Exception e){System.out.println(e.getMessage());
		                   return '\0';}
	}
	public static int leggiIntero()
	{	return tastiera.nextInt();	}
	public static double leggiReale()
	{	return tastiera.nextDouble();	}
	public static String leggiStringa()
	{	return tastiera.next();	}
	public static String leggiLinea()
	{	return tastiera.nextLine();	}
	// scritture di linee
	public static void scriviBooleano(boolean b)
	{	video.println(b);	}
	public static void scriviCarattere(char c)
	{	video.println(c);	}
	public static void scriviIntero(int i)
	{	video.println(i);	}
	public static void scriviReale(double d) 
	{	video.println(d);	}
	public static void scriviStringa(String s)
	{	video.println(s);	}
	public static void nuovaLinea() 
	{	video.println();	}
	// scritture di parole + spazio
	public static void scriviBool(boolean b)
	{	video.print(b); video.print(' ');	}
	public static void scriviCar(char c)
	{	video.print(c); video.print(' ');	}
	public static void scriviInt(int i)
	{	video.print(i); video.print(' ');	}
	public static void scriviReal(double d) 
	{	video.print(d); video.print(' ');	}
	public static void scriviStr(String s)
	{	video.print(s); video.print(' ');	}
	// compatibilita`
	public static boolean leggiBool()
	{	return leggiBooleano();	}
	public static char leggiCar()
	{	return leggiCarattere();	}
	public static int leggiInt()
	{	return leggiIntero();	}
	public static double leggiReal()
	{	return leggiReale();	}
	public static String leggiStr()
	{	return leggiStringa();	}	
}


