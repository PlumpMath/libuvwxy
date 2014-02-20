package de.uvwxy.helper;

public class Preconditions {
	public static void checkNotNull(Object o){
		if (o == null){
			throw new RuntimeException("The parameter was null");
		}
	}
}
