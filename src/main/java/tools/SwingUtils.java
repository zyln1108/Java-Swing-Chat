package tools;

import javax.swing.*;
import java.awt.*;
public class SwingUtils {

    /**
     * 设置大小为电脑屏幕的指定比例
     * @param jFrame 窗口
     * @param w 电脑屏幕宽度的w倍，w<=1
     * @param h 电脑屏幕高度的h倍，h<=1
     */
    public static void setSize(JFrame jFrame, double w, double h){
        int width = (int) (getScreenWidth() * w);
        int height = (int) (getScreenHeight() * h);
        jFrame.setSize(width, height);
    }

    /**
     * 获得电脑屏幕宽度
     * @return
     */
    public static double getScreenWidth() {
        return Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    }

    /**
     * 获得电脑屏幕宽度
     * @return
     */
    public static double getScreenHeight() {
        return Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }

    /**
     * 按照width、height比例调整图标大小
     * @param imageIcon 要调整的图标
     * @param width 调整后的图像宽度
     * @param height 调整后的图像高度
     * @return
     */
    public static ImageIcon scaleImageIcon(ImageIcon imageIcon, double width, double height) {
        imageIcon.setImage(imageIcon.getImage().getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH));
        return imageIcon;
    }
}
