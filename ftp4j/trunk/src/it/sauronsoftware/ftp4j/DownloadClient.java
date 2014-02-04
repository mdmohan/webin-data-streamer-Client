/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.sauronsoftware.ftp4j;

import static it.sauronsoftware.ftp4j.FTPClient.TYPE_BINARY;
import static it.sauronsoftware.ftp4j.FTPClient.TYPE_TEXTUAL;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;
import main.java.udt.util.UnifiedSocket;

/**
 *
 * @author asenf
 */
public class DownloadClient implements Runnable {
    private InputStream dataTransferInputStream;
    private final UnifiedSocket dtConnection;
    private final boolean modezEnabled;
    private final FTPDataTransferListener listener;
    private final long offset;
    private final String charset;
    private final OutputStream outputStream;
    private final int SEND_AND_RECEIVE_BUFFER_SIZE;
    private final int tp;
    private final boolean udt;
    private final boolean use_l;
    
    private String output;
    private long xferred;

    public DownloadClient(UnifiedSocket dtConnection,
                                boolean modezEnabled,
                                FTPDataTransferListener listener,
                                long offset,
                                String charset,
                                OutputStream outputStream,
                                int SEND_AND_RECEIVE_BUFFER_SIZE,
                                int tp,
                                boolean udt) {
        this.dtConnection = dtConnection;
        this.modezEnabled = modezEnabled;
        this.listener = listener;
        if (this.listener instanceof MinimalTransferListener)
            this.use_l = false;
        else
            this.use_l = true;
        this.offset = offset;
        this.charset = charset;
        this.outputStream = outputStream;
        this.SEND_AND_RECEIVE_BUFFER_SIZE = SEND_AND_RECEIVE_BUFFER_SIZE;
        this.tp = tp;
        this.udt = udt;
        
        this.output = "";
        this.xferred = 0;
    }
    
    @Override
    public void run() {
            try {
                    long dt_file = System.currentTimeMillis(), dt_upd = System.currentTimeMillis(); // time before upload
                    // Opens the data transfer connection.
                    dataTransferInputStream = dtConnection.getInputStream();
                    // MODE Z enabled?
                    if (modezEnabled) {
                            dataTransferInputStream = new InflaterInputStream(dataTransferInputStream);
                    }
                    // Listeners.
                    if (listener != null) {
                            listener.started();
                            listener.transferred(offset);
                    }
                    // MD5 checksum
                    MessageDigest digest = null; // calculate checksum of incoming data stream
                    try {
                        digest = MessageDigest.getInstance("MD5"); // get the hash algorithm
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    // Let's do it!
                    int l = 0;
                    if (tp == TYPE_TEXTUAL) {
                            Reader reader = new InputStreamReader(
                                            dataTransferInputStream, charset);
                            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream)); // AJS
                            char[] buffer = new char[SEND_AND_RECEIVE_BUFFER_SIZE];
                            while ((l = reader.read(buffer, 0, buffer.length)) != -1) {
                                    writer.write(buffer, 0, l);
                                    writer.flush();
                                    digest.update((new String(buffer)).getBytes(), 0, l);
                                    if (listener != null) {
                                            listener.transferred(l);
                                            if (use_l) {
                                                    dt_upd = System.currentTimeMillis() - dt_upd;
                                                    double rate = (((double)(l)) / (((double)(dt_upd)))) * 1000.0; // bytes/s
                                                    if (rate > 0) {
                                                            String sd = size_display((long)(rate));
                                                            if (sd != null && sd.length() > 0) listener.setText("Speed: " + size_display((long)(rate)) + "/s");
                                                    }
                                                    dt_upd = System.currentTimeMillis();
                                            }
                                    }
                            }
                    } else if (tp == TYPE_BINARY) {
                            byte[] buffer = new byte[SEND_AND_RECEIVE_BUFFER_SIZE];

                            if (!this.udt) { // TCP
                                while ((l = dataTransferInputStream.read(buffer, 0, buffer.length)) != -1) {
                                        outputStream.write(buffer, 0, l);
                                        digest.update(buffer, 0, l); // Calculate MD5 for TCP stream
                                        xferred += l;
                                        //outputStream.flush();
                                        if (listener != null) {
                                                listener.transferred(l);
                                                if (use_l) {
                                                        dt_upd = System.currentTimeMillis() - dt_upd;
                                                        double rate = (((double)(l)) / (((double)(dt_upd)))) * 1000.0; // bytes/s
                                                        if (rate > 0) {
                                                                String sd = size_display((long)(rate));
                                                                if (sd != null && sd.length() > 0) listener.setText("Speed: " + size_display((long)(rate)) + "/s");
                                                        }
                                                        dt_upd = System.currentTimeMillis();
                                                }
                                        }
                                }
                            } else { // UDT
                                buffer = new byte[48 * 65536 - 192];

                                while ( (l = dataTransferInputStream.read(buffer)) > -1 ) {
                                    outputStream.write(buffer, 0, l);
                                    if (l > 0) {
                                            digest.update(buffer, 0, l); // Calculate MD5 for TCP stream
                                            xferred += l;
                                    }
                                    if (listener != null) {
                                            listener.transferred(l);
                                            if (use_l) {
                                                    dt_upd = System.currentTimeMillis() - dt_upd;
                                                    double rate = (((double)(l)) / (((double)(dt_upd)))) * 1000.0; // bytes/s
                                                    if (rate > 0) {
                                                            String sd = size_display((long)(rate));
                                                            if (sd != null && sd.length() > 0) listener.setText("Speed: " + size_display((long)(rate)) + "/s");
                                                    }
                                                    dt_upd = System.currentTimeMillis();
                                            }
                                    }
                                    if (l == 0) try {
                                        Thread.sleep(500); // CPU load reduction (somehow speeds up xfer as well...)
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                                
                                }
                            }

                            // Get a representation of the MD5 digest (checksum)
                            byte[] md5sum = digest.digest();
                            output = "";
                            for (int i_=0; i_ < md5sum.length; i_++)
                                output += Integer.toString( ( md5sum[i_] & 0xff ) + 0x100, 16).substring( 1 );
                    }
                } catch (IOException e) {
                    if (listener != null) {
                            listener.aborted();
                    }
                try {
                    throw new FTPAbortedException();
                } catch (FTPAbortedException ex) {
                    Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
        }
    
        private String size_display(long in) { // expects bytes
            String result = "";
            DecimalFormat df = new DecimalFormat("#,##0.00");

            double in_format = 0;
            if (in < 1024) {
                result = in + " Bytes";
            } else if (in < Math.pow(1024, 2)) {
                in_format = (in/Math.pow(1024, 1));
                //result = new Double(df.format(in_format)).doubleValue() + " KB";
                result = df.format(in_format) + " KB";
            } else if (in < Math.pow(1024, 3)) {
                in_format = (in/Math.pow(1024, 2));
                //result = new Double(df.format(in_format)).doubleValue() + " MB";
                result = df.format(in_format) + " MB";
            } else if (in < Math.pow(1024, 4)) {
                in_format = (in/Math.pow(1024, 3));
                //result = new Double(df.format(in_format)).doubleValue() + " GB";
                result = df.format(in_format) + " GB";
            } else if (in < Math.pow(1024, 5)) {
                in_format = (in/Math.pow(1024, 4));
                //result = new Double(df.format(in_format)).doubleValue() + " TB";
                result = df.format(in_format) + " TB";
            } else if (in < Math.pow(1024, 6)) {
                in_format = (in/Math.pow(1024, 5));
                //result = new Double(df.format(in_format)).doubleValue() + " PB";
                result = df.format(in_format) + " PB";
            }

            return result;
        }

        public String get_md5() {
            return this.output;
        }
        
        public long getXferred() {
            return this.xferred;
        }
}
