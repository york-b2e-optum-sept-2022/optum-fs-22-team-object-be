package org.yorksolutions.teamobjbackend.dtos;

import org.yorksolutions.teamobjbackend.entities.AccountPermission;

public class AccountDTO extends RequestDTO
{
    public String email;
    public String password;
    public String permission;
}
