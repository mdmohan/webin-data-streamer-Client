/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.sauronsoftware.ftp4j;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asenf
 */
public class FTPTransferClient extends Thread {

    private final int index;
    private final String path;
    private final String dest;
    private final FTPClient c;
    private boolean download;
    private String threadname;
    
    public FTPTransferClient(int index, String path, String dest, FTPClient c) {
        this.index = index;
        this.path = path;
        this.dest = dest;
        this.c = c;
        
    }
    
    @Override
    public void run() {
        try {
            System.out.println("(" + index + ") Downloading " + path + " to " + dest);
            c.download(path, new File(dest));
        } catch (IllegalStateException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPIllegalReplyException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPDataTransferException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FTPAbortedException ex) {
            Logger.getLogger(FTPTransferClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getIndex() {
        return this.index;
    }
    
    public boolean getDownload() {
        return this.download;
    }
    
    public String getDest() {
        return this.dest;
    }
    
}

