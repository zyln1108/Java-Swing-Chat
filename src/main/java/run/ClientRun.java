package run;

import com.formdev.flatlaf.FlatDarkLaf;
import controller.ClientLoginController;
import tools.SwingUtils;
import view.ClientLoginForm;

import javax.swing.*;
import java.awt.*;

public class ClientRun {
    public static JFrame frame;

    public static void main(String[] args) {
        login();
    }

    private static void login(){
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置", JOptionPane.WARNING_MESSAGE);
            return;
        }
        frame = new JFrame("Chat客户端登陆");
        Image image = Toolkit.getDefaultToolkit().getImage(frame.getClass().getResource("/icon.png"));
        frame.setIconImage(image);
        ClientLoginForm clientLoginForm = new ClientLoginForm();
        frame.setContentPane(clientLoginForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.setSize(frame, 0.35, 0.4);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ClientLoginController clientController = new ClientLoginController(clientLoginForm);
    }
}
