
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

public class EncryptBill {

    private JPanel pnlUserInput;
    private JLabel lbl;
    private JLabel logo;
    private JLabel title;
    private JPanel pnlTitle;

    String filepath;
    String password;
    private JPanel pnlButton;
    private JButton btnChooseFile;
    private JButton btnEncrypt;
    private JButton btnCancel;

    public EncryptBill() {
        JFrame frame = new JFrame("Encrypt Bill");
        frame.setLayout(new GridLayout(1, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ---------
        // panel for user input
        createAndShowGUI();

        // ----------
        frame.getContentPane().setBackground(Color.darkGray);
        // frame.add(new JLabel(new ImageIcon("Path/To/Your/Image.png")));

        JPanel overall = new JPanel();
        GridLayout layout = new GridLayout(3, 1);
        layout.setHgap(10);
        overall.setLayout(layout);
        // overall.setHorizontalAlignment(SwingConstants.RIGHT);

        ImageIcon icon = new ImageIcon("./Logo4.png");
        logo = new JLabel(icon);
        // logo.setPreferredSize(new Dimension(20, 15));
        logo.setHorizontalAlignment(SwingConstants.LEFT); // set the horizontal alignement on the x axis !
        logo.setVerticalAlignment(SwingConstants.TOP);

        frame.add(logo, BorderLayout.NORTH);

        overall.add(pnlTitle);
        overall.add(pnlUserInput);
        overall.add(pnlButton);

        frame.add(overall, BorderLayout.SOUTH);
        // frame.add(pnlUserInput);
        // frame.add(pnlButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setSize(700, 260);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private byte[] readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return Files.readAllBytes(file.toPath());
    }

    private byte[] encrypt(byte[] arr, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key key = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(arr);
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

    public void encryptFile(String fileName, String password) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        storeFile(encrypt(readFile(fileName), password), fileName + ".crypt");
    }

    private void createAndShowGUI() {
        pnlUserInput = new JPanel();
        pnlUserInput.setBackground(Color.darkGray);
        btnChooseFile = new JButton("Choose File");
        lbl = new JLabel("No file selected");
        lbl.setForeground(Color.white);

        title = new JLabel("Encrypt Bill");
        title.setFont(new Font("Serif", Font.BOLD, 25));
        title.setForeground(Color.white);

        pnlTitle = new JPanel();
        pnlTitle.setBackground(Color.darkGray);
        pnlTitle.add(title, BorderLayout.NORTH);

        pnlUserInput.add(btnChooseFile, BorderLayout.WEST);
        pnlUserInput.add(lbl, BorderLayout.EAST);

        pnlButton = new JPanel();
        pnlButton.setBackground(Color.darkGray);
        btnEncrypt = new JButton("Encrypt");
        btnCancel = new JButton("Cancel");

        pnlButton.add(btnEncrypt, BorderLayout.WEST);
        pnlButton.add(btnCancel, BorderLayout.WEST);

        lbl.getText();

        // ---------

        btnChooseFile.addActionListener(new ChooseFileListener()); // add listeners to elements
        btnEncrypt.addActionListener(new EncryptBillListener());
        btnCancel.addActionListener(new CancelListener());
    } // createAndShowGUI

    private class ChooseFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Choose File");
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                // System.out.println("getCurrentDirectory(): " +
                // chooser.getCurrentDirectory());
                // System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                filepath = chooser.getSelectedFile().getAbsolutePath();
                String fileName = chooser.getSelectedFile().getAbsolutePath()
                        .substring(chooser.getSelectedFile().getAbsolutePath().lastIndexOf("\\") + 1);
                System.out.println(fileName);
                lbl.setText(fileName);

                JPanel panel = new JPanel();
                JLabel label = new JLabel("Enter Encryption Password:");
                JPasswordField pass = new JPasswordField(10);
                panel.add(label);
                panel.add(pass);
                String[] options = new String[] { "OK", "Cancel" };
                int option = JOptionPane.showOptionDialog(null, panel, "Password",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[1]);
                if (option == 0) // pressing OK button
                {
                    char[] passwords = pass.getPassword();
                    // System.out.println("Your password is: " + new String(passwords));
                    password = new String(passwords);
                }

            } else {
                System.out.println("No Selection ");
            }

        }
    } //

    private class EncryptBillListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                encryptFile(filepath, password);
                JOptionPane.showMessageDialog(null, "File Encrypted Successfully", "Encrypt Bill",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                System.out.println("Something went wrong.");
            }

            System.exit(0);
        }
    } //

    private class CancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            System.exit(0);
        }
    } //

    public static void main(String[] args) {
        new EncryptBill();

    }

}