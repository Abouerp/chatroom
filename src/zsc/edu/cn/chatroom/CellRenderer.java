package zsc.edu.cn.chatroom;

import javax.swing.*;
import java.awt.*;

/**
 * @author Abouerp
 */
public class CellRenderer extends JLabel implements ListCellRenderer{
    CellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        /**
         * 加入宽度为5的空白边框
         */
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (value != null) {
            setText(value.toString());
            //设置头像
            setIcon(new ImageIcon("images//tou1.png"));
        }
        //设置用户被选中和为被选中两种前景与背景颜色状态表示
        if (isSelected) {
            //被选中时   设置背景色
            setBackground(new Color(255, 255, 153));
            //设置字体色
            setForeground(Color.black);
        } else {
            //未被选中   设置背景色
            setBackground(Color.white);
            //设置字体色
            setForeground(Color.black);
        }
        setEnabled(list.isEnabled());
        setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
        setOpaque(true);
        return this;
    }
}
