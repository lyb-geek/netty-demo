package com.netty.filetransport;

import java.io.Serializable;
import java.util.Arrays;

public class Req implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6900751129417514123L;
	private String id;
	private String name;
	private String requestMessage;
	private byte[] attachment;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "Req [id=" + id + ", name=" + name + ", requestMessage=" + requestMessage + ", attachment="
				+ Arrays.toString(attachment) + "]";
	}

}
