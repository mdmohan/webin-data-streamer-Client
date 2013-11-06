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

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author asenf
 */
public class TransferListener implements FTPDataTransferListener {
	
	public JProgressBar progress;
	public JLabel status;
        long total, up;
        int scale;
	
	public TransferListener(JLabel status, JProgressBar progress, String size) {
                this.scale = 100000;
		this.status = status;
		this.progress = progress;
		//this.progress.setMaximum(size);
		this.progress.setMaximum(this.scale);
                this.total = Long.parseLong(size);
                this.up = 0;
	}
	
	@Override
	public void aborted() {
		//System.out.println("Uploading aborted!");
		this.status.setText("Uploading aborted!");
	}

	@Override
	public void completed() {
		//System.out.println("Upload completed!");
		this.status.setText("Upload completed!");
	}

	@Override
	public void failed() {
		//System.out.println("Uploading failed!");
		this.status.setText("Upload failed!");
	}

	@Override
	public void started() {
		//System.out.println("Uploading started!");
		//this.status.setText("Download completed!");	
	}

	@Override
	public void transferred(int arg0) {
                this.up += arg0;
                double pct = (double)this.up / (double)this.total;
                int ipct = (int)Math.ceil(pct * this.scale);
                ipct = ipct>this.scale?this.scale:ipct;
		//this.progress.setValue(this.progress.getValue() + arg0);
		this.progress.setValue(ipct);
	}
	@Override
	public void transferred(long arg0) {
                this.up += arg0;
                double pct = (double)this.up / (double)this.total;
                int ipct = (int)Math.ceil(pct * 1000);
                ipct = ipct>1000?1000:ipct;
		//this.progress.setValue(this.progress.getValue() + arg0);
		this.progress.setValue(ipct);
	}
        
        @Override
        public void setText(String text) {
            this.status.setText(text);
        }
}