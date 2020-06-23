package com.project.net.client;

import javax.swing.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.debug.JOptionPaneTest;
import com.project.net.connect.*;
import com.project.net.packet.ToPacket;
import com.project.protocol.MHAP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;


public class Client extends JFrame implements ConnectionListener {

    private static final long serialVersionUID = 1L;

    private JPanel connectionContents;
    private JPanel contents;
    private JPanel workBench;

    private JButton btnInput1;
    private JButton btnInput;
    private ImageIcon icon;


    private JTextArea hello;

    private CardLayout cl;

    private Connection connection;
    private ToPacket packet = new ToPacket();
    private MHAP mhap = new MHAP();

    private static final String ipAddress = "localhost";
    private static final int port = 8000;
    private final JTextArea log = new JTextArea();

    private final  String   TITLE_message = "Окно сообщения";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });

    }
    private Client ()
    {
        super("Протокл аутентификации MHAP");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        
        UIManager.put("OptionPane.yesButtonText"   , "Да"    );
        UIManager.put("OptionPane.noButtonText"    , "Нет"   );
        UIManager.put("OptionPane.cancelButtonText", "Отмена");


        cl = new CardLayout();
        workBench = new JPanel(cl);


        setContentPane(addConnectionContent());
       
        setSize(400, 500);
        try {
            connection= new Connection(this,ipAddress,port);
        } catch (IOException e) {
            System.out.println("Connection exception: " + e);
        }
        setResizable(false);
        setVisible(true);

    }

    private JPanel addConnectionContent(){
        connectionContents = new JPanel();

        connectionContents.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
       
        icon = new ImageIcon("images/warning.png");

        hello = new JTextArea(3,21);
        hello.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        hello.setBackground(new Color(0,0,0,0));
        hello.append("             Добро пожаловать! \n");
        hello.append("Для продолжения нажмите на кнопку");
        hello.setLineWrap(true);
        hello.setWrapStyleWord(true);
        hello.setEditable(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 1;
        connectionContents.add(hello, c);

        btnInput1 = new JButton("Подключение");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(5,110 , 0, 0);
        btnInput1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String msg;
                    msg = packet.Convert(mhap.CreatePacket());
                    connection.sendString(msg);
                    System.out.println(msg);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        connectionContents.add(btnInput1,c);
        //addInputListeners();
        return connectionContents;

    }

    private JPanel addWorkPan(){
        contents = new JPanel();

        contents.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();




        btnInput = new JButton("Подключение1");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;

        contents.add(btnInput,c);

        return contents;
        //contents.add(log, BorderLayout.SOUTH);

        //fieldInput.addActionListener();
        // contents.add(fieldName);

    }

    private void addMessageListeners()
    {
            JOptionPane.showMessageDialog(Client.this,
                    "<html><h2>Вы не зарегестрированы в системе</h2><i>Вадим Владленович<i>", TITLE_message,
                    JOptionPane.INFORMATION_MESSAGE, icon);
        System.exit(0);
    }

    private void addPassManager(){

            String result = JOptionPane.showInputDialog(
                Client.this,
                "<html><h2>Введите пароль");


            mhap.SetData(result);
            if(result.isEmpty()){
                JOptionPane.showMessageDialog(Client.this,
                        "<html><h2>Введите пароль", TITLE_message,
                        JOptionPane.INFORMATION_MESSAGE, icon);
                System.exit(0);
            }
            try {
                String msg;
                msg = packet.Convert(mhap.CreatePacket());
                connection.sendString(msg);
                System.out.println(msg);
               


            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

    }
    private void addMessageProcessing(boolean status){
        if(status==true){
            JOptionPane.showMessageDialog(Client.this,
                    "<html><h2>Добро пожаловать</h2><i>Вадим Владленович</i>");
        }else{
            JOptionPane.showMessageDialog(Client.this,
                    "<html><h2>Вы ввели неверный пароль</h2><i>Вадим Владленович<i>", TITLE_message,
                    JOptionPane.INFORMATION_MESSAGE, icon);
            System.exit(0);
        }
    }


    @Override
    public void onConnectionReady(Connection connection) {
        System.out.println("Connection ready...");
    }

    @Override
    public void onException(Connection connection, Exception e) {
        System.out.println("Connection exception: " + e);
    }

    @Override
    public void onDisconnect(Connection connection) {
        System.out.println("Connection close");
    }

    @Override
    public void onReceiveString(Connection connection, String value) {
        System.out.println(value);
        try {
            packet = new ObjectMapper()
                    .readerFor(ToPacket.class)
                    .readValue(value);

            mhap = new MHAP(packet.GetCode(),packet.GetID(),packet.GetLength(),packet.GetDate());

            if (mhap.GetStatus()==false){
                addMessageListeners();
            }else if (mhap.GetPacketID()==1){
                addPassManager();
            }else if(mhap.GetPacketID()==3){
                addMessageProcessing(mhap.GetStatus());
            }
        } catch (JsonProcessingException e) {
            System.out.println("erros to JsonFile);
        }
    }
}













