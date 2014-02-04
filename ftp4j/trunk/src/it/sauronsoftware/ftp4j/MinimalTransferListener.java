/*
 * ftp4j - A pure Java FTP client library
 * 
 * Copyright (C) 2013+ EMBL/EBI (Alexander Senf)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package it.sauronsoftware.ftp4j;

/**
 *
 * @author asenf
 */
public class MinimalTransferListener implements FTPDataTransferListener {
	
        long total_xfer;
	
	public MinimalTransferListener() {
                this.total_xfer = 0;
	}
	
	@Override
	public void aborted() {
		System.out.println(">aborted!");
	}

	@Override
	public void completed() {
		System.out.println(">completed!");
	}

	@Override
	public void failed() {
		System.out.println(">failed!");
	}

	@Override
	public void started() {
		System.out.println(">started!");
	}

	@Override
	public void transferred(int arg0) {
                this.total_xfer += arg0;
	}
        
	@Override
	public void transferred(long arg0) {
                this.total_xfer += arg0;
	}
        
        @Override
        public void setText(String text) {
		System.out.println(">" + text + "!");
        }
        
        public long get_transferred() {
            return this.total_xfer;
        }
}