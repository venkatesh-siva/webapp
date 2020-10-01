package edu.csye.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class PasswordStrengthValidationHelper
{
    private static String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})";
 
    
    public static boolean validatePassword(final String password)
    {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.find();
    }
}