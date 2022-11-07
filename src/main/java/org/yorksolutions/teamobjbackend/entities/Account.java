package org.yorksolutions.teamobjbackend.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.List;

@Entity
public class Account
{
    @Id
    String id;

    @JsonProperty("PermissionLevel")
    AccountPermission permission;

    public AccountPermission getPermission()
    {
        return permission;
    }

    @JsonGetter("PermissionLevel")
    public String permissionAsString()
    {
        return permission.name();
    }
    @JsonSetter("PermissionLevel")
    public void permissionFromInteger(String en)
    {
        this.permission = AccountPermission.valueOf(en);
    }

    @JsonProperty
    String email;

    @JsonIgnore
    String password;

    @JsonIgnore
    @OneToMany
    List<ProductOrder> pastOrders;

    @OneToOne
    ProductOrder cart;


}
