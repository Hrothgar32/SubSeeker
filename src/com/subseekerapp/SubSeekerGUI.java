package com.subseekerapp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SubSeekerGUI {

    private OpenSubtitlesHasher myHasher = new OpenSubtitlesHasher();
    private MethodCaller methodCaller;
    private MethodResponseReceiver methodResponseReceiver;
    private JFrame mainFrame;
    private JComboBox<String> languageBox;
    private JPanel mainPanel;
    private ImageIcon opensub;
    private JButton getMovieButton;
    private JLabel chooseLangLabel;
    private JTextField movieNameField;
    private GetMovieListener getMovieListener = new GetMovieListener();

    public SubSeekerGUI(){
        mainFrame = new JFrame("SubSeeker");
        mainFrame.setSize(300,200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chooseLangLabel = new JLabel("Choose the language:");
        languageBox = new JComboBox<>();


        mainPanel = new JPanel();
        getMovieButton = new JButton("Get Movie");
        getMovieButton.addActionListener(getMovieListener);
        movieNameField = new JTextField(10);
        mainPanel.add(chooseLangLabel);
        mainPanel.add(languageBox);
        mainPanel.add(movieNameField);
        mainPanel.add(getMovieButton);
        mainFrame.setContentPane(mainPanel);
    }

    class GetMovieListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser a = new JFileChooser();
            a.showDialog(new JDialog(),"Select");
            movieNameField.setText(a.getSelectedFile().getName());
            try{
                System.out.println(myHasher.computeHash(a.getSelectedFile()));
            }catch (IOException ex){}
        }
    }

    public void go(){
        mainFrame.setVisible(true);
    }
}

