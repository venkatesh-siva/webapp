package edu.csye.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryNameValidatorHelper {
	 private static String pattern = "[$&+,:;=\\\\\\\\?@#|/'<>.^*()%!-]";
	 
	    
	    public static boolean validateCategoryName(final String password)
	    {
	        Pattern p = Pattern.compile(pattern);
	        Matcher m = p.matcher(password);
	        return m.find();
	    }
}
