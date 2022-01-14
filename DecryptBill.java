
/**
 * Write a description of class EncryptBill here.
 *
 * @author name:
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import javax.print.*;





public class DecryptBill {


    private JPanel pnlUserInput;
    private JLabel lbl;
    private JLabel logo;
    private JLabel title;
    private JPanel pnlTitle;

    String filepath = null;
    String password;
    private JPanel pnlButton;
    private JButton btnChooseFile;
    private JButton btnDecrypt;

    private JButton btnPrint;

    public DecryptBill(){
        JFrame frame = new JFrame("Decrypt Bill");
        frame.setLayout(new GridLayout(1, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //---------
        // panel for user input
        createAndShowGUI();
                
        //----------
        frame.getContentPane().setBackground( Color.darkGray );


        // frame.add(pnlUserInput, BorderLayout.NORTH);
        // frame.add(pnlButton, BorderLayout.SOUTH);

        JPanel overall = new JPanel();
        GridLayout layout = new GridLayout(3, 1);
        layout.setHgap(10);
        overall.setLayout(layout);

        ImageIcon icon = new ImageIcon("./Logo4.png"); 
        logo = new JLabel(icon);
        // logo.setPreferredSize(new Dimension(20, 15));
        logo.setHorizontalAlignment(SwingConstants.LEFT); // set the horizontal alignement on the x axis !
        logo.setVerticalAlignment(SwingConstants.TOP);

        frame.add(logo,  BorderLayout.NORTH);
        
        overall.add(pnlTitle);
        overall.add(pnlUserInput);
        overall.add(pnlButton);

        frame.add(overall, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setSize(700,260);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    

    private byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return Files.readAllBytes(file.toPath());
    }

    private Key generateKey(String password) {
        String key = password;
        while (key.length() < 32)
            key += key;
        key = key.substring(0, 32);
        return new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), "AES");
    }
    private void storeFile(byte[] arr, String fileName) throws IOException {
        InputStream is = new ByteArrayInputStream(arr);
        FileOutputStream fos = new FileOutputStream(fileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0)
            fos.write(buffer, 0, length);
        is.close();
        fos.close();
    }

    private byte[] decrypt(byte[] arr, String password) throws InvalidKeyException, IllegalBlockSizeException,BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Key key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(arr);
    }


    
    private void decryptFile(String fileName, String password) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        storeFile(decrypt(readFile(fileName), password), (fileName.split(".crypt"))[0]);
        filepath = fileName.split(".crypt")[0];
    }

    private void createAndShowGUI() {
        pnlUserInput = new JPanel();
        pnlUserInput.setBackground(Color.darkGray);
        btnChooseFile =  new JButton("Choose File");
        lbl = new JLabel("No file Selected");
        lbl.setForeground(Color.white);

        title = new JLabel("Decrypt Bill");
        title.setFont(new Font("Serif", Font.BOLD, 25));
        title.setForeground(Color.white);


        pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.darkGray);
        pnlTitle.add(title, BorderLayout.NORTH);

        pnlUserInput.add(btnChooseFile, BorderLayout.WEST);
        pnlUserInput.add(lbl, BorderLayout.EAST);

        pnlButton = new JPanel();
        pnlButton.setBackground(Color.darkGray);
        btnDecrypt =  new JButton("Decrypt");
        btnPrint =  new JButton("Print");

  
        pnlButton.add(btnDecrypt, BorderLayout.WEST);
        pnlButton.add(btnPrint, BorderLayout.WEST);
               
        lbl.getText();
        
        //---------

        btnChooseFile.addActionListener(new ChooseFileListener()); // add listeners to elements
        btnDecrypt.addActionListener(new DecryptBillListener());
        btnPrint.addActionListener(new PrintBillListener());

    } //createAndShowGUI

    private class ChooseFileListener implements ActionListener {
       
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            // System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            // System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            filepath = chooser.getSelectedFile().getAbsolutePath();
            String fileName = chooser.getSelectedFile().getAbsolutePath().substring(chooser.getSelectedFile().getAbsolutePath().lastIndexOf("\\")+1);
            System.out.println(fileName);
            lbl.setText(fileName);

            // JFrame frame = new JFrame();
            // String result = JOptionPane.showInputDialog(frame, "Enter Decryption Password:");

            JPanel panel = new JPanel();
            JLabel label = new JLabel("Enter Decryption password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "The title",
                                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, options, options[1]);
            if(option == 0) // pressing OK button
            {
                char[] passwords = pass.getPassword();
                // System.out.println("Your password is: " + new String(passwords));
                password = new String(passwords);
            }
            System.out.println(filepath);
            
        } else {
            System.out.println("No Selection ");
        }

      }
    } //

    private class DecryptBillListener implements ActionListener {
       
      public void actionPerformed(ActionEvent e) {
         try{
            decryptFile(filepath, password);
            JOptionPane.showMessageDialog(null, "File Decrypted Successfully", "Decrypt Bill", JOptionPane.INFORMATION_MESSAGE);
         } catch (Exception ex){
            JOptionPane.showMessageDialog(null, ex.toString(), "Decrypt Bill", JOptionPane.INFORMATION_MESSAGE);
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Enter new Decryption password:");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            String[] options = new String[]{"OK", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, panel, "The title",
                                    JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, options, options[1]);
            if(option == 0) // pressing OK button
            {
                char[] passwords = pass.getPassword();
                // System.out.println("Your password is: " + new String(password));
                password = new String(passwords);
            }
         }
      }
    } //

    private class PrintBillListener implements ActionListener  {
       
      public void actionPerformed(ActionEvent e) {
        if(filepath == null){
            System.out.println("Need to Devrypt before Printing");
        }else{
            try{
                printBill();
            }catch(PrintException exp){
                System.out.println("Unable to Print");
            }catch(IOException ioe){
                System.out.println("Cant Open file");
            }
        }
          
      }
    } //

    public void printBill() throws PrintException, IOException {

        System.out.println(filepath);
        new Pdf2Image(filepath);
                
        // FileInputStream in = new FileInputStream(filepath+"0.png");
        // Doc doc = new SimpleDoc(in, DocFlavor.INPUT_STREAM.PNG, null);
        // PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        // try {
        //         service.createPrintJob().print(doc, null);
        // } catch (Throwable e) {
        //         e.printStackTrace();
        // }

    }
    public static void main(String[] args)   {

        new DecryptBill();

    }

}