package com.helicalinsight.admin;
import com.helicalinsight.admin.model.User;
import org.junit.Test;
import org.junit.Assert;

public class ExternalUserTest {


    @Test
    public void shouldReturnFalseWhenValueIsNull() {
        User user = new User();
        user.setIsExternallyAuthenticated(null);

        Assert.assertFalse(user.getIsExternallyAuthenticated());
    }

    @Test
   public void shouldReturnFalseWhenValueIsFalse() {
        User user = new User();
        user.setIsExternallyAuthenticated(false);

        Assert.assertFalse(user.getIsExternallyAuthenticated());
    }

    @Test
    public void shouldReturnTrueWhenValueIsTrue() {
        User user = new User();
        user.setIsExternallyAuthenticated(true);

        Assert.assertTrue(user.getIsExternallyAuthenticated());
    }
}