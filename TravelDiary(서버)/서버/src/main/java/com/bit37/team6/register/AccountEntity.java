package com.bit37.team6.register;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userPw;

    public AccountEntity toAccountEntity(RegisterDTO registerDTO){
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUserId(registerDTO.getUserId());
        accountEntity.setUserPw(registerDTO.getUserPw());
        return accountEntity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
}
