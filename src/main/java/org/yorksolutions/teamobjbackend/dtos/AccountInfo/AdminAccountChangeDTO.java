package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

/**
 * AdminAccountChangeDTO is an AccountDTO with an extra field, the ID of the account to change.  It is used
 * for an admin to edit an account that isn't theirs.
 *
 * @author Max Feige
 *
 */
@SuppressWarnings("unused")
public class AdminAccountChangeDTO extends AccountDTO
{

    /**
     * ID of the user to edit
     */
    public String accountChangeID;
}
