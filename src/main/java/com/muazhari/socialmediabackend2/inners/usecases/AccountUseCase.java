package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.outers.deliveries.gateways.AccountGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountUseCase {
    @Autowired
    AccountGateway accountGateway;

    public Account getAccountById(UUID accountId) throws UsernameNotFoundException {
        return accountGateway.getAccountById(accountId);
    }
}
