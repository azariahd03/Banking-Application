package com.Aithani.BankingApp.Mapper;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Util.DataMaskingUtil;
import dto.AccountDto;

public class AccountMapper {
    public static AccountDto mapToAccountDto(Account account) {

        return new AccountDto(
                account.getId(),
                account.getAccountHolderName(),
                account.getBalance(),
                DataMaskingUtil.maskMobile(account.getMobile()),
                DataMaskingUtil.maskPan(account.getPan())
        );
    }

    public static Account mapToAccount(AccountDto accountDto) {

        Account account = new Account();

        account.setId(accountDto.getId());
        account.setAccountHolderName(accountDto.getAccountHolderName());
        account.setBalance(accountDto.getBalance());
        account.setMobile(accountDto.getMobile());
        account.setPan(accountDto.getPan());

        return account;
    }
}
