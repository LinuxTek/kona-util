/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
import java.io.FileNotFoundException;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
*/

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class KFileUtil {
	private static Logger logger = Logger.getLogger(KFileUtil.class);

 
    
	public static byte[] toByteArray(InputStream in) throws IOException {
		int bufferSize = 1024;
		return (toByteArray(in, bufferSize));
	}

	public static byte[] toByteArray(InputStream in, int bufferSize) throws IOException {
		/*
		 * int read = 0; byte[] data = new byte[(int)size];
		 * 
		 * int b = in.read(); while (b != -1) { data[read] = (byte)b; b =
		 * in.read(); read++; }
		 * 
		 * in.close(); return (data);
		 */

		ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);

		// read up to 1k at a time
		byte[] buffer = new byte[bufferSize];

		boolean done = false;
		while (!done) {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1) {
				done = true; // EOF
			} else {
				baos.write(buffer, 0, bytesRead);
			}
		}
		return baos.toByteArray();
	}

	public static byte[] toByteArray(String filePath) throws IOException {
		File file = new File(filePath);
		return toByteArray(file);
	}

	public static byte[] toByteArray(File file) throws IOException {

		FileInputStream in = new FileInputStream(file);
		byte[] data = toByteArray(in);
		in.close();
		return data;

		/*
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream(1024); // read
		 * up to 1k at a time byte[] buffer = new byte[1024];
		 * 
		 * boolean done = false; while (!done) { int bytesRead =
		 * in.read(buffer); if (bytesRead == -1) { done = true; // EOF } else {
		 * baos.write(buffer, 0, bytesRead); } } in.close(); return
		 * baos.toByteArray();
		 */
	}

	public static byte[] toByteArray(URL url) throws IOException {
		InputStream in = url.openStream();
		byte[] data = toByteArray(in);
		in.close();
		return data;
	}

	public static String readerToString(Reader is) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] b = new char[4 * 1024];
		int n = is.read(b);

		// Read a block. If it gets any chars, append them.
		while (n > 0) {
			sb.append(b, 0, n);
			n = is.read(b);
		}

		// Only construct the String object once, here.
		String s = sb.toString();
		// log.debug(this, "file contents:\n\t" + s);

		return (s);
	}

	public static String readFile(Reader reader) throws IOException {
		return readerToString(reader);
	}

	public static String readFile(File f) throws IOException {
		FileReader reader = new FileReader(f);
		return readFile(reader);
	}

	public static byte[] readBinaryFile(File f) throws IOException {
		return toByteArray(f);

	}

	public static String readFile(String filename) throws IOException {
		return readFile(new File(filename));
	}

	public static byte[] readBinaryFile(String filename) throws IOException {
		return readBinaryFile(new File(filename));
	}
    
	public static void appendFile(String filename, String text) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
		out.println(text);
        out.close();
	}
    
	public static void appendFile(File f, String text) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		out.println("the text");
        out.close();
	}

	public static int copyFile(InputStream in, OutputStream out, boolean close) throws IOException {
		int size = 0;
		int b = in.read();

		while (b != -1) {
			size++;
			out.write(b);
			b = in.read();
		}

		in.close();

		if (close) {
			out.close();
		}

		return (size);
	}

	public static long getSize(InputStream in) throws IOException {
		int size = 0;
		int b = in.read();

		while (b != -1) {
			size++;
			b = in.read();
		}

		in.close();
		return (size);
	}

	public static boolean isAncestor(File dir, File file) {
		File parent = file.getParentFile();

		if (parent == null) {
			return (false);
		}

		if (parent.equals(dir)) {
			return (true);
		}

		return (isAncestor(dir, parent));
	}

	public static void stringToFile(String text, String filename) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		out.write(text);
		out.flush();
		out.close();
	}

	public static File writeFile(String filename, String text) throws IOException {
		
		File file = new File(filename);

		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IOException("Unable to create path: "
					+ file.getParentFile());
		}
        
		return writeFile(file, text);
	}
    
	public static File writeFile(File file, String text) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(text);
		out.flush();
		out.close();
        return file;
	}

	public static File writeFile(String filename, byte[] data) throws IOException {
		File f = new File(filename);
		return writeFile(f, data);
	}

	// File object with full path to file
	public static File writeFile(File file, byte[] data) throws IOException {
		File parent = file.getParentFile();
        
        // parent may be null if file passed in is a relative file
		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IOException("Unable to create path: "
					+ file.getParentFile());
		}
        
		OutputStream out = new FileOutputStream(file);
		out.write(data);
		out.close();

		logger.debug("wrote file to: " + file.toString());
        return file;
	}

	public static File writeFile(File file, URL url) throws IOException {
		URLConnection uc = url.openConnection();
		return writeFile(file, toByteArray(uc.getInputStream()));
	}

	public static File writeFile(String filename, URL url) throws IOException {
		File f = new File(filename);
		return writeFile(f, url);
	}

	public static void writeFiles(String directory, Map<String, String> fileMap) throws IOException {
		for (String filename : fileMap.keySet()) {
			String text = fileMap.get(filename);
			filename = directory + "/" + filename;
			logger.debug("writing file: " + filename);
			writeFile(filename, text);
		}
	}

	public static void writeBinaryFiles(String directory,
			Map<String, byte[]> fileMap) throws IOException {
		for (String filename : fileMap.keySet()) {
			byte[] data = fileMap.get(filename);
			filename = directory + "/" + filename;
			logger.debug("writing file: " + filename);
			writeFile(filename, data);
		}
	}

    


	public static File createZipFile(String zipFile, String path) throws IOException {
		ZipOutputStream zout = new ZipOutputStream(
				new FileOutputStream(zipFile));

		File f = new File(path);
		if (f.isDirectory()) {
			addZipDir(zout, path, f);
		} else {
			addZipFile(zout, path, f);
		}

		// Complete the ZIP file
		zout.close();

		f = new File(zipFile);
		return f;
	}

	public static File createZipFile(String zipFile, String[] filenames) throws IOException {
		logger.debug("creating zipfile: " + zipFile);

		// Create the ZIP file
		ZipOutputStream zout = new ZipOutputStream(
				new FileOutputStream(zipFile));

		// Compress the files
		for (int i = 0; i < filenames.length; i++) {
			addZipFile(zout, null, new File(filenames[i]));
		}

		// Complete the ZIP file
		zout.close();

		File f = new File(zipFile);
		return f;
	}

	private static void addZipDir(ZipOutputStream zout, String path, File dir) throws IOException {
		// get sub-folder/files list
		File[] files = dir.listFiles();

		logger.debug("Adding directory: " + dir.getName());

		for (int i = 0; i < files.length; i++) {
			// if the file is directory, call the function recursively
			if (files[i].isDirectory()) {
				addZipDir(zout, path, files[i]);
				continue;
			}

			/*
			 * we are here means, its file and not directory, so add it to the
			 * zip file
			 */
			addZipFile(zout, path, files[i]);
		}
	}

	/*
	 * Add a file to a Zip stream.
	 * 
	 * @param zout zip stream
	 * 
	 * @param relativePath files added will be relative to this path. Set this
	 * to null to have the absolute path of the file added to the stream.
	 * 
	 * @file the file to add to the stream.
	 */
	private static void addZipFile(ZipOutputStream zout, String relativePath,
			File file) throws IOException {
		logger.debug("Adding file: " + file.getName());

		// create byte buffer
		byte[] buffer = new byte[1024];

		// create object of FileInputStream
		FileInputStream fin = new FileInputStream(file);

		String filename = file.getAbsolutePath();

		logger.debug("addZipFile:" + "\nfilename: " + filename
				+ "\nrelative path: " + relativePath);

		if (relativePath != null) {
			filename = filename.replaceFirst(Pattern.quote(relativePath), "");
		}

		if (filename.startsWith("/")) {
			filename = filename.substring(1);
		}

		logger.debug("new filename:" + filename);

		zout.putNextEntry(new ZipEntry(filename));

		/*
		 * After creating entry in the zip file, actually write the file.
		 */
		int length;
		while ((length = fin.read(buffer)) > 0) {
			zout.write(buffer, 0, length);
		}

		/*
		 * After writing the file to ZipOutputStream, use
		 * 
		 * void closeEntry() method of ZipOutputStream class to close the
		 * current entry and position the stream to write the next entry.
		 */

		zout.closeEntry();

		// close the InputStream
		fin.close();
	}

	public static byte[] getUrlContents(String url) throws IOException {
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		return toByteArray(uc.getInputStream());
	}
    
	public static String getUrlContentsAsString(String url) throws IOException {
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		return IOUtils.toString(uc.getInputStream(), "UTF-8");
	}
	
	public static String getTempFileName() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (tmpDir == null) {
			tmpDir = "/tmp";
		}
		String uuid = UUID.randomUUID().toString();
		File f = new File(tmpDir, uuid);
		return f.getAbsolutePath();
	}
    
	public static File createTempFile() throws IOException {
		return createTempFile(null);
	}
	
	public static File createTempFile(String extension) throws IOException {
        Date now = new Date();
        String prefix = new Long(now.getTime()).toString();
        String suffix = ".tmp";
        if (extension != null) {
        	suffix = extension;
        }
        File f = File.createTempFile(prefix, suffix);
		f.deleteOnExit();
        return f;
	}
    
	public static File writeTempFile(String s) throws IOException {
		return writeTempFile(s, null);
	}
	
	public static File writeTempFile(String s, String extension) throws IOException {
        File f = createTempFile(extension);
        writeFile(f, s);
        logger.debug("writeTempFile: " + f.getAbsolutePath());
        return f;
	}
    
	public static File writeTempFile(byte[] b) throws IOException {
		return writeTempFile(b, null);
	}
	
	public static File writeTempFile(byte[] b, String extension) throws IOException {
        File f = createTempFile(extension);
        writeFile(f, b);
        logger.debug("writeTempFile: " + f.getAbsolutePath());
        return f;
	}
    
    
	public static File urlToTempFile(String url, String extension) throws IOException {
        byte[] b = getUrlContents(url);
        return writeTempFile(b, extension);
	}
}