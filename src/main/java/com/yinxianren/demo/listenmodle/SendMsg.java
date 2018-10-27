package com.yinxianren.demo.listenmodle;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.util.TimeUtils;

public class SendMsg {

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
	private MessageProducer producer= null; 
	// TextMessage message;
	private	ObjectMessage message = null; 
	
	
	
	private  SendMsg init() {

		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar,默认端口号是61616，从activeMQde conf/activemq/activemq.xml配置文件查看
		connectionFactory = new ActiveMQConnectionFactory(DEFAULT_USER,DEFAULT_PASSWORD, "tcp://192.168.43.238:61616");
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE, Session.CLIENT_ACKNOWLEDGE);
			// 创建目录，参数是目的地的名称，是目的地的唯一标记
			destination = session.createQueue("demo-listening");
			// 得到消息生成者【发送者】
			producer = session.createProducer(destination);
			/*  设置不持久化，此处学习，实际根据项目决定
			 *  static final int NON_PERSISTENT = 1;
			 *  static final int PERSISTENT = 2;
			 */
//			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			int i=0;
		    while(true) {
		    	i++;
		    	// 构造消息，此处写死，项目就是参数，或者方法获取
				message = session.createObjectMessage(""+System.currentTimeMillis() );
				producer.send(message);
				System.out.println("发送者======>>>消息已经发生第"+i+"消息了！");
				session.commit();
				
				try {
					TimeUnit.SECONDS.sleep(5);
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
		new SendMsg().init();
	}

}
