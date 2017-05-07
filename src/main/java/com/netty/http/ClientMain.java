package com.netty.http;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

public class ClientMain {
	public static void main(String[] args) {
		EventLoopGroup loopGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(loopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				channel.pipeline().// 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
				addLast(new HttpResponseDecoder()).
				// 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
				addLast(new HttpRequestEncoder()).addLast(new ClientHandler());

			}
		}).option(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture future = bootstrap.connect("localhost", 8080).sync();

			URI uri = new URI("http://localhost:8080");
			String msg = "Are you ok?";
			DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
					uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));

			// 构建http请求
			request.headers().set(HttpHeaderNames.HOST, "localhost");
			request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

			// 发送http请求
			future.channel().writeAndFlush(request);

			future.channel().closeFuture().sync();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (loopGroup != null) {
				loopGroup.shutdownGracefully();
			}
		}
	}

}
