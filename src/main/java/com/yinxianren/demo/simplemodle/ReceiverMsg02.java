package com.yinxianren.demo.simplemodle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ReceiverMsg02 {

	private static final String DEFAULT_USER="guest";
	private static final String DEFAULT_PASSWORD="guest";

	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	private ConnectionFactory connectionFactory=null; // Connection ：JMS 客户端到JMS
	// Provider 的连接
	private Connection connection = null;
	// Session： 一个发送或接收消息的线程
	private Session session= null;
	// Destination ：消息的目的地;消息发送给谁.
	private Destination destination= null; 
	// MessageProducer：消息发送者
	private MessageConsumer consumer= null; 

	private	TextMessage message = null; 


	private ReceiverMsg02 init() {

		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
		connectionFactory = new ActiveMQConnectionFactory(DEFAULT_USER,DEFAULT_PASSWORD, "tcp://192.168.43.238:61616");

		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接   CLIENT_ACKNOWLEDGE手动处理模式
			session = connection.createSession(false,Session.CLIENT_ACKNOWLEDGE);
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			destination = session.createQueue("demo-first");
			consumer = session.createConsumer(destination);
			//设置监听器，注册成功后，队列中的消息变化会自动触发监听器的代码，接受消息并处理
			/*
			 * 监听器一旦注册，永久有效
			 * 永久--comsumer线程不关闭
			 * 处理消息的方式：只要有消息未处理，自动调用onMessage方法，处理消息
			 * 监听器可以注册若干个，注册多个监听器，相当于集群
			 * activemq自动循环调用多个监听器，处理队列中的消息，实现并行处理；
			 * @param msg :未处理的消息
			 */

//			consumer.setMessageListener(new MessageListener() {
//				@Override
//				public void onMessage(Message msg) {
//					try {
//						//却方法，代表consumenr已经收到消息，确定后，mq删除对应的消息
//						msg.acknowledge();
//						message=(TextMessage)msg;
//						System.out.println("接受者接受到："+message.getText());
//					} catch (JMSException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			});
//			
			consumer.setMessageListener((msg)->{
				try {
					//却方法，代表consumenr已经收到消息，确定后，mq删除对应的消息
					msg.acknowledge();
					message=(TextMessage)msg;
					System.out.println("接受者接受到："+message.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
			//阻塞当前代码，保证listener代码未结束，如果代码结束，监听器自动关闭
			System.in.read();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (null != connection)
					connection.close();
			} catch (Throwable ignore) {
			}
		}
		return this;
	}


	public static void main(String[] args) {

		new ReceiverMsg02().init();

	}

}
