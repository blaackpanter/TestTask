import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Consumer extends JFrame {

    private static SQLConnection sqlConnection;
    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;
    private static JButton buttonReceive;
    private static JButton buttonShowAll;
    private static JTextArea msgFromPbl;
    private static JTextArea msgFromDB;

    private static final String QUEUE_NAME = "information";
    private static final String EXCHANGE_NAME = "topic_logs";

    private Consumer() {
        super("Consumer");
        buttonReceive = new JButton("Receive");
        msgFromPbl = new JTextArea("message from publisher");
        buttonShowAll = new JButton("Show all messages");
        msgFromDB = new JTextArea("all messages from database");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(buttonReceive);
        panel.add(msgFromPbl);
        panel.add(buttonShowAll);
        panel.add(msgFromDB);
        setContentPane(panel);
        setSize(900, 200);
    }

    public static void main(String[] argv) throws Exception {
        JFrame myWindow = new Consumer();
        myWindow.setVisible(true);

        sqlConnection = new SQLConnection();

        factory = new ConnectionFactory();
        factory.setHost("localhost");
        setConnect();
        buttonReceive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeliverCallback deliverCallback = ((consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    msgFromPbl.setText(message);
                    System.out.println(message);
                    try {
                        sqlConnection.addMessage(message);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
                try {
                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        buttonShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    msgFromDB.setText(sqlConnection.showAllMessages());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void setConnect() {
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            String bindingKey = "#.info";

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, bindingKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
