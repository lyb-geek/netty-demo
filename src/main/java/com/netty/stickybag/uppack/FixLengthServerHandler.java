package com.netty.stickybag.uppack;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class FixLengthServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String clientContent = (String) msg;
		System.out.println("服务端接收到来自客户端的消息：" + clientContent);

		String response = "heyhi";

		ctx.writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println(cause);
		ctx.close();
	}

}
