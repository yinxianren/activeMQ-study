package com.yinxianren.demo.simplemodle;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
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
	private	TextMessage message = null; 



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
			destination = session.createQueue("demo-first");
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
				message = session.createTextMessage(""+System.currentTimeMillis() );
				//producer.send(message);//普通发送
				/*
				 * 不需特殊关注。
				 *我们可以在发送消息时，指定消息的权重，broker可以建议权重较高的消息将会优先发送给Consumer。
				 *在某些场景下，我们通常希望权重较高的消息优先传送；不过因为各种原因，priority并不能决定消息传送的严格顺序(order)。
				 *JMS标准中约定priority可以为0~9的整数数值，值越大表示权重越高，默认值为4。
				 *不过activeMQ中各个存储器对priority的支持并非完全一样。比如JDBC存储器可以支持0~9，
				 *因为JDBC存储器可以基于priority对消息进行排序和索引化；但是对于kahadb/levelDB等这种基于日志文件的存储器而言，
				 *priority支持相对较弱，只能识别三种优先级(LOW: < 4,NORMAL: =4,HIGH: > 4)。
				 *
				 *在broker端，默认是不存储priority信息的，我们需要手动开启，修改activemq.xml配置文件，在broker标签的子标签policyEntries中增加下述配置：
				 *<policyEntry queue=">" prioritizedMessages="true"/>
				 *不过对于“非持久化”类型的消息(如果没有被swap到临时文件)，它们被保存在内存中，它们不存在从文件Paged in到内存的过程，因为可以保证优先级较高的消息，总是在prefetch的时候被优先获取，这也是“非持久化”消息可以担保消息发送顺序的优点
				 */
				producer.send(destination, message, DeliveryMode.PERSISTENT, 1, 0);
				producer.send(destination, message, DeliveryMode.PERSISTENT, 4, 0);
				producer.send(destination, message, DeliveryMode.PERSISTENT, 9, 0);




				System.out.println("发送者======>>>消息已经发生第"+i+"消息了！");
				session.commit();

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
		new SendMsg().init();
	}

}
