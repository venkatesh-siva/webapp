package edu.csye.helper;

public class UserNameValidationHelper {
	 private static String emailpattern = "([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})";
	 
	 private static String namepattern = "^(?=.{1,60}$)[a-zA-Z]+(?:[-'\\s][a-zA-Z]+)*$";
	 
	    
	    public static boolean validateEmail(final String email)
	    {
	    	if (email.matches(emailpattern)) {
                return true;
            }
	    	return false;
	    }
	    
	    public static boolean validateName(final String name) {
	    	if (name.matches(namepattern)) {
                return true;
            }
	    	return false;
	    }
}
