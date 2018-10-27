package com.yinxianren.demo.simplemodle;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ReceiverMsg {

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
	// TextMessage message;
	private	TextMessage message = null; 


	private ReceiverMsg init() {

		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
		connectionFactory = new ActiveMQConnectionFactory(DEFAULT_USER,DEFAULT_PASSWORD, "tcp://192.168.43.238:61616");

		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			destination = session.createQueue("demo-first");
			consumer = session.createConsumer(destination);
			int i = 0;
			while (true) {
				//receive()方法是一个主动获取消息的方法，执行一次，拉取一个消息，测试中使用，开放中少用
				message= (TextMessage) consumer.receive();
				if (null != message) {
					i++;
					System.out.println("我是接收者====>>>>收到消息" + i +":"+ message.getText());
				} else {
					break;
				}
				
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}


		} catch (JMSException e) {
			// TODO Auto-generated catch block
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

    new ReceiverMsg().init();

	}

}
