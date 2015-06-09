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
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Switcher
 */
public class Server {
    private static final long serialVersionUID=1L;
    public static void main(String arga[]){
        String portNum ="12345";
        String registryURL;
        try{
            int RMIPortNum = Integer.parseInt(portNum);
            startRegistry(RMIPortNum);
            POSImpl pos = new POSImpl();
            registryURL = "rmi://localhost:"+portNum+"/pos";
            Naming.rebind(registryURL, pos);
            System.out.println("Server ready");
        }catch(Exception re){
            System.out.println("Exception in creating and starting registry:"+re);
            
        }
    }
    public static void startRegistry(int RMIPortNum) throws RemoteException{
        try{
            Registry reg = LocateRegistry.getRegistry(RMIPortNum);
            reg.list();
        }catch (RemoteException re) {
            System.out.println("RMI registry cannot be located at port:"
                    + RMIPortNum);
            Registry reg = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port:"
                    + RMIPortNum);
        }
        
    }
}
