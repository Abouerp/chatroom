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
 * 聊天室，界面以及逻辑
 * 用户列表中每个用户显示状态
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
        // 赋值
        name = u_name;
        clientSocket = client;
        onlines = new Vector();

        SwingUtilities.updateComponentTreeUI(this);

        try {
            //改变窗口显示风格
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

            @Override//加载背景图片
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

        // 聊天信息显示区域

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 10, 490, 510);
        getContentPane().add(scrollPane);
        //聊天信息显示框
        textArea = new JTextArea();
        textArea.setEditable(false);
        //激活自动换行功能
        textArea.setLineWrap(true);
        //激活断行不断字功能
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("sdf", Font.BOLD, 17));
        try {
            scrollPane.setViewportView(textArea);
        } catch (Exception e) {
            System.out.println("");
        }

        // 打字区域
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 557, 498, 137);
        getContentPane().add(scrollPane_1);
        //输入信息显示框
        final JTextArea textArea_1 = new JTextArea();
        textArea_1.setLineWrap(true);//激活自动换行功能
        textArea_1.setWrapStyleWord(true);//激活断行不断字功能
        textArea_1.setFont(new Font("sdf", Font.PLAIN, 18));
        scrollPane_1.setViewportView(textArea_1);

        // 关闭按钮
        final JButton btnNewButton = new JButton("关闭");
        btnNewButton.setBounds(270, 700, 100, 40);
        getContentPane().add(btnNewButton);

        // 发送按钮
        JButton btnNewButton_1 = new JButton("发送");
        btnNewButton_1.setBounds(224 + 180, 700, 100, 40);
        getRootPane().setDefaultButton(btnNewButton_1);
        getContentPane().add(btnNewButton_1);

        JButton btnNewButton_sendAll = new JButton("发给所有");
        btnNewButton_sendAll.setBounds(136, 700, 100, 40);
        getRootPane().setDefaultButton(btnNewButton_sendAll);
        getContentPane().add(btnNewButton_sendAll);

        //清屏按钮
        JButton btnNewButton_4 = new JButton("清屏");
        btnNewButton_4.setBounds(414, 523, 100, 34);
        //增加时间监听
        btnNewButton_4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(null);

            }
        });
        getContentPane().add(btnNewButton_4);

        // 在线客户列表
        listmodel = new UUListModel(onlines);
        list = new JList(listmodel);
        list.setCellRenderer(new CellRenderer());
        list.setOpaque(false);
        Border etch = BorderFactory.createEtchedBorder();
        list.setBorder(BorderFactory.createTitledBorder(etch, "当前在线用户:", TitledBorder.LEADING, TitledBorder.TOP, new Font(
                "sdf", Font.BOLD, 20), Color.green));
        //在线用户滚动区域
        JScrollPane scrollPane_2 = new JScrollPane(list);
        scrollPane_2.setBounds(530, 10, 345, 575);
        scrollPane_2.setOpaque(false);
        scrollPane_2.getViewport().setOpaque(false);
        getContentPane().add(scrollPane_2);

        // 文件传输栏        progressBar       它可以 简单地输出进度的变化情况
        progressBar = new JProgressBar();
        progressBar.setBounds(530, 650, 345, 25);
        progressBar.setMinimum(1);
        progressBar.setMaximum(100);
        getContentPane().add(progressBar);

        // 文件传输提示
        lblNewLabel = new JLabel("文件传输信息栏：");
        lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 15));
        lblNewLabel.setBackground(Color.green);
        lblNewLabel.setBounds(530, 610, 345, 25);
        lblNewLabel.setForeground(Color.WHITE);
        getContentPane().add(lblNewLabel);

        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            // 记录上线客户的信息在catbean中，并发送给服务器
            CatBean bean = new CatBean();
            bean.setType(0);
            bean.setName(name);
            bean.setTimer(CatUtil.getTimer());
            oos.writeObject(bean);
            oos.flush();

            // 启动客户接收线程
            new ClientInputThread().start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 发送按钮事件监听
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                List to = list.getSelectedValuesList();//获取所选择对象
                //若未选择对象
                if (to.size() < 1) {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择聊天对象");
                    return;
                }
                //若选择对象为自己
                if (to.toString().contains(name + "(我)")) {
                    JOptionPane
                            .showMessageDialog(getContentPane(), "不能向自己发送信息");
                    return;
                }
                //若发送信息为空
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "不能发送空信息");
                    return;
                }
                //正常情况下，建立连接，发送数据
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

                // 自己发的内容也要现实在自己的屏幕上面
                textArea.append(time + " 我对" + to + "说:\r\n" + info + "\r\n");
                //清空发送消息栏并重新获取焦点
                textArea_1.setText(null);
                textArea_1.requestFocus();
            }
        });

        /**
         * 发送给所有人
         */
        btnNewButton_sendAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                //若发送信息为空
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "不能发送空信息");
                    return;
                }

                //正常情况下，建立连接，发送数据
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

                // 自己发的内容也要现实在自己的屏幕上面
                textArea.append(time + " 我对大家说:\r\n" + info + "\r\n");

                //清空发送消息栏并重新获取焦点
                textArea_1.setText(null);
                textArea_1.requestFocus();
            }
        });


        // 关闭按钮事件监听
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //在文件传输时不能关闭窗口
                if (isSendFile || isReceiveFile) {
                    JOptionPane.showMessageDialog(contentPane,
                            "正在传输文件中，您不能离开...",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    //发送下线消息
                    btnNewButton.setEnabled(false);
                    CatBean clientBean = new CatBean();
                    clientBean.setType(-1);
                    clientBean.setName(name);
                    clientBean.setTimer(CatUtil.getTimer());
                    sendMessage(clientBean);
                }
            }
        });

        // 窗口事件监听
        this.addWindowListener(new WindowAdapter() {
            //离开
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                if (isSendFile || isReceiveFile) {
                    JOptionPane.showMessageDialog(contentPane,
                            "正在传输文件中，您不能离开...",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                } else {
                    int result = JOptionPane.showConfirmDialog(getContentPane(),
                            "您确定要离开聊天室");//取得用户选择
                    //若用户选择确定离开，发送下线消息
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

        // 在线用户列表监听
        list.addMouseListener(new MouseAdapter() {

            @Override//鼠标事件监听
            public void mouseClicked(MouseEvent e) {
                List to = list.getSelectedValuesList();
                //双击事件代表发送文件
                if (e.getClickCount() == 2) {
                    //如过发送对象为自己
                    if (to.toString().contains(name + "(我)")) {
                        JOptionPane.showMessageDialog(getContentPane(), "不能向自己发送文件");
                        return;
                    }

                    // 双击打开文件文件选择框
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("选择文件框"); // 标题
                    chooser.showDialog(getContentPane(), "选择"); // 按钮的名字

                    // 判定是否选择了文件
                    if (chooser.getSelectedFile() != null) {
                        // 获取路径
                        filePath = chooser.getSelectedFile().getPath();
                        File file = new File(filePath);
                        // 如果文件为空
                        if (file.length() == 0) {
                            JOptionPane.showMessageDialog(getContentPane(), filePath + "文件为空,不允许发送.");
                            return;
                        }
                        //正常状态，建立连接，发送请求
                        CatBean clientBean = new CatBean();
                        clientBean.setType(2);// 请求发送文件
                        clientBean.setSize(new Long(file.length()).intValue());
                        clientBean.setName(name);
                        clientBean.setTimer(CatUtil.getTimer());
                        clientBean.setFileName(file.getName()); // 记录文件的名称
                        clientBean.setInfo("请求发送文件");

                        // 判断要发送给谁
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
     * 线程接收类
     *
     * @author Abouerp
     */
    class ClientInputThread extends Thread {

        @Override
        public void run() {
            try {
                // 不停的从服务器接收信息
                while (true) {
                    ois = new ObjectInputStream(clientSocket.getInputStream());
                    final CatBean bean = (CatBean) ois.readObject();
                    //分析接受到catbean的类型
                    switch (bean.getType()) {
                        case 0: {
                            // 更新列表
                            onlines.clear();//清空列表
                            HashSet<String> clients = bean.getClients();
                            Iterator<String> it = clients.iterator();
                            //重新加载
                            while (it.hasNext()) {
                                String ele = it.next();
                                if (name.equals(ele)) {
                                    onlines.add(ele + "(我)");
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
                            //直接下线
                            return;
                        }
                        case 1: {
                            String info = null;
                            //获取发送信息
                            if (bean.getClients().size() != 1) {
                                info = bean.getTimer() +" "+ bean.getName() + "说 \r\n";
                            } else {
                                info = bean.getTimer() + "  " + bean.getName()
                                        + "对 " + bean.getClients() + "说:\r\n";
                            }//将对方发送消息中自己的名字替换成“我”
                            if (info.contains(name)) {
                                info = info.replace(name, "我");
                            }
                            textArea.append(info + bean.getInfo() + "\r\n");
                            textArea.selectAll();
                            break;
                        }
                        case 2: {
                            // 由于等待目标客户确认是否接收文件是个阻塞状态，所以这里用线程处理
                            new Thread() {
                                @Override
                                public void run() {
                                    //显示是否接收文件对话框
                                    int result = JOptionPane.showConfirmDialog(
                                            getContentPane(), bean.getInfo());
                                    //对用户做出的选择做出反应
                                    switch (result) {
                                        case 0: {  //接收文件
                                            JFileChooser chooser = new JFileChooser();
                                            chooser.setDialogTitle("保存文件框"); // 标题
                                            //默认文件名称还有放在当前目录下
                                            chooser.setSelectedFile(new File(bean.getFileName()));
                                            // 设置按钮名字
                                            chooser.showDialog(getContentPane(), "保存");
                                            //保存路径
                                            String saveFilePath = chooser.getSelectedFile().toString();

                                            //创建客户CatBean
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(3);
                                            clientBean.setName(name);  //接收文件的客户名字
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(saveFilePath);
                                            clientBean.setInfo("确定接收文件");

                                            // 判断要发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);  //文件来源
                                            clientBean.setTo(bean.getClients());//给这些客户发送文件

                                            // 创建新的tcp socket 接收数据
                                            try {
                                                // 0可以获取空闲的端口号
                                                ServerSocket ss = new ServerSocket(0);
                                                clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
                                                clientBean.setPort(ss.getLocalPort());
                                                // 先通过服务器告诉发送方, 你可以直接发送文件到我这里了
                                                sendMessage(clientBean);
                                                isReceiveFile = true;
                                                //等待文件来源的用户，输送文件....目标用户从网络上读取文件，并写在本地上
                                                Socket sk = ss.accept();
                                                textArea.append(CatUtil.getTimer() + "  " + bean.getFileName() + "文件保存中.\r\n");
                                                DataInputStream dis = new DataInputStream(new BufferedInputStream(sk.getInputStream()));//从网络上读取文件
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
                                                        lblNewLabel.setText("下载进度:" + count + "/" + bean.getSize() + "  整体" + index + "%");
                                                    } else {
                                                        lblNewLabel.setText("下载进度:" + count + "/" + bean.getSize() + "  整体:" + new Double(new Double(count).doubleValue() / new Double(bean.getSize()).doubleValue() * 100).intValue() + "%");
                                                        if (count == bean.getSize()) {
                                                            progressBar.setValue(100);
                                                        }
                                                    }
                                                }

                                                //给文件来源客户发条提示，文件保存完毕
                                                PrintWriter out = new PrintWriter(sk.getOutputStream(), true);
                                                out.println(CatUtil.getTimer() + " 发送给" + name + "的文件[" + bean.getFileName() + "]" + "文件保存完毕.\r\n");
                                                out.flush();
                                                dos.flush();
                                                dos.close();
                                                out.close();
                                                dis.close();
                                                sk.close();
                                                ss.close();
                                                textArea.append(CatUtil.getTimer() + "  " + bean.getFileName() + "文件保存完毕.存放位置为:" + saveFilePath + "\r\n");
                                                isReceiveFile = false;
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                        default: {
                                            //用户选择取消接收
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(4);
                                            clientBean.setName(name);  //接收文件的客户名字
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(bean.getFileName());
                                            clientBean.setInfo(CatUtil.getTimer() + "  "
                                                    + name + "取消接收文件["
                                                    + bean.getFileName() + "]");
                                            // 判断要发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);  //文件来源
                                            clientBean.setTo(bean.getClients());//给这些客户发送文件
                                            sendMessage(clientBean);
                                            break;
                                        }
                                    }
                                };
                            }.start();
                            break;
                        }
                        case 3: {  //目标客户愿意接收文件，源客户开始读取本地文件并发送到网络上
                            textArea.append(bean.getTimer() + "  " + bean.getName() + "确定接收文件" + ",文件传送中..\r\n");//在聊天面板显示信息
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        isSendFile = true;
                                        //创建要接收文件的客户套接字
                                        Socket s = new Socket(bean.getIp(), bean.getPort());
                                        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));  //本地读取该客户刚才选中的文件
                                        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));  //网络写出文件
                                        int size = dis.available();

                                        int count = 0;  //读取次数
                                        int num = size / 100;
                                        int index = 0;
                                        while (count < size) {
                                            int t = dis.read();
                                            dos.write(t);
                                            count++;
                                            //显示传输进度
                                            if (num > 0) {
                                                if (count % num == 0 && index < 100) {
                                                    progressBar.setValue(++index);
                                                }
                                                lblNewLabel.setText("上传进度:" + count + "/" + size + "  整体" + index + "%");
                                            } else {
                                                lblNewLabel.setText("上传进度:" + count + "/"
                                                        + size + "  整体:" + new Double(new Double(count).doubleValue() / new Double(size).doubleValue() * 100).intValue() + "%"
                                                );
                                                if (count == size) {
                                                    progressBar.setValue(100);
                                                }
                                            }
                                        }
                                        dos.flush();
                                        dis.close();
                                        /**
                                         * 读取目标客户的提示保存完毕的信息
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
     * 传输信息方法
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
