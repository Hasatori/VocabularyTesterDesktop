/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.sql.Connection;

/**
 *
 * @author hradi
 */
public
        interface IDatabase {

    public
            Connection getConnection();

    public
            void disconnect();
}
