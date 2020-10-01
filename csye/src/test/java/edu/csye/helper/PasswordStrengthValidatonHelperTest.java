package edu.csye.helper;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class PasswordStrengthValidatonHelperTest {

	@Test
    void hashPasswordPositiveTest() {
    	
        boolean result = PasswordStrengthValidationHelper.validatePassword("TestValue@123");
        assertTrue(result);
    }
	
    @Test
    void hashPassword() {
    	
        boolean result = PasswordStrengthValidationHelper.validatePassword("123");
        assertFalse(result);
    }

}
