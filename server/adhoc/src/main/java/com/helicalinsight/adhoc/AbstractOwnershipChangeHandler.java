package com.helicalinsight.adhoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * The {@code AbstractOwnershipChangeHandler} class is an abstract class providing a foundation for handling ownership changes.
 */
public abstract class AbstractOwnershipChangeHandler {
	/**
     * The {@code UserService} instance is used to interact with user-related operations.
     * In Spring, when you have multiple beans of the same type and want to specify which one to inject,
     * you can use the '@Qualifier' annotation.
     */
	@Qualifier("userDetailsService")
	@Autowired
	protected UserService userService;
	
	/**
     * Abstract method to be implemented by subclasses for handling ownership change.
     *
     * @param resourceId 		resource for which ownership is to be changed.
     * @param ownerId   		id of the new owner.
     * @return {@code true} if ownership change is successful, {@code false} otherwise.
     */
	abstract boolean change(Integer resourceId , Integer ownerId);
	
	/**
     * it checks the request is initiated by a user with admin role.
     *
     * @return {@code true} if the request is from a user with admin role, {@code false} otherwise.
     */
	protected boolean requestedFromAdminRole() {
		String currentLoggedInUserId= SecurityUtils.securityObject().getCreatedBy();
		User currentlyLoggedInUser = userService.findUser(Integer.valueOf(currentLoggedInUserId));
		return isAdmin(currentlyLoggedInUser);
	}
	/**
     * Checks the given user has admin role or not.
     *
     * @param user 		 user to be checked for admin role.
     * @return {@code true} if the user has admin role, {@code false} otherwise.
     */
	protected boolean isAdmin(User user) {
		return user.getRoles()
				.stream()
				.anyMatch(role -> role.getRole_name().equalsIgnoreCase("ROLE_ADMIN"));
	}
}
