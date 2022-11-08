package org.yorksolutions.teamobjbackend.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
public class Account
{
    @Id
    String id;

    public String getId()
    {
        return id;
    }

    @JsonProperty("PermissionLevel")
    AccountPermission permission;

    public Account()
    {
        this.pastOrders = new ArrayList<>();
    }

    public void setPermission(AccountPermission permission)
    {
        this.permission = permission;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public Account(String id, AccountPermission permission, String email, String password)
    {
        this.id = id;
        this.permission = permission;
        this.email = email;
        this.password = password;
        this.pastOrders = new ArrayList<>();
        newCart();
    }

    @JsonProperty
    public AccountPermission getPermission()
    {
        return permission;
    }


    public List<ProductOrder> getPastOrders()
    {
        return pastOrders;
    }

    @Column(unique = true)
    @JsonProperty
    String email;

    @JsonIgnore
    String password;

    @JsonIgnore
    @OneToMany
    List<ProductOrder> pastOrders;


    @JsonIgnore
    @OneToOne
    ProductOrder cart;

    @JsonGetter("PermissionLevel")
    public String permissionAsString()
    {
        return permission.name();
    }

    public ProductOrder getCart()
    {
        return cart;
    }

    @JsonSetter("PermissionLevel")
    public void permissionFromInteger(String en)
    {
        this.permission = AccountPermission.valueOf(en);
    }

    public void newCart()
    {
        cart = new ProductOrder(YorkUtils.GenerateUUID());
    }
}
