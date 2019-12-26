package zsc.edu.cn.login;

import zsc.edu.cn.bean.ClientBean;
import zsc.edu.cn.chatroom.CatChatRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;

import static java.awt.Color.WHITE;


/**
 * ��¼���洰��
 * ʵ���û���¼����
 *
 * @author Abouerp and coldFish
 */
public class CatLogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton btnNewButton;
    private JButton btnNewButton_1;
    private JLabel lblNewLabel;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    public static HashMap<String, ClientBean> onlines;
    final String URL = "jdbc:mysql://106.12.84.152:3307/socket?";
    final String USERNAME = "root";
    final String PASSWORD = "root";
    private Connection conn = null;
    private Statement state = null;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // ������½����
                    CatLogin frame = new CatLogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public CatLogin() {

        setTitle("�����ҵ�¼����");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(472, 235, 967, 681);

        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override//���豳��ͼƬ
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon(
                                "images/ͼƬ1.png").getImage(),
                        0, 0, getWidth(), getHeight(), null);
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //�û��������
        textField = new JTextField();
        /**
         * ����
         */
        textField.setFont(new Font("Serif", 1, 25));
        textField.setBounds(197, 376, 190, 50);
		textField.setForeground(WHITE);
        /**
         * ���ÿؼ��Ƿ�͸���ġ�true��ʾ��͸����false��ʾ͸�����ı�������Ϊ͸��
         */
        textField.setOpaque(false);
        contentPane.add(textField);
        textField.setColumns(10);

        //���������
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Serif", 2, 25));
		//������ʾ�ַ�
        passwordField.setEchoChar('*');
		//�ı�������Ϊ͸��
        passwordField.setOpaque(false);

		passwordField.setForeground(WHITE);
        passwordField.setBounds(197, 464, 190, 50);
        contentPane.add(passwordField);

        //��¼��ť
        btnNewButton = new JButton();
		//����ͼ�걳��
        btnNewButton.setIcon(new ImageIcon("images\\��¼1.png"));
        btnNewButton.setBounds(459, 537, 108, 54);
		//��������ڸ�������ʾ״̬�仯
        btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		//���ûس���Ӧ
        getRootPane().setDefaultButton(btnNewButton);
        contentPane.add(btnNewButton);

        //ע�ᰴť
        btnNewButton_1 = new JButton();
		//����ͼ�걳��
        btnNewButton_1.setIcon(new ImageIcon("images\\ͼƬ2.png"));
        btnNewButton_1.setBounds(773, 276, 111, 56);
		//��������ڸ�������ʾ״̬�仯
        btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPane.add(btnNewButton_1);

        // ��ʾ��Ϣ
        lblNewLabel = new JLabel();
        lblNewLabel.setBounds(220, 521, 190, 30);
        lblNewLabel.setFont(new Font("Dialog", 1, 15));
        lblNewLabel.setForeground(Color.red);
        contentPane.add(lblNewLabel);

        // ������½��ť
        btnNewButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                String u_name = textField.getText();//��ȡ�û�������û���
                String u_pwd = new String(passwordField.getPassword());//��ȡ�û����������
                try {
                    //�����û���¼����
                    LoginDatabase(u_name, u_pwd);

                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


            }
        });

        //ע�ᰴť����
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                btnNewButton_1.setEnabled(false);
                CatResign frame = new CatResign();
                frame.setVisible(true);// ��ʾע�����
                setVisible(false);// ���ص���½����

            }
        });
    }

    /**
     * ��¼���ݿⷽ���������û������û�����
     *
     * @param u_name
     * @param u_pwd
     * @throws SQLException
     */
    protected void LoginDatabase(String u_name, String u_pwd) throws SQLException {
        ResultSet result = null;
        try {
            // ���ӵ�MySQL
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            state = conn.createStatement();
            // ��MySQL�ж�ȡ����
            result = state.executeQuery("SELECT * FROM test WHERE `name` =" + "'" + u_name + "'");
            // ����û����Ƿ����
            if (result.next()) {
                // �������ݱȶ�
                if (result.getString(1).equals(u_name)) {
                    if (result.getString(2).equals(u_pwd)) {
                        try {
                            Socket client = new Socket("localhost", 6667);
                            btnNewButton.setEnabled(false);
                            CatChatRoom frame = new CatChatRoom(u_name, client);
                            /**
                             * ��½֮����ʾ������棬�ڽ���½��������
							 */
							frame.setVisible(true);
                            this.setVisible(false);

                        } catch (UnknownHostException e1) {

                            errorTip("The connection with the server is interrupted, please login again");
                        } catch (IOException e1) {

                            errorTip("The connection with the server is interrupted, please login again");
                        }

                    } else {
                        //�����û���ʾ��Ϣ���������Ϣ��
                        lblNewLabel.setText("���������������");
                        textField.setText("");
                        passwordField.setText("");
                        textField.requestFocus();
                    }
                } else {
                    //�����û���ʾ��Ϣ���������Ϣ��
                    lblNewLabel.setText("�������ǳƲ����ڣ�");
                    textField.setText("");
                    passwordField.setText("");
                    textField.requestFocus();
                }
            }

        } catch (SQLException ex) {
            System.out.println("SQLException:" + ex.getMessage());
            System.out.println("SQLState:" + ex.getSQLState());
            System.out.println("VendorError:" + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            System.out.println("Not Found:" + ex.toString());
        } finally {

        }
    }

    protected void errorTip(String str) {
        // TODO Auto-generated method stub
        //��ʾ������Ϣ�������Ϣ��
        JOptionPane.showMessageDialog(contentPane, str, "Error Message",
                JOptionPane.ERROR_MESSAGE);
        textField.setText("");
        passwordField.setText("");
        textField.requestFocus();
    }
}