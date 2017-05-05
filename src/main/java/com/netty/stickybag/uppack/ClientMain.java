package com.netty.stickybag.uppack;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ClientMain {
	public static void main(String[] args) {
		EventLoopGroup loopGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(loopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				// 服务端响应消息按|进行分割
				ByteBuf delimiter = Unpooled.copiedBuffer("|".getBytes());
				channel.pipeline()
						// 按分隔符拆分，解决粘包拆包问题
						.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter))
						.addLast("stringDecoder", new StringDecoder(Charset.forName("UTF-8")))
						.addLast("stringEncoder", new StringEncoder(Charset.forName("UTF-8")))
						.addLast(new ClientHandler());

			}
		}).option(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture future = bootstrap.connect("localhost", 8080).sync();

			future.channel().writeAndFlush("我是客户端,我请求消息1|");

			future.channel().writeAndFlush("我是客户端,我请求消息2|");

			future.channel().writeAndFlush("我是客户端,我请求消息3|");

			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (loopGroup != null) {
				loopGroup.shutdownGracefully();
			}
		}
	}

}
