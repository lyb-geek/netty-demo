package com.netty.serial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMain {
	public static void main(String[] args) {
		EventLoopGroup loopGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(loopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				channel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
						.addLast(MarshallingCodeCFactory.buildMarshallingDecoder()).addLast(new ClientHandler());

			}
		}).option(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture future = bootstrap.connect("localhost", 8080).sync();

			Req req = new Req();
			req.setId("1");
			req.setName("请求内容");
			req.setRequestMessage("我是客户端，请求内容");

			future.channel().writeAndFlush(req);

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
