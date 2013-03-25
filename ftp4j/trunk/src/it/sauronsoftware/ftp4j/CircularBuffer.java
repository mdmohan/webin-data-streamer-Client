/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.sauronsoftware.ftp4j;

/**
 *
 * @author asenf
 */
public class CircularBuffer {
    private byte[] buffer;
    private int size, start, end;
    private int capacity = 192 * 1024; // 192K
    
    CircularBuffer() {
        buffer = new byte[capacity];
        start = 0;
        end = 0;
        size = 0;        
    }
    CircularBuffer(int InitialSize) {
        capacity = InitialSize;
        buffer = new byte[capacity];
        start = 0;
        end = 0;
        size = 0;        
    }
    
    public synchronized void put(byte[] data) { // Add data "at the end"
        int delta = capacity - end;
        if (delta >= data.length) {
            System.arraycopy(data, 0, buffer, end, data.length);
            end = end + data.length;
        } else {
            System.arraycopy(data, 0, buffer, end, delta);
            int remainder = data.length - delta;
            System.arraycopy(data, delta, buffer, 0, remainder);
            end = remainder;
        }
    }
    
    public synchronized byte[] get(int length) {
        byte[] result = new byte[length];
        
        int delta = capacity - start;
        if (delta >= length) {
            System.arraycopy(buffer, start, result, 0, length);
            start = start + length;
        } else {
            System.arraycopy(buffer, start, result, 0, delta);
            int remainder = length - delta;
            System.arraycopy(buffer, 0, result, delta, remainder);
            start = remainder;
        }
        
        return result;
    }
    
    public synchronized void get(byte[] result) {
        int length = result.length;
        
        int delta = capacity - start;
        if (delta >= length) {
            System.arraycopy(buffer, start, result, 0, length);
            start = start + length;
        } else {
            System.arraycopy(buffer, start, result, 0, delta);
            int remainder = length - delta;
            System.arraycopy(buffer, 0, result, delta, remainder);
            start = remainder;
        }
    }

    public int getSize() {
        int s = end - start;
        if (s<0)
            s += capacity;
        
        return s;
    }
}
