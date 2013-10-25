package com.mixshare.rapid_evolution.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public abstract class CommandClient {

    static private Logger log = Logger.getLogger(CommandClient.class);
    abstract protected Logger getLogger();
    
    // constants
    static private String mixshare_url = "mixshare.dyndns.org";
    static private int timeout = 60000;

    // i/o variables
    private Socket socket = null;
    private BufferedReader buffered_reader = null;
    private PrintStream print_stream = null;

    public CommandClient(int port) {
        try {
            socket = new Socket(mixshare_url, port);
            socket.setSoTimeout(timeout);            
            OutputStream output_stream = socket.getOutputStream();
            InputStream input_stream = socket.getInputStream();
            print_stream = new PrintStream(output_stream);
            buffered_reader = new BufferedReader(new InputStreamReader(input_stream));            
        } catch (Exception e) {
            log.error("CommandClient(): error Exception", e);
        }        
    }
    
    public void close() {
        try {
            if (buffered_reader != null) buffered_reader.close();
            if (print_stream != null) print_stream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            log.error("close(): error Exception", e);
        }
    }

    public boolean isConnected() {
        if (socket != null)
            return socket.isConnected();
        return false;
    }
        
    // sends just a string, usually used to piece together an entire line
    public void sendString(String str) {
        if (log.isTraceEnabled()) log.trace("sendString(): sending string: " + str);
        print_stream.print(str);
    }

    // sends an entire line (string + end of line marker)
    public void sendLine(String str) {
        if (log.isTraceEnabled()) log.trace("sendLine(): sending line: " + str);
        print_stream.println(str);
    }

    // sends a line which spans multiple lines
    public void sendComments(String comments) {
        StringTokenizer comment_tokens = new StringTokenizer(comments, "\n");
        int num_comment_lines = comment_tokens.countTokens();
        if (num_comment_lines < 1) {
            sendLine("1");
            sendLine("");
        } else {
            sendLine(String.valueOf(num_comment_lines));
            while (comment_tokens.hasMoreTokens()) {
                String comment_token = comment_tokens.nextToken();
                sendLine(comment_token);
            }
        }        
    }
    
    // receives an entire line, null if it does not receive one
    public String receiveLine() {
        String return_val = null;
        try {
            return_val = buffered_reader.readLine();        
        } catch (java.net.SocketTimeoutException ste) {
            log.warn("receiveLine(): socket timeout receiving line");
        } catch (java.net.SocketException e) {
            log.warn("receiveLine(): socket Exception: " + e);
        } catch (java.io.IOException e) {
            log.warn("receiveLine(): error Exception", e);
        }
        if (log.isTraceEnabled()) log.trace("receiveLine(): value=" + return_val);
        return return_val;
    }

}
