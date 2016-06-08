/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KByteArrayDataSource implements DataSource {
    private Logger logger = LoggerFactory.getLogger(KByteArrayDataSource.class);

	private byte[] data = null;
	private String contentType = null;
	private String name = null;

	public KByteArrayDataSource(byte[] data, String contentType) {
		this.data = data;
		this.contentType = contentType;
	}

    public KByteArrayDataSource(InputStream in, String contentType) {
		this.contentType = contentType;

		ArrayList<byte[]> buffers = new ArrayList<byte[]>();

        byte[] buffer = null;
		int read = 0;
		int count = 0;
		int size = 4096;

        try {
            if (in != null) {
				int b = in.read();
				while (b != -1) {
					if (count % size == 0) {
						buffer = new byte[size];
						buffers.add(buffer);
						count = 0;
					}
					buffer[count] = (byte)b;
					b = in.read();
					read++;
					count++;
				}
				in.close();
			}

			data = new byte[read];
			int i=0;
			for (byte[] b : buffers) {
				for (int j=0; j<b.length; j++) {
					data[i] = b[j];
					i++;
				}
			}
		} catch (Exception e) {
			logger.debug("Error reading InputStream: ", e);
		}

		logger.debug("getBytes(): read=" + read);
		logger.debug("getBytes(): data.length=" + data.length);
	}

	public KByteArrayDataSource(String data, String contentType) {
		this.data = data.getBytes();
		this.contentType = contentType;
	}

	public byte[] getByteArray() {
		return (data);
	}

	public InputStream getInputStream() throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		return (in);
	}

	// FIXME!
	public OutputStream getOutputStream() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		return (os);
	}

	public String getContentType() {
		return (contentType);
	}

	public String getName() {
		return (name);
	}

	public void setName(String name) {
		this.name = name;
	}
}
