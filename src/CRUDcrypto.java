import com.mysql.cj.protocol.Resultset;
import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;


public class CRUDcrypto {
    private JPanel Main;

    private JTable table1;

    private JLabel Visualize;
    private JLabel Title;
    private JLabel Mobile;
    private JLabel salarytext;
    private JLabel nametext;

    private JTextField salarytextfield;
    private JTextField mobiletextfield;
    private JTextField nametextfield;
    private JTextField searchtextfield;

    private JButton searchbutton;
    private JButton updatebutton;
    private JButton saveButton1;
    private JButton deleteButton;
    private JTextField salaryupdatefield;
    private JTextField idupdatefield;
    private JTextField deletetextfield;
    private JButton RefreshButton;

    //main function that sets the frame visible
    public static void main(String[] args) {
        JFrame frame = new JFrame("Employee");
        frame.setContentPane(new CRUDcrypto().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //creation of the variable needed for creating a sql connection via driver
    //required the jar file in the dependencies folder + import of the library
    Connection con;
    PreparedStatement pst;
    public void connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/CRUDcryptodb","root","");
            System.out.println("Successfully connected via jdbc Driver to the server    \n");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void table_load(){
        try{
            pst = con.prepareStatement("select * from employee");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
            //abbiamo importato mediante dipendenza una libreria esterna rs2xml.jar
            //che presenta il metodo DbUtils per creare e riempire dinamicamente una tabella Jtable.

        }catch(SQLException e2){
            e2.printStackTrace();
        }
    }

    private boolean isOnlyDigits(String str) {
        int count,i;
        count=0;
        for (i=0; i<str.length();i++){
            if(str.charAt(i)<='9' && str.charAt(i)>='0'){
                count++;
            }
        }
        if (count == str.length()){
            return true;
        }
        else {
            return false;
        }
    };

    private boolean isOnlyChars(String text) {
        return text.matches("[a-zA-Z]+");
    };

    public CRUDcrypto() {
        connect();
        table_load();
        saveButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pst = con.prepareStatement("insert into employee(empname,salary,mobile) values (?,?,?)");
                    if(isOnlyChars(nametextfield.getText()) && isOnlyDigits(salarytextfield.getText()) && isOnlyDigits(mobiletextfield.getText())){
                        //TODO: piuttosto che isOnlyDigits crea una funzione che verifichi e accetti realmente numeri di telefono
                        pst.setString(1,nametextfield.getText());
                        pst.setString(2,salarytextfield.getText());
                        pst.setString(3,mobiletextfield.getText());
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null,"Record added");
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Record not added, invalid input");
                    }
                    table_load();
                    nametextfield.setText(""); //risetta vuoto i text fields
                    salarytextfield.setText("");
                    mobiletextfield.setText("");
                    nametextfield.requestFocus(); // fa restare il cursore in quel textfield requestFocus

                } catch(SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        searchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pst = con.prepareStatement("select * from employee where salary <=?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    if (isOnlyDigits(searchtextfield.getText())){
                        pst.setString(1,searchtextfield.getText());
                        ResultSet rs = pst.executeQuery();
                        if(!rs.next()){
                            JOptionPane.showMessageDialog(null,"No record found");
                        }
                        else{
                            rs.previous();
                            table1.setModel(DbUtils.resultSetToTableModel(rs));
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Invalid input");
                    }
                    searchtextfield.setText("");

                }catch (SQLException e3){
                    e3.printStackTrace();
                }

            }
        });
        updatebutton.addActionListener(new ActionListener() {
            //TODO: sistema scomparsa testo nei due textfield di update ( se clicchi su uno sparisce l'altro)
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pst = con.prepareStatement("UPDATE employee set salary = ? where id= ?");
                    if(isOnlyDigits(salaryupdatefield.getText()) && isOnlyDigits(idupdatefield.getText())){
                        pst.setString(1,salaryupdatefield.getText());
                        pst.setString(2,idupdatefield.getText());
                        int rs = pst.executeUpdate();
                        if(rs == 0){
                            JOptionPane.showMessageDialog(null,"Not updated, probably id not exists");
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Update completed");
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null,"invalid input");
                    }
                    table_load();
                    salaryupdatefield.setText("");
                    idupdatefield.setText("");
                } catch(SQLException e4){
                    e4.printStackTrace();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    pst = con.prepareStatement("DELETE FROM employee where id = ?");
                    if(isOnlyDigits(deletetextfield.getText())) {
                        pst.setString(1, deletetextfield.getText());
                        int rs = pst.executeUpdate();
                        System.out.println(rs);
                        if(rs == 0){
                            JOptionPane.showMessageDialog(null,"No tuple for such id");
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Deletion completed");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,"Invalid input");
                    }
                    table_load();
                    deletetextfield.setText("");
                }
                catch(SQLException e5){
                    e5.printStackTrace();
                }
            }
        });
        RefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table_load();
            }
        });
        searchtextfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                searchtextfield.setText("");
            }
        });
        salaryupdatefield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                salaryupdatefield.setText("");
            }
        });
        searchtextfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                searchtextfield.setText("salary");
            }
        });
        salaryupdatefield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                salaryupdatefield.setText("salary");
            }
        });
        idupdatefield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                idupdatefield.setText("");
            }
        });

        idupdatefield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                idupdatefield.setText("EmployeeId");
            }
        });

        deletetextfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                deletetextfield.setText("");
            }
        });

        deletetextfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                deletetextfield.setText("EmployeeId");
            }
        });

        searchtextfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        pst = con.prepareStatement("select * from employee where salary <=?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        if (isOnlyDigits(searchtextfield.getText())){
                            pst.setString(1,searchtextfield.getText());
                            ResultSet rs = pst.executeQuery();
                            if(!rs.next()){
                                JOptionPane.showMessageDialog(null,"No record found");
                            }
                            else{
                                rs.previous();
                                table1.setModel(DbUtils.resultSetToTableModel(rs));
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Invalid input");
                        }
                        searchtextfield.setText("");

                    }catch (SQLException e3){
                        e3.printStackTrace();
                    }
                }
            }
        });

        salaryupdatefield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        pst = con.prepareStatement("UPDATE employee set salary = ? where id= ?");
                        if(isOnlyDigits(salaryupdatefield.getText()) && isOnlyDigits(idupdatefield.getText())){
                            pst.setString(1,salaryupdatefield.getText());
                            pst.setString(2,idupdatefield.getText());
                            int rs = pst.executeUpdate();
                            if(rs == 0){
                                JOptionPane.showMessageDialog(null,"Not updated, probably id not exists");
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"Update completed");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"invalid input");
                        }
                        table_load();
                        salaryupdatefield.setText("");
                        idupdatefield.setText("");
                    } catch(SQLException e4){
                        e4.printStackTrace();
                    }
                }
            }
        });

        idupdatefield.addKeyListener(new KeyAdapter() {
            //TODO: use the same keylistener for both textfields (idupdatefield & salaryupdatefield)
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER ){
                    try {
                        pst = con.prepareStatement("UPDATE employee set salary = ? where id= ?");
                        if(isOnlyDigits(salaryupdatefield.getText()) && isOnlyDigits(idupdatefield.getText())){
                            pst.setString(1,salaryupdatefield.getText());
                            pst.setString(2,idupdatefield.getText());
                            int rs = pst.executeUpdate();
                            if(rs == 0){
                                JOptionPane.showMessageDialog(null,"Not updated, probably id not exists");
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"Update completed");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"invalid input");
                        }
                        table_load();
                        salaryupdatefield.setText("");
                        idupdatefield.setText("");
                    } catch(SQLException e4){
                        e4.printStackTrace();
                    }
                }
            }
        });
    }

    private void createUIComponents() {
    }
}
