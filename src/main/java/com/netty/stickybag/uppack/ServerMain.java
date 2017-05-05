package com.netty.stickybag.uppack;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 
 * <p>
 * Title: ServerMain
 * </p>
 * <p>
 * Description: 拆包粘包解决，第一种按分隔符拆分
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年5月5日
 */
public class ServerMain {
	public static void main(String[] args) {
		EventLoopGroup bossLoop = new NioEventLoopGroup();
		EventLoopGroup workerLoop = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossLoop, workerLoop).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						// 客户端消息按|进行分割
						ByteBuf delimiter = Unpooled.copiedBuffer("|".getBytes());
						channel.pipeline()
								// 按分隔符拆分，解决粘包拆包问题
								.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter))
								.addLast("stringDecoder", new StringDecoder(Charset.forName("UTF-8")))
								.addLast("stringEncoder", new StringEncoder(Charset.forName("UTF-8")))
								.addLast(new ServerHandler());

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
