package org.yorksolutions.teamobjbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.*;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.entities.ProductOrder;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductOrderRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Service used for CRUD operations relating to user accounts
 */
@Service
public class AccountService
{
    private final AccountRepository accountRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, ProductOrderRepository productOrderRepository, ProductRepository productRepository)
    {
        this.accountRepository = accountRepository;
        this.productOrderRepository = productOrderRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    private void init()
    {
        if(this.accountRepository.count() == 0)
        {
            Account account = new Account(YorkUtils.GenerateUUID(),AccountPermission.ADMIN,"admin","admin");
            this.productOrderRepository.save(account.getCart());
            this.accountRepository.save(account);
        }
    }


    /**
     * Attempts to register a new user
     * @param dto Relevant account data
     * @return The ID of the newly registered user
     * @throws ResponseStatusException CONFLICT on email collision, FORBIDDEN on customer/shopkeeper trying to make an account for another person,
     */
    public String AttemptRegister(AccountDTO dto) throws ResponseStatusException
    {
        Optional<Account> existingAccount = accountRepository.findAccountByEmail(dto.email);
        if(existingAccount.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Account with email already exists");
        }
        AccountPermission permissionLevel = AccountPermission.CUSTOMER;
        //Guard for creating something of a high level
        if(dto.permission != null)
        {
            var requestedPerm = AccountPermission.valueOf(dto.permission);
            if(requestedPerm != AccountPermission.CUSTOMER)
            {
                if(dto.userID == null)
                {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account creation of high permission must be made by administrator");
                }
                var creator = accountRepository.findById(dto.userID);
                if(creator.isEmpty() || creator.get().getPermission() != AccountPermission.ADMIN)
                {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account creation of high permission must be made by administrator");
                }
                permissionLevel = requestedPerm;
            }
        }
        Account newAccount = new Account(YorkUtils.GenerateUUID(),permissionLevel,dto.email,dto.password);
        productOrderRepository.save(newAccount.getCart());
        accountRepository.save(newAccount);
        return newAccount.getId();
    }

    /**
     * Attempts to login an existing user
     * @param dto account information
     * @return The ID of the logged in user
     * @throws ResponseStatusException BAD_REQUEST on invalid login credentials
     */
    public String AttemptLogin(AccountDTO dto) throws ResponseStatusException
    {
        Optional<Account> existingAccount = accountRepository.findAccountByEmail(dto.email);
        if(existingAccount.isEmpty() || !existingAccount.get().getPassword().equals(dto.password))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid login credentials");
        }
        return existingAccount.get().getId();
    }

    /**
     * Edits the reqeusters account - non admin, cannot change permissions
     * @param dto
     * @return ID of the edited account
     * @throws ResponseStatusException BAD_REQUEST on invalid userID, CONFLICT on changing to a taken email
     */

    public String EditAccount(AccountDTO dto) throws ResponseStatusException
    {
        Account acc = this.GetRequesterAccount(dto);
        Optional<Account> emailAcc = accountRepository.findAccountByEmail(dto.email);

        //If the meail account exists and is not the same ID as the requester account, then they are trying to chagne email to something already taken.
        //ADMIN can change other account info
        if( emailAcc.isPresent() && !emailAcc.get().getId().equals(acc.getId()))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already taken");
        }

        if(dto.email != null)
        {
            acc.setEmail(dto.email);
        }
        if(dto.password != null)
        {
            acc.setPassword(dto.password);

        }
        this.accountRepository.save(acc);

        return acc.getId();

    }

    /**
     * Edits an account as teh admin
     * @param dto relevant account information
     * @return ID of the editeed account
     * @throws ResponseStatusException FORBIDDEN on non-admin access, NOT_FOUND on invalid accountChangeID,BAD_REQUEST on invalid userID or invalid permission level
     */
    public String EditAccountAdmin(AdminAccountChangeDTO dto) throws ResponseStatusException
    {
        Account acc = this.GetRequesterAccount(dto);
        if(acc.getPermission() != AccountPermission.ADMIN)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only admins can edit other accounts");
        }
        Optional<Account> targetAct = accountRepository.findById(dto.accountChangeID);
        if(targetAct.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find target account ID");
        }
        Account changeAct = targetAct.get();

        AccountPermission permission = changeAct.getPermission();
        if(dto.permission != null)
        {
            try
            {
                permission = AccountPermission.valueOf(dto.permission);
            }
            catch(Exception e)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid permission value");
            }
        }
        if(dto.email != null)
        {
            changeAct.setEmail(dto.email);
        }
        if(dto.password != null)
        {
            changeAct.setPassword(dto.password);
        }
        changeAct.setPermission(permission);

        this.accountRepository.save(changeAct);

        return changeAct.getId();

    }

    /**
     *
     * @param dto
     * @throws ResponseStatusException BAD_REQUEST on invalid userID, FORBIDDEN on attempting to delete own account as admin , NOT_FOUND on admin attempting to delete unknown email address
     */
    public void DeleteAccount(AccountDTO dto) throws ResponseStatusException
    {
        Account requester = GetRequesterAccount(dto);
        if(dto.email == null && requester.getPermission() == AccountPermission.ADMIN)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You cannot delete your own account as an admin");
        }
        //user deletes self
        else if(dto.email == null)
        {
            this.productOrderRepository.delete(requester.getCart());
            this.accountRepository.delete(requester);
            return;
        }
        Optional<Account> targetAccount = this.accountRepository.findAccountByEmail(dto.email);

        if(targetAccount.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find account with that email");
        }
        boolean sameAcct = targetAccount.get().getId().equals(requester.getId());
        //if requester is NOT admin and the two accounts are DIFFERENET, then they are trying to delete someone elses account
        if(requester.getPermission() != AccountPermission.ADMIN && !sameAcct)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not have permission to delete another account");
        }

        if(requester.getPermission() == AccountPermission.ADMIN && sameAcct)
        {
            //if admin is attempting to delete their own account thats bad
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You cannot delete your own account as an admin");
        }
        this.productOrderRepository.delete(targetAccount.get().getCart());
        this.accountRepository.delete(targetAccount.get());


    }
    public Account GetAccountByEmail(AccountDTO dto) throws ResponseStatusException
    {
        Account req = GetRequesterAccount(dto);
        if(req.getPermission() != AccountPermission.ADMIN)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only admins can request accounts by email");
        }
        Optional<Account> found = this.accountRepository.findAccountByEmail(dto.email);
        if(found.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find account with that email");
        }
        return found.get();

    }
    public List<Account> GetAllAccounts(RequestDTO dto) throws ResponseStatusException
    {
        Account acc = GetRequesterAccount(dto);
        if(acc.getPermission() != AccountPermission.ADMIN)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only admins can request all accounts");
        }
        var all = this.accountRepository.findAll();
        ArrayList<Account> accs = new ArrayList<>();
        all.forEach(accs::add);
        return accs;
    }
    private Account GetRequesterAccount(RequestDTO dto) throws ResponseStatusException
    {
        if(dto.userID == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"must provide userID");
        }
        Optional<Account> acc = this.accountRepository.findById(dto.userID);
        if(acc.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid userID");
        }
        return acc.get();
    }
    public String GetPermissionLevel(RequestDTO dto) throws ResponseStatusException
    {
        Account c = GetRequesterAccount(dto);
        return c.permissionAsString();
    }
    public void Checkout(RequestDTO dto) throws ResponseStatusException
    {
        Account acc = GetRequesterAccount(dto);
        var cart = acc.getCart();
        if(cart.getProductsOrdered().size() == 0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot checkout with 0 items");
        }
        cart.setTotal(-1.0);
        cart.setDate(System.currentTimeMillis());
        acc.getPastOrders().add(cart);
        //save old cart, add to list, make new cart
        productOrderRepository.save(cart);
        acc.newCart();
        productOrderRepository.save(acc.getCart());
        accountRepository.save(acc);
    }
    public List<OrderDTO> GetHistory(RequestDTO dto) throws ResponseStatusException
    {
        Account acc = GetRequesterAccount(dto);
        Set<String> allProductIDs = new HashSet<>();
        var pastOrders = acc.getPastOrders();
        for(ProductOrder po : pastOrders)
        {
            allProductIDs.addAll(po.getProductsOrdered().keySet());
        }
        //Essentially collect all product ID strings for a batched repository request
        Iterable<Product> correspondingProducts = this.productRepository.findAllByProductIDIn(allProductIDs);
        HashMap<String, Product> idProductMap = new HashMap<>();
        for(Product p : correspondingProducts)
        {
            idProductMap.put(p.getProductID(),p);
        }
        List<OrderDTO> orderHistory = new ArrayList<>();
        for(ProductOrder po : pastOrders)
        {
            orderHistory.add(OrderDTO.FromProductOrder(po,idProductMap));
        }
        return orderHistory;

    }

    public void AddToCart(CartChangeDTO dto) throws ResponseStatusException
    {
        Account acc = GetRequesterAccount(dto);
        Optional<Product> prod = productRepository.findById(dto.productID);
        if(prod.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find product with that ID");
        }
        if(dto.number < 0)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have negative number of products");
        }
        if(dto.number == 0)
        {
            acc.getCart().RemoveProduct(prod.get());
        }
        else
        {
            acc.getCart().SetProduct(prod.get(),dto.number);
        }
        this.productOrderRepository.save(acc.getCart());
        this.accountRepository.save(acc);
    }
    public OrderDTO GetCart(RequestDTO dto) throws ResponseStatusException
    {
        Account acc = GetRequesterAccount(dto);
        Iterable<Product> correspondingProducts = this.productRepository.findAllByProductIDIn(acc.getCart().getProductsOrdered().keySet());
        HashMap<String, Product> prods = new HashMap<>();
        for(Product p : correspondingProducts)
        {
            prods.put(p.getProductID(),p);
        }
        return OrderDTO.FromProductOrder(acc.getCart(),prods);
    }

    public void TestClear()
    {
        this.accountRepository.deleteAll();
        this.productOrderRepository.deleteAll();
        init();
    }
}
