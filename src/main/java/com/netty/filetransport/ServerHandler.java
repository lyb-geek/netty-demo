package com.netty.filetransport;

import java.io.File;
import java.io.FileOutputStream;

import com.netty.util.GzipUtils;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Req clientContent = (Req) msg;
		System.out.println("服务端接收到来自客户端的消息：" + clientContent);

		Resp response = new Resp();
		response.setId("1");
		response.setName("响应内容");
		response.setResponseMessage("我是服务端，我已经接收到消息");

		writeFile(clientContent.getAttachment());

		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(cause);
		ctx.close();
	}

	private void writeFile(byte[] data) {
		try {
			// 写出文件
			String writePath = System.getProperty("user.dir") + File.separatorChar + "receive" + File.separatorChar
					+ "001.jpg";
			System.out.println(writePath);
			FileOutputStream fos = new FileOutputStream(writePath);
			GzipUtils.ungzip(data);
			fos.write(data);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
