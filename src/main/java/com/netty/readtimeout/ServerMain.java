package com.netty.readtimeout;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 
 * <p>
 * Title: ServerMain
 * </p>
 * <p>
 * Description: 利用jboss.marshalling实现对象编解码
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年5月6日
 */
public class ServerMain {
	public static void main(String[] args) {
		EventLoopGroup bossLoop = new NioEventLoopGroup();
		EventLoopGroup workerLoop = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossLoop, workerLoop).channel(NioServerSocketChannel.class)
				// 设置日志
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
								.addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
								.addLast(new ReadTimeoutHandler(5)).addLast(new ServerHandler());

					}

				}).option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture future = bootstrap.bind(8080).sync();
			System.out.println("server start ...");
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bossLoop != null) {
				bossLoop.shutdownGracefully();
			}

			if (workerLoop != null) {
				workerLoop.shutdownGracefully();
			}
		}
	}

}
