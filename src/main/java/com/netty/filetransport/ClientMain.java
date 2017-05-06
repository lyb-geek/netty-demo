package com.netty.filetransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.netty.util.GzipUtils;

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

			req.setAttachment(getFileData());

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

	private static byte[] getFileData() {
		byte[] ret = null;
		try {
			// 读取文件
			String readPath = System.getProperty("user.dir") + File.separatorChar + "sources" + File.separatorChar
					+ "001.jpg";
			File file = new File(readPath);
			FileInputStream in = new FileInputStream(file);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();

			ret = GzipUtils.gzip(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

}
