/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

/**
 *
 * @author hradi
 */
public interface IUser {

    public void logout();

    abstract IUser tryLogin(String email, String password);

    public Integer getId();

    public String getName();

    public String getEmail();

}
