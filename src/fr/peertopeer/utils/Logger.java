package fr.peertopeer.utils;

public class Logger {
	
	static private Logger logger;
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_WHITE = "\u001B[37m";
	
	static public Logger getInstance(){
		if(logger == null)
			logger = new Logger();
		return logger;
	}
	
	public Logger() {}
	
	public void success(String success){
		System.out.println(ANSI_GREEN+success+ANSI_RESET);
	}
	
	public void info(String info){
		System.out.println(ANSI_WHITE+info+ANSI_RESET);
	}
	
	public void warning(String warning){
		System.out.println(ANSI_BLUE+warning+ANSI_RESET);
	}
	
	public void debug(String debug){
		System.out.println(ANSI_YELLOW+debug+ANSI_RESET);
	}
	
	public void error(String error){
		System.out.println(ANSI_RED+error+ANSI_RESET);
	}
}
