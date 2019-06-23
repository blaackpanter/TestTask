import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publisher extends JFrame {

    private static ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;
    private static JTextField field;
    private static JButton send;
    private static String message;

    private Publisher() {
        super("Publisher");
        send = new JButton("Send");
        field = new JTextField(30);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(send);
        panel.add(field);
        setContentPane(panel);
        setSize(400, 100);

    }

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        JFrame publisherWindow = new Publisher();
        publisherWindow.setVisible(true);

        factory = new ConnectionFactory();
        factory.setHost("localhost");
        setConnect();
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = field.getText();
                field.setText("");
                String routingKey = "message.info";
                try {
                    channel.basicPublish(EXCHANGE_NAME, routingKey,
                            null,
                            message.getBytes("UTF-8"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void setConnect() {
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //By default, RabbitMQ will send each message to the next consumer, in sequence. On average every consumer will get the same number of messages. This way of distributing messages is called round-robin. Try this out with three or more workers.
}
