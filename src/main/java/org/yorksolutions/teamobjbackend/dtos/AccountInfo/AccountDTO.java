package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import org.yorksolutions.teamobjbackend.dtos.RequestDTO;

/**
 *
 * DTO that contains account information
 * @author Max Feige
 */
@SuppressWarnings("unused")
public class AccountDTO extends RequestDTO
{


    /**
     * Email of the user.
     */
    public String email;
    /**
     * Password of the user
     */
    public String password;
    /**
     * Permission level of the user
     */
    public String permission;
}
