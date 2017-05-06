package com.netty.serial;

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

		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(cause);
		ctx.close();
	}

}
