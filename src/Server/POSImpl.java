/*
 * The MIT License
 *
 * Copyright 2015 Switcher.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Server;

import Common.BingoPOSInterface;
import Common.Order;
import bingopos.dbConnect;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Switcher
 */

//Sell each item to orders table, then when sale closed set invoice total

public class POSImpl extends UnicastRemoteObject implements BingoPOSInterface{
    
    Connection c;
    PreparedStatement pstm;
    ResultSet rs;
    private int invNum = 0;
    public POSImpl() throws RemoteException{
        super();
        List<Order> order = new ArrayList();
    }

    /**
     *Get the date and time formatted into a string
     * @return Formatted string of the date and time
     * @throws RemoteException
     */
    public synchronized String getDateTime() throws RemoteException{
        DateFormat df = DateFormat.getDateTimeInstance (DateFormat.MEDIUM, DateFormat.MEDIUM, new Locale ("en", "EN"));
        String formattedDate = df.format (new Date ());
        return formattedDate;
    }
    
    /**
     *Returns time only
     * @return Timestamp, used in SQL
     */
    public synchronized static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    /**
     * Creates an invoice in the database and returns the invoice number
     * @param userID The current user logged into the POS
     * @return Returns invoice number created
     * @throws RemoteException
     */
    public synchronized int createInvoice(int userID) throws RemoteException{
        invNum = 0;
        PreparedStatement createInv = null;
        PreparedStatement getNum = null;
        String createInvSt = "INSERT INTO invoices(time, users_idusers) VALUES(?,?)";
        String getNumSt = "SELECT last_insert_id() as last_id FROM invoices";
        try{
            c = dbConnect.getConnection();
            createInv = c.prepareStatement(createInvSt, PreparedStatement.RETURN_GENERATED_KEYS);
            c.setAutoCommit(false);
            createInv.setTimestamp(1,getCurrentTimeStamp());
            createInv.setInt(2,userID);
            createInv.executeUpdate();
            getNum = c.prepareStatement(getNumSt);
            rs = getNum.executeQuery();
            rs.next();
            invNum = rs.getInt("last_id");
            c.commit();
        }catch (SQLException ex) {
            System.out.println("Unable to insert invoice: " + ex);
            if (c!= null){
                try{
                    System.out.println("Transaction being rolled back.");
                    c.rollback();
                }catch (SQLException sqlex) {
                    System.out.println("Error rolling back" + sqlex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(POSImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
        if (createInv != null){
            try {
                createInv.close();
                c.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(POSImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        if (getNum != null){
            try{
                getNum.close();
                c.setAutoCommit(true);
            }catch (SQLException ex2) {
                Logger.getLogger(POSImpl.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
            
    }
        return invNum;
    }
    public synchronized void sellItem(int prodId, double price, int qty){
        //if prod id same then increae qty
        
        
    }
    
}
