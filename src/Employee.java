import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Employee {
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

    //main function that sets the frame visible
    public static void main(String[] args) {
        JFrame frame = new JFrame("Employee");
        frame.setContentPane(new Employee().Main);
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

    public Employee() {
        connect();
        table_load();
        saveButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String empname,salary,mobile;

                empname = nametextfield.getText();
                salary = salarytextfield.getText();
                mobile = mobiletextfield.getText();

                try {
                    //TODO:control the input variable
                    pst = con.prepareStatement("insert into employee(empname,salary,mobile) values (?,?,?)");
                    pst.setString(1,empname);
                    pst.setString(2,salary);
                    pst.setString(3,mobile);
                    pst.executeUpdate();
                    table_load();
                    JOptionPane.showMessageDialog(null,"Record added");
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
                    //TODO:control the input variable
                    pst = con.prepareStatement("select * from employee where salary <=?");
                    if (isOnlyDigits(searchtextfield.getText())){
                        pst.setString(1,searchtextfield.getText());
                        ResultSet rs = pst.executeQuery();
                        if(rs.next() == false){
                            //TODO:fix this part, handle multiple null resultset
                            JOptionPane.showMessageDialog(null,"No record found");
                        }
                        else{
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
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //TODO:control the input variable
                    pst = con.prepareStatement("UPDATE employee set salary = ? where id= ?");
                    pst.setString(1,salaryupdatefield.getText());
                    pst.setString(2,idupdatefield.getText());
                    pst.executeUpdate();
                    table_load();
                    salaryupdatefield.setText("");
                    idupdatefield.setText("");
                    JOptionPane.showMessageDialog(null,"Update completed");

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
                    pst.setString(1,deletetextfield.getText());
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Deletion completed");
                    table_load();
                    deletetextfield.setText("");
                }
                catch(SQLException e5){
                    e5.printStackTrace();
                }
            }
        });
    }

    private void createUIComponents() {
    }
}
