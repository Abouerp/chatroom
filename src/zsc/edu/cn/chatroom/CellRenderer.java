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
         * ������Ϊ5�Ŀհױ߿�
         */
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (value != null) {
            setText(value.toString());
            //����ͷ��
            setIcon(new ImageIcon("images//tou1.png"));
        }
        //�����û���ѡ�к�Ϊ��ѡ������ǰ���뱳����ɫ״̬��ʾ
        if (isSelected) {
            //��ѡ��ʱ   ���ñ���ɫ
            setBackground(new Color(255, 255, 153));
            //��������ɫ
            setForeground(Color.black);
        } else {
            //δ��ѡ��   ���ñ���ɫ
            setBackground(Color.white);
            //��������ɫ
            setForeground(Color.black);
        }
        setEnabled(list.isEnabled());
        setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
        setOpaque(true);
        return this;
    }
}
