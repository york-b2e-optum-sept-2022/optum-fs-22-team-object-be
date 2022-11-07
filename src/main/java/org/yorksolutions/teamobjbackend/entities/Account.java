package org.yorksolutions.teamobjbackend.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account
{
    @Id
    String id;

    @JsonProperty("PermissionLevel")
    AccountPermission permission;

    public Account()
    {
        this.pastOrders = new ArrayList<>();
    }
    public Account(String id, AccountPermission permission, String email, String password)
    {
        this.id = id;
        this.permission = permission;
        this.email = email;
        this.password = password;
        this.pastOrders = new ArrayList<>();
        this.cart = new ProductOrder();
    }

    public AccountPermission getPermission()
    {
        return permission;
    }


    @Column(unique = true)
    @JsonProperty
    String email;

    @JsonIgnore
    String password;

    @JsonIgnore
    @OneToMany
    List<ProductOrder> pastOrders;

    @OneToOne
    ProductOrder cart;

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
}
