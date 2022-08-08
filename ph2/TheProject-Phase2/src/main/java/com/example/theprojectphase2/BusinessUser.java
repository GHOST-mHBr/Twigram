package com.example.theprojectphase2;

import java.util.ArrayList;

public class BusinessUser extends User{
       ArrayList<User> likedByUsers = new ArrayList<>();
       private String BankId;


       public BusinessUser(String userName,String passWord){this.UserName = userName; this.PassWord=passWord;}

       public void setBankId(String bankId) {
              BankId = bankId;
       }
}
