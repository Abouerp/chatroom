package zsc.edu.cn.chatroom;



import zsc.edu.cn.bean.CatBean;
import zsc.edu.cn.util.CatUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * �����ң������Լ��߼�
 * �û��б���ÿ���û���ʾ״̬
 *
 * @author Abouerp
 */
public class CatChatRoom extends JFrame {

    private static final long serialVersionUID = 6129126482250125466L;

    private static JPanel contentPane;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static String name;
    private static JTextArea textArea;
    private static AbstractListModel listmodel;
    private static JList list;
    private static String filePath;
    private static JLabel lblNewLabel;
    private static JProgressBar progressBar;
    private static Vector onlines;
    private static boolean isSendFile = false;
    private static boolean isReceiveFile = false;

    private File contentFile;

    /**
     * Create the frame.
     */

    public CatChatRoom(String u_name, Socket client) {
        // ��ֵ
        name = u_name;
        clientSocket = client;
        onlines = new Vector();

        SwingUtilities.updateComponentTreeUI(this);

        try {
            //�ı䴰����ʾ���
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }


        setTitle(name);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(440, 200, 896, 796);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override//���ر���ͼƬ
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("images\\q1.jpg").getImage(), 0, 0,
                        getWidth(), getHeight(), null);
            }
        };

        try {
            contentFile = new File(name);
        } catch (Exception e) {
            // TODO: handle exception
        }

        setContentPane(contentPane);
        contentPane.setLayout(null);

        // ������Ϣ��ʾ����

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 10, 490, 510);
        getContentPane().add(scrollPane);
        //������Ϣ��ʾ��
        textArea = new JTextArea();
        textArea.setEditable(false);
        //�����Զ����й���
        textArea.setLineWrap(true);
        //������в����ֹ���
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("sdf", Font.BOLD, 17));
        try {
            scrollPane.setViewportView(textArea);
        } catch (Exception e) {
            System.out.println("");
        }

        // ��������
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 557, 498, 137);
        getContentPane().add(scrollPane_1);
        //������Ϣ��ʾ��
        final JTextArea textArea_1 = new JTextArea();
        textArea_1.setLineWrap(true);//�����Զ����й���
        textArea_1.setWrapStyleWord(true);//������в����ֹ���
        textArea_1.setFont(new Font("sdf", Font.PLAIN, 18));
        scrollPane_1.setViewportView(textArea_1);

        // �رհ�ť
        final JButton btnNewButton = new JButton("�ر�");
        btnNewButton.setBounds(270, 700, 100, 40);
        getContentPane().add(btnNewButton);

        // ���Ͱ�ť
        JButton btnNewButton_1 = new JButton("����");
        btnNewButton_1.setBounds(224 + 180, 700, 100, 40);
        getRootPane().setDefaultButton(btnNewButton_1);
        getContentPane().add(btnNewButton_1);

        JButton btnNewButton_sendAll = new JButton("��������");
        btnNewButton_sendAll.setBounds(136, 700, 100, 40);
        getRootPane().setDefaultButton(btnNewButton_sendAll);
        getContentPane().add(btnNewButton_sendAll);

        //������ť
        JButton btnNewButton_4 = new JButton("����");
        btnNewButton_4.setBounds(414, 523, 100, 34);
        //����ʱ�����
        btnNewButton_4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(null);

            }
        });
        getContentPane().add(btnNewButton_4);

        // ���߿ͻ��б�
        listmodel = new UUListModel(onlines);
        list = new JList(listmodel);
        list.setCellRenderer(new CellRenderer());
        list.setOpaque(false);
        Border etch = BorderFactory.createEtchedBorder();
        list.setBorder(BorderFactory.createTitledBorder(etch, "��ǰ�����û�:", TitledBorder.LEADING, TitledBorder.TOP, new Font(
                "sdf", Font.BOLD, 20), Color.green));
        //�����û���������
        JScrollPane scrollPane_2 = new JScrollPane(list);
        scrollPane_2.setBounds(530, 10, 345, 575);
        scrollPane_2.setOpaque(false);
        scrollPane_2.getViewport().setOpaque(false);
        getContentPane().add(scrollPane_2);

        // �ļ�������        progressBar       ������ �򵥵�������ȵı仯���
        progressBar = new JProgressBar();
        progressBar.setBounds(530, 650, 345, 25);
        progressBar.setMinimum(1);
        progressBar.setMaximum(100);
        getContentPane().add(progressBar);

        // �ļ�������ʾ
        lblNewLabel = new JLabel("�ļ�������Ϣ����");
        lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 15));
        lblNewLabel.setBackground(Color.green);
        lblNewLabel.setBounds(530, 610, 345, 25);
        lblNewLabel.setForeground(Color.WHITE);
        getContentPane().add(lblNewLabel);

        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            // ��¼���߿ͻ�����Ϣ��catbean�У������͸�������
            CatBean bean = new CatBean();
            bean.setType(0);
            bean.setName(name);
            bean.setTimer(CatUtil.getTimer());
            oos.writeObject(bean);
            oos.flush();

            // �����ͻ������߳�
            new ClientInputThread().start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ���Ͱ�ť�¼�����
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                List to = list.getSelectedValuesList();//��ȡ��ѡ�����
                //��δѡ�����
                if (to.size() < 1) {
                    JOptionPane.showMessageDialog(getContentPane(), "��ѡ���������");
                    return;
                }
                //��ѡ�����Ϊ�Լ�
                if (to.toString().contains(name + "(��)")) {
                    JOptionPane
                            .showMessageDialog(getContentPane(), "�������Լ�������Ϣ");
                    return;
                }
                //��������ϢΪ��
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "���ܷ��Ϳ���Ϣ");
                    return;
                }
                //��������£��������ӣ���������
                CatBean clientBean = new CatBean();
                clientBean.setType(1);
                clientBean.setName(name);
                String time = CatUtil.getTimer();
                clientBean.setTimer(time);
                clientBean.setInfo(info);
                HashSet set = new HashSet();
                set.addAll(to);
                clientBean.setClients(set);
                sendMessage(clientBean);

                // �Լ���������ҲҪ��ʵ���Լ�����Ļ����
                textArea.append(time + " �Ҷ�" + to + "˵:\r\n" + info + "\r\n");
                //��շ�����Ϣ�������»�ȡ����
                textArea_1.setText(null);
                textArea_1.requestFocus();
            }
        });

        /**
         * ���͸�������
         */
        btnNewButton_sendAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                //��������ϢΪ��
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "���ܷ��Ϳ���Ϣ");
                    return;
                }

                //��������£��������ӣ���������
                CatBean clientBean = new CatBean();
                clientBean.setType(1);
                clientBean.setName(name);
                String time = CatUtil.getTimer();
                clientBean.setTimer(time);
                clientBean.setInfo(info);
                HashSet set = new HashSet();
                set.addAll(onlines);
                clientBean.setClients(set);
                sendMessage(clientBean);

                // �Լ���������ҲҪ��ʵ���Լ�����Ļ����
                textArea.append(time + " �ҶԴ��˵:\r\n" + info + "\r\n");

                //��շ�����Ϣ�������»�ȡ����
                textArea_1.setText(null);
                textArea_1.requestFocus();
            }
        });


        // �رհ�ť�¼�����
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //���ļ�����ʱ���ܹرմ���
                if (isSendFile || isReceiveFile) {
                    JOptionPane.showMessageDialog(contentPane,
                            "���ڴ����ļ��У��������뿪...",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    //����������Ϣ
                    btnNewButton.setEnabled(false);
                    CatBean clientBean = new CatBean();
                    clientBean.setType(-1);
                    clientBean.setName(name);
                    clientBean.setTimer(CatUtil.getTimer());
                    sendMessage(clientBean);
                }
            }
        });

        // �����¼�����
        this.addWindowListener(new WindowAdapter() {
            //�뿪
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                if (isSendFile || isReceiveFile) {
                    JOptionPane.showMessageDialog(contentPane,
                            "���ڴ����ļ��У��������뿪...",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    int result = JOptionPane.showConfirmDialog(getContentPane(),
                            "��ȷ��Ҫ�뿪������");//ȡ���û�ѡ��
                    //���û�ѡ��ȷ���뿪������������Ϣ
                    if (result == 0) {
                        CatBean clientBean = new CatBean();
                        clientBean.setType(-1);
                        clientBean.setName(name);
                        clientBean.setTimer(CatUtil.getTimer());
                        sendMessage(clientBean);
                    }
                }
            }
        });

        // �����û��б����
        list.addMouseListener(new MouseAdapter() {

            @Override//����¼�����
            public void mouseClicked(MouseEvent e) {
                List to = list.getSelectedValuesList();
                //˫���¼��������ļ�
                if (e.getClickCount() == 2) {
                    //������Ͷ���Ϊ�Լ�
                    if (to.toString().contains(name + "(��)")) {
                        JOptionPane.showMessageDialog(getContentPane(), "�������Լ������ļ�");
                        return;
                    }

                    // ˫�����ļ��ļ�ѡ���
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("ѡ���ļ���"); // ����
                    chooser.showDialog(getContentPane(), "ѡ��"); // ��ť������

                    // �ж��Ƿ�ѡ�����ļ�
                    if (chooser.getSelectedFile() != null) {
                        // ��ȡ·��
                        filePath = chooser.getSelectedFile().getPath();
                        File file = new File(filePath);
                        // ����ļ�Ϊ��
                        if (file.length() == 0) {
                            JOptionPane.showMessageDialog(getContentPane(), filePath + "�ļ�Ϊ��,��������.");
                            return;
                        }
                        //����״̬���������ӣ���������
                        CatBean clientBean = new CatBean();
                        clientBean.setType(2);// �������ļ�
                        clientBean.setSize(new Long(file.length()).intValue());
                        clientBean.setName(name);
                        clientBean.setTimer(CatUtil.getTimer());
                        clientBean.setFileName(file.getName()); // ��¼�ļ�������
                        clientBean.setInfo("�������ļ�");

                        // �ж�Ҫ���͸�˭
                        HashSet<String> set = new HashSet<String>();
                        set.addAll(list.getSelectedValuesList());
                        clientBean.setClients(set);
                        sendMessage(clientBean);
                    }
                }
            }
        });

    }

    /**
     * �߳̽�����
     *
     * @author Abouerp
     */
    class ClientInputThread extends Thread {

        @Override
        public void run() {
            try {
                // ��ͣ�Ĵӷ�����������Ϣ
                while (true) {
                    ois = new ObjectInputStream(clientSocket.getInputStream());
                    final CatBean bean = (CatBean) ois.readObject();
                    //�������ܵ�catbean������
                    switch (bean.getType()) {
                        case 0: {
                            // �����б�
                            onlines.clear();//����б�
                            HashSet<String> clients = bean.getClients();
                            Iterator<String> it = clients.iterator();
                            //���¼���
                            while (it.hasNext()) {
                                String ele = it.next();
                                if (name.equals(ele)) {
                                    onlines.add(ele + "(��)");
                                } else {
                                    onlines.add(ele);
                                }
                            }

                            listmodel = new UUListModel(onlines);
                            list.setModel(listmodel);
                            textArea.append(bean.getInfo() + "\r\n");
                            textArea.selectAll();
                            break;
                        }
                        case -1: {
                            //ֱ������
                            return;
                        }
                        case 1: {
                            String info = null;
                            //��ȡ������Ϣ
                            if (bean.getClients().size() != 1) {
                                info = bean.getTimer() +" "+ bean.getName() + "˵ \r\n";
                            } else {
                                info = bean.getTimer() + "  " + bean.getName()
                                        + "�� " + bean.getClients() + "˵:\r\n";
                            }//���Է�������Ϣ���Լ��������滻�ɡ��ҡ�
                            if (info.contains(name)) {
                                info = info.replace(name, "��");
                            }
                            textArea.append(info + bean.getInfo() + "\r\n");
                            textArea.selectAll();
                            break;
                        }
                        case 2: {
                            // ���ڵȴ�Ŀ��ͻ�ȷ���Ƿ�����ļ��Ǹ�����״̬�������������̴߳���
                            new Thread() {
                                @Override
                                public void run() {
                                    //��ʾ�Ƿ�����ļ��Ի���
                                    int result = JOptionPane.showConfirmDialog(
                                            getContentPane(), bean.getInfo());
                                    //���û�������ѡ��������Ӧ
                                    switch (result) {
                                        case 0: {  //�����ļ�
                                            JFileChooser chooser = new JFileChooser();
                                            chooser.setDialogTitle("�����ļ���"); // ����
                                            //Ĭ���ļ����ƻ��з��ڵ�ǰĿ¼��
                                            chooser.setSelectedFile(new File(bean.getFileName()));
                                            // ���ð�ť����
                                            chooser.showDialog(getContentPane(), "����");
                                            //����·��
                                            String saveFilePath = chooser.getSelectedFile().toString();

                                            //�����ͻ�CatBean
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(3);
                                            clientBean.setName(name);  //�����ļ��Ŀͻ�����
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(saveFilePath);
                                            clientBean.setInfo("ȷ�������ļ�");

                                            // �ж�Ҫ���͸�˭
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);  //�ļ���Դ
                                            clientBean.setTo(bean.getClients());//����Щ�ͻ������ļ�

                                            // �����µ�tcp socket ��������
                                            try {
                                                // 0���Ի�ȡ���еĶ˿ں�
                                                ServerSocket ss = new ServerSocket(0);
                                                clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
                                                clientBean.setPort(ss.getLocalPort());
                                                // ��ͨ�����������߷��ͷ�, �����ֱ�ӷ����ļ�����������
                                                sendMessage(clientBean);
                                                isReceiveFile = true;
                                                //�ȴ��ļ���Դ���û��������ļ�....Ŀ���û��������϶�ȡ�ļ�����д�ڱ�����
                                                Socket sk = ss.accept();
                                                textArea.append(CatUtil.getTimer() + "  " + bean.getFileName() + "�ļ�������.\r\n");
                                                DataInputStream dis = new DataInputStream(new BufferedInputStream(sk.getInputStream()));//�������϶�ȡ�ļ�
                                                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saveFilePath)));

                                                int count = 0;
                                                int num = bean.getSize() / 100;
                                                int index = 0;
                                                while (count < bean.getSize()) {
                                                    int t = dis.read();
                                                    dos.write(t);
                                                    count++;

                                                    if (num > 0) {
                                                        if (count % num == 0 && index < 100) {
                                                            progressBar.setValue(++index);
                                                        }
                                                        lblNewLabel.setText("���ؽ���:" + count + "/" + bean.getSize() + "  ����" + index + "%");
                                                    } else {
                                                        lblNewLabel.setText("���ؽ���:" + count + "/" + bean.getSize() + "  ����:" + new Double(new Double(count).doubleValue() / new Double(bean.getSize()).doubleValue() * 100).intValue() + "%");
                                                        if (count == bean.getSize()) {
                                                            progressBar.setValue(100);
                                                        }
                                                    }
                                                }

                                                //���ļ���Դ�ͻ�������ʾ���ļ��������
                                                PrintWriter out = new PrintWriter(sk.getOutputStream(), true);
                                                out.println(CatUtil.getTimer() + " ���͸�" + name + "���ļ�[" + bean.getFileName() + "]" + "�ļ��������.\r\n");
                                                out.flush();
                                                dos.flush();
                                                dos.close();
                                                out.close();
                                                dis.close();
                                                sk.close();
                                                ss.close();
                                                textArea.append(CatUtil.getTimer() + "  " + bean.getFileName() + "�ļ��������.���λ��Ϊ:" + saveFilePath + "\r\n");
                                                isReceiveFile = false;
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                        default: {
                                            //�û�ѡ��ȡ������
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(4);
                                            clientBean.setName(name);  //�����ļ��Ŀͻ�����
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(bean.getFileName());
                                            clientBean.setInfo(CatUtil.getTimer() + "  "
                                                    + name + "ȡ�������ļ�["
                                                    + bean.getFileName() + "]");
                                            // �ж�Ҫ���͸�˭
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);  //�ļ���Դ
                                            clientBean.setTo(bean.getClients());//����Щ�ͻ������ļ�
                                            sendMessage(clientBean);
                                            break;
                                        }
                                    }
                                };
                            }.start();
                            break;
                        }
                        case 3: {  //Ŀ��ͻ�Ը������ļ���Դ�ͻ���ʼ��ȡ�����ļ������͵�������
                            textArea.append(bean.getTimer() + "  " + bean.getName() + "ȷ�������ļ�" + ",�ļ�������..\r\n");//�����������ʾ��Ϣ
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        isSendFile = true;
                                        //����Ҫ�����ļ��Ŀͻ��׽���
                                        Socket s = new Socket(bean.getIp(), bean.getPort());
                                        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));  //���ض�ȡ�ÿͻ��ղ�ѡ�е��ļ�
                                        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));  //����д���ļ�
                                        int size = dis.available();

                                        int count = 0;  //��ȡ����
                                        int num = size / 100;
                                        int index = 0;
                                        while (count < size) {
                                            int t = dis.read();
                                            dos.write(t);
                                            count++;
                                            //��ʾ�������
                                            if (num > 0) {
                                                if (count % num == 0 && index < 100) {
                                                    progressBar.setValue(++index);
                                                }
                                                lblNewLabel.setText("�ϴ�����:" + count + "/" + size + "  ����" + index + "%");
                                            } else {
                                                lblNewLabel.setText("�ϴ�����:" + count + "/"
                                                        + size + "  ����:" + new Double(new Double(count).doubleValue() / new Double(size).doubleValue() * 100).intValue() + "%"
                                                );
                                                if (count == size) {
                                                    progressBar.setValue(100);
                                                }
                                            }
                                        }
                                        dos.flush();
                                        dis.close();
                                        /**
                                         * ��ȡĿ��ͻ�����ʾ������ϵ���Ϣ
                                         */
                                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                        textArea.append(br.readLine() + "\r\n");
                                        isSendFile = false;
                                        br.close();
                                        s.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                };
                            }.start();
                            break;
                        }
                        case 4: {
                            textArea.append(bean.getInfo() + "\r\n");
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
    }

    /**
     * ������Ϣ����
     *
     * @param clientBean
     */
    private void sendMessage(CatBean clientBean) {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(clientBean);
            oos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
