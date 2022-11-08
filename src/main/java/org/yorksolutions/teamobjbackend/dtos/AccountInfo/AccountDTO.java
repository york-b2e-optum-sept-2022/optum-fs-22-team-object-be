package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import org.yorksolutions.teamobjbackend.dtos.RequestDTO;

@SuppressWarnings("unused")
public class AccountDTO extends RequestDTO
{
    public String email;
    public String password;
    public String permission;
}
