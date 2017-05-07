package com.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerMain {
	public static void main(String[] args) {
		EventLoopGroup bossLoop = new NioEventLoopGroup();
		EventLoopGroup workerLoop = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossLoop, workerLoop).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline().
						// 就是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
						addLast(new HttpObjectAggregator(66335))
								// 主要用于处理大数据流,
								.addLast(new ChunkedWriteHandler()).
						// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
						addLast(new HttpResponseEncoder()).
						// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
						addLast(new HttpRequestDecoder()).addLast(new ServerHandler());

					}

				}).option(ChannelOption.SO_BACKLOG, 120).childOption(ChannelOption.SO_KEEPALIVE, true);

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
