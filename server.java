import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EventObject;

public class server {
    static ArrayList<Myfile> myfiles = new ArrayList<>();

    public static void main(String[] args) {
       // int field = 0;

        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane JScrollPane = new JScrollPane(jPanel);
        JScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // ScrollPanelConstant

        JLabel jlTitle = new JLabel("File Reciver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle);
        jFrame.add(JScrollPane);
        jFrame.setVisible(true);

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(1234);
            while (true) {

                try {
    
                    Socket socket = serverSocket.accept();
    
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    int fileNameLength = dataInputStream.readInt();
    
                    if (fileNameLength > 0) {
                        byte[] fileNameBytes = new byte[fileNameLength];
                        dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                        String fileName = new String(fileNameBytes);
    
                        int fileContentLength = dataInputStream.readInt();
    
                        if (fileContentLength > 0) {
                            byte[] fileContentBytes = new byte[fileContentLength];
                            dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
    
                            JPanel jpFileRow = new JPanel();
                            jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));
    
                            JLabel jlFileName = new JLabel(fileName);
                            jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                            jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                            /* char */
                            // char[] fileId;
                            int fileId=0;
                            if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                                jpFileRow.setName(String.valueOf(fileId));
                                jpFileRow.addMouseListener(getMyMouseListener());
    
                                jpFileRow.add(jlFileName);
                                jPanel.add(jpFileRow);
                                jFrame.validate();
                            } else {
                                jpFileRow.setName(String.valueOf(fileId));
                                jpFileRow.addMouseListener(getMyMouseListener());
    
                                jpFileRow.add(jlFileName);
                                jPanel.add(jpFileRow);
    
                                jFrame.validate();
                            }
    
                            myfiles.add(new Myfile(fileId , fileName ,  fileContentBytes, getFileExtension(fileName)));
                        }
    
                    }
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
        
        catch (IOException error) {
            
            error.printStackTrace();
        }
    

        
        

    }

    public static MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                JPanel jPanel = (JPanel) ((EventObject) e).getSource();

                int fileId = Integer.parseInt(jPanel.getName());
                for (Myfile myFile : myfiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
        JFrame jFrame = new JFrame("File Downloader");
        jFrame.setSize(400, 400);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel jlTitle = new JLabel("File Downloade");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel jlPrompt = new JLabel("Want to Download" + fileName);
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        if (fileExtension.equalsIgnoreCase("txt")) {
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToDownload = new File(fileName);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

                    fileOutputStream.write(fileData);
                    fileOutputStream.close();

                    jFrame.dispose();

                } catch (IOException error) {
                    error.printStackTrace();

                }

            }
        });

        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });
        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        jFrame.add(jPanel);

        return jFrame;

    }

    public static String getFileExtension(String fileName) {
        // Not work with .tar.gz
        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "NO Extension found";
        }

    }
}