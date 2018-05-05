package com.subseekerapp;

import org.jdom2.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubSeekerGUI {

    private JFrame mainFrame;
    private SubSeekerGUI mainGUI = this;
    private JComboBox<String> languageBox;
    private JPanel mainPanel;
    private JButton getMovieButton;
    private JButton searchMovieButton;
    private JButton logOutButton;
    private JLabel chooseLangLabel;
    private JTextField movieNameField;
    private GetMovieListener getMovieListener = new GetMovieListener();
    private SearchMovieListener searchMovieListener = new SearchMovieListener();
    private LogOutListener logOutListener = new LogOutListener();
    private DownloadListener downloadListener = new DownloadListener();
    private SelectSubtitlesWindow selectSubtitlesWindow;

    private String appToken;
    private OpenSubtitlesHasher myHasher = new OpenSubtitlesHasher();
    private MovieData movieData = new MovieData();

    public SubSeekerGUI(){
        mainFrame = new JFrame("SubSeeker");
        mainFrame.setSize(300,200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chooseLangLabel = new JLabel("Choose the language:");
        languageBox = new JComboBox<>();
        languageBox.addItem("eng");
        languageBox.addItem("hun");
        languageBox.addItem("de");


        mainPanel = new JPanel();
        getMovieButton = new JButton("Get Movie");
        searchMovieButton = new JButton("Search Movies");
        searchMovieButton.addActionListener(searchMovieListener);
        getMovieButton.addActionListener(getMovieListener);
        logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(logOutListener);
        movieNameField = new JTextField(10);
        mainPanel.add(chooseLangLabel);
        mainPanel.add(languageBox);
        mainPanel.add(movieNameField);
        mainPanel.add(getMovieButton);
        mainPanel.add(searchMovieButton);
        mainPanel.add(logOutButton);
        mainFrame.setContentPane(mainPanel);
    }

    public SelectSubtitlesWindow getSelectSubtitlesWindow() {
        return selectSubtitlesWindow;
    }

    class LogInDialog extends JDialog{

        private String userName;
        private String password;
        private LogInDialog mer = this;
        private JLabel userNameLabel = new JLabel("Username");
        private JLabel passwordLabel = new JLabel("Password");
        private JTextField userNameField = new JTextField(10);
        private JPasswordField passwordField = new JPasswordField(10);
        private JButton logIn = new JButton("Login");
        private LoginButtonListener loginButtonListener = new LoginButtonListener();

        public LogInDialog(){
            this.setContentPane(new JPanel());
            this.add(userNameLabel);
            this.add(userNameField);
            this.add(passwordLabel);
            this.add(passwordField);
            logIn.addActionListener(loginButtonListener);
            this.add(logIn);
            this.setTitle("OpenSubtitles Login");
            this.setSize(200,170);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }

        public void Login(){
            this.setLocationRelativeTo(mer);
            this.setVisible(true);
        }

        class LoginButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                userName = userNameField.getText();
                password = String.copyValueOf(passwordField.getPassword());
                appToken = MethodCaller.LogIn(userName,password,"en","SubSeeker1");
                mer.dispose();
            }
        }
    }
    //TODO: Fixing up a normal GUI for selection.
    class SelectSubtitlesWindow extends JFrame{

        private ArrayList<MovieBuff> buffArray = new ArrayList<>();
        private JButton downloadButton = new JButton("Download selected");
        private JPanel displayPanel = new JPanel();
        private ScrollPane pane = new ScrollPane();
        private JPanel mainPanel = new JPanel();

        public void go(){
            pane.add(displayPanel);
            mainPanel.add(pane);
            mainPanel.add(downloadButton);
            this.setContentPane(mainPanel);
            this.setVisible(true);
        }
        public SelectSubtitlesWindow(){
            downloadButton.addActionListener(downloadListener);
            displayPanel.setLayout(new BoxLayout(displayPanel,BoxLayout.PAGE_AXIS));
            this.setSize(200,150);
        }
        public void addMovieSelections(String imdbID, List<Element>subFileIDs, List<Element> subFileNames){
            JPanel moviePanel = new JPanel();
            moviePanel.setLayout(new BoxLayout(moviePanel,BoxLayout.LINE_AXIS));
            JLabel idLabel = new JLabel(imdbID);
            moviePanel.add(idLabel);
            for(int i = 0; i < subFileIDs.size(); i++){
                MovieBuff movieSelection = new MovieBuff(imdbID,subFileIDs.get(i).getText(),subFileNames.get(i).getText());
                moviePanel.add(movieSelection.getBuffPanel());
            }
            displayPanel.add(moviePanel);
        }
        class MovieBuff{
            private JPanel buffPanel;
            private JCheckBox isDown;
            private JLabel nameLabel;
            private String subFileID;
            private String imdbID;

            public JPanel getBuffPanel() {
                return buffPanel;
            }

            public JCheckBox getIsDown() {
                return isDown;
            }

            public String getImdbID() {
                return imdbID;
            }

            public MovieBuff(String imdbID, String subFileID, String subFileNames){
                this.subFileID = subFileID;
                this.imdbID = imdbID;
                buffPanel = new JPanel();
                isDown = new JCheckBox();
                nameLabel = new JLabel(subFileNames);
                buffPanel.add(isDown);
                buffPanel.add(nameLabel);
            }
        }
    }

    class GetMovieListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser a = new JFileChooser();
            a.showDialog(new JDialog(),"Select");
            movieNameField.setText(a.getSelectedFile().getName());
            try{
                File curMovieFile = a.getSelectedFile();
                movieData.getMovieAddresses().add(curMovieFile.getAbsolutePath());
                String hash = myHasher.computeHash(curMovieFile);
                long size = curMovieFile.length();
                String language = (String) languageBox.getSelectedItem();
                movieData.getMovieByteSizes().add(size);
                movieData.getMovieHashes().add(hash);
                movieData.getMovieLangs().add(language);
                System.out.println("Movie with hash:" + hash + " and size:" + size + " added!");
            }catch (IOException ex){}
        }
    }

    class SearchMovieListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            selectSubtitlesWindow = new SelectSubtitlesWindow();
            MethodCaller.SearchSubtitles(appToken,movieData,mainGUI);
            movieData.clear();
        }
    }

    class LogOutListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            MethodCaller.LogOut(appToken);
        }
    }

    class DownloadListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            MethodCaller.DownloadSubtitles(appToken,movieData);
        }
    }

    public void go(){
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        LogInDialog logInDialog = new LogInDialog();
        logInDialog.Login();
    }
}

