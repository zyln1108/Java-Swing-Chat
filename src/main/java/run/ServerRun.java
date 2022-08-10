package run;

import com.formdev.flatlaf.FlatDarkLaf;
import controller.ServerController;
import tools.SwingUtils;
import view.ServerMainForm;

import javax.swing.*;
import java.awt.*;

public class ServerRun {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame frame = new JFrame("Chat服务端");
        Image image = Toolkit.getDefaultToolkit().getImage(frame.getClass().getResource("/icon.png"));
        frame.setIconImage(image);
        ServerMainForm serverMainForm = new ServerMainForm();
        frame.setContentPane(serverMainForm.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.setSize(frame, 0.55, 0.5);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ServerController serverController = new ServerController(serverMainForm);
    }
}
