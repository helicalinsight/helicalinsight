package com.helicalinsight.efw.utility;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import net.sf.json.JSONObject;

public class FileCopierOverNetwork {
	private static final Logger logger = LoggerFactory.getLogger(FileCopierOverNetwork.class);
	final static SftpProgressMonitor monitor = new SftpProgressMonitor() {
		public void init(final int op, final String source, final String target, final long max) {
			logger.info("sftp start uploading file from:" + source + " to:" + target);
		}

		public boolean count(final long count) {
			logger.debug("sftp sending bytes: " + count);
			return true;
		}

		public void end() {
			logger.info("sftp uploading is done.");
		}
	};

	public static void transferFileToUnix(JSONObject parameters) {
		String hostname = parameters.getString("hostname");
		String username = parameters.getString("username");
		String password = parameters.getString("password");
		String fromFilePath = parameters.getString("filePath");
		String destinationFilePath = parameters.getString("linDestination");
		File fromFile = new File(fromFilePath);
		String copyWinFrom = fromFile.getAbsolutePath();
		File toFile = new File(destinationFilePath);
		// String copyWinFrom = "d:/fromWindows.del";
		// String copyLinTo = "/home/thin/ibrahim-scripts/fromWindows.del";
		String copyLinTo = toFile.getAbsolutePath();

		try {
			putFile(hostname, username, password, copyWinFrom, copyLinTo);
			// getFile(hostname, username, password, copyLinuxFrom,
			// copyLinuxTo);
		} catch (JSchException e1) {
			e1.printStackTrace();
		} catch (SftpException e1) {
			e1.printStackTrace();
		}
		logger.info("Done !!");
	}

	public static void putFile(String hostname, String username, String password, String copyFrom, String copyTo)
			throws JSchException, SftpException {
		logger.info("Initiate sending file to Linux Server...");
		JSch jsch = new JSch();
		Session session = null;
		logger.info("Trying to connect.....");
		session = jsch.getSession(username, hostname, 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();
		logger.info("is server connected? " + session.isConnected());

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		logger.info("Server's home directory: " + sftpChannel.getHome());
		try {
			sftpChannel.put(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			logger.error("file was not found: " + copyFrom);
		}

		sftpChannel.exit();
		session.disconnect();
		logger.info("Finished sending file to Linux Server...");
	}

	public static void getFile(String hostname, String username, String password, String copyFrom, String copyTo)
			throws JSchException {
		logger.info("Initiate getting file from Linux Server...");
		JSch jsch = new JSch();
		Session session = null;
		logger.info("Trying to connect.....");
		session = jsch.getSession(username, hostname, 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();
		logger.info("is server connected? " + session.isConnected());

		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		try {
			logger.info(sftpChannel.getHome());
		} catch (SftpException e1) {
			e1.printStackTrace();
		}
		try {
			sftpChannel.get(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			logger.error("file was not found: " + copyFrom);
		}

		sftpChannel.exit();
		session.disconnect();
		logger.info("Finished getting file from Linux Server...");
	}

}
