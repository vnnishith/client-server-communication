package com.rvce.CnProject;

import javax.swing.*;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XmlVisitor.TextPredictor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
 
public class ServerBoard extends JFrame {
    private JTextArea messagesArea;
    private JButton sendButton;
    private JButton clearBtn;
    private JTextField message;
    private JTextField number;
    private JButton startServer;
    private TCPServer mServer;
    private JScrollPane messageAreaScrollPane;
 
    public ServerBoard() {
 
        super("ServerBoard");
 
        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));
 
        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2,BoxLayout.X_AXIS));
        
        JPanel panelFields3 = new JPanel();
        panelFields3.setLayout(new BoxLayout(panelFields3, BoxLayout.X_AXIS));
 
        //here we will have the text messages screen
        messagesArea = new JTextArea();
        messagesArea.setColumns(50);
        messagesArea.setRows(20);
        messagesArea.setEditable(false);
        
        messageAreaScrollPane = new JScrollPane (messagesArea, 
        		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
 
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                String numberText = number.getText();
                
                if(verified(messageText, numberText)){
                	String textToSend = "p-start"+numberText+"p-stop"+"txt-start"+messageText+"txt-stop";
                	
                	messagesArea.append("\n" + "sending " + messageText + " to " + numberText);
                	// send the message to the client
                	mServer.sendMessage(textToSend);
                	// clear text
                	message.setText("");
                }
                else{
                	messagesArea.append('\n' + "Failed to send message! make sure fields are not blank !");
                }
            }
        });
 
        
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e){
        		message.setText("");
        		messagesArea.setText("");
        		number.setText("");
        	}
        });
        
        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disable the start button
                startServer.setEnabled(false);
 
                //creates the object OnMessageReceived asked by the TCPServer constructor
                mServer = new TCPServer(new TCPServer.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
                        messagesArea.append("\n "+message);
                    }
                });
                mServer.start();
 
            }
        });
 
        //the box where the user enters the text (EditText is called in Android)
        message = new JTextField();
        message.setSize(200, 20);
        
        
        
        number = new JTextField();
        number.setSize(200, 20);
 
        //add the buttons and the text fields to the panel
        panelFields.add(messageAreaScrollPane);
        panelFields.add(startServer);
 
        panelFields2.add(message);
        panelFields2.add(sendButton);
 
        panelFields3.add(number);
        panelFields3.add(clearBtn);
        
        getContentPane().add(panelFields);
        getContentPane().add(panelFields3);
        getContentPane().add(panelFields2);
 
 
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
 
        setSize(300, 170);
        setVisible(true);
    }
    public void publish(String message){
    	messagesArea.append("\n" + message);
    }
    
    public boolean verified(String message, String number){
    	//verify it is a number and message isnt empty
    	int numCount = 0;
    	int plusCount = 0;
    	if(message.equals("") || number.equals("")) return false;
    	for(char e: number.toCharArray()){
    		if(e == '+') {
    			plusCount++;
    			continue;
    		}
    		if(e < '0' || e > '9') return false;
    		numCount++;
    	}
    	if(plusCount > 1) return false;
    	if(plusCount == 1 && numCount == 12) return true;
    	if(plusCount == 0 && numCount == 10) return true;
    	return false;
    }
}