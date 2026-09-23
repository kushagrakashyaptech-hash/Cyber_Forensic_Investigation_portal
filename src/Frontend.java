import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class Frontend extends JFrame {
    private final Backend backend = new Backend();

    public Frontend() { showLogin(); }

    private void showLogin() {
        setTitle("Cyber Forensic Investigation Portal");
        setSize(430,280); setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p=new JPanel(new GridLayout(4,2,10,12));
        p.setBorder(BorderFactory.createEmptyBorder(30,35,30,35));
        JTextField user=new JTextField();
        JPasswordField pass=new JPasswordField();
        JButton login=new JButton("LOGIN");

        p.add(new JLabel("Username:")); p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);
        p.add(new JLabel()); p.add(login);
        p.add(new JLabel()); p.add(new JLabel("Demo: admin / admin123"));

        JLabel title=new JLabel("CYBER FORENSIC PORTAL",SwingConstants.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,20));
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        root.add(title,BorderLayout.NORTH); root.add(p,BorderLayout.CENTER);
        setContentPane(root);

        login.addActionListener(e->{
            try{
                if(backend.login(user.getText().trim(),new String(pass.getPassword())))
                    dashboard(user.getText().trim());
                else JOptionPane.showMessageDialog(this,"Invalid username or password.");
            }catch(Exception ex){ error(ex); }
        });
    }

    private void dashboard(String username) {
        setTitle("Cyber Forensic Portal - Dashboard");
        setSize(650,400); setLocationRelativeTo(null);

        JPanel root=new JPanel(new BorderLayout(15,15));
        root.setBorder(BorderFactory.createEmptyBorder(25,30,25,30));
        JLabel title=new JLabel("CYBER FORENSIC INVESTIGATION PORTAL");
        title.setFont(new Font("Arial",Font.BOLD,21));
        JLabel welcome=new JLabel("Welcome, "+username);

        JPanel top=new JPanel(new BorderLayout());
        top.add(title,BorderLayout.NORTH); top.add(welcome,BorderLayout.SOUTH);

        JPanel buttons=new JPanel(new GridLayout(2,2,15,15));
        JButton cases=new JButton("CASE MANAGEMENT");
        JButton evidence=new JButton("EVIDENCE MANAGEMENT");
        JButton timeline=new JButton("TIMELINE");
        JButton reports=new JButton("REPORTS");
        buttons.add(cases); buttons.add(evidence); buttons.add(timeline); buttons.add(reports);

        root.add(top,BorderLayout.NORTH); root.add(buttons,BorderLayout.CENTER);
        root.add(new JLabel("First Review Prototype | Java Swing + JDBC + MySQL",SwingConstants.CENTER),BorderLayout.SOUTH);
        setContentPane(root);

        cases.addActionListener(e->caseWindow());
        evidence.addActionListener(e->evidenceWindow());
        timeline.addActionListener(e->JOptionPane.showMessageDialog(this,"Timeline: planned for next phase."));
        reports.addActionListener(e->JOptionPane.showMessageDialog(this,"Reports: planned for next phase."));
        revalidate(); repaint();
    }

    private void caseWindow() {
        JFrame f=new JFrame("Case Management"); f.setSize(820,500); f.setLocationRelativeTo(this);
        JPanel root=new JPanel(new BorderLayout(10,10)); root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel form=new JPanel(new GridLayout(5,2,8,8));
        JTextField no=new JTextField(), title=new JTextField(), inv=new JTextField(), desc=new JTextField();
        JButton add=new JButton("ADD CASE");
        form.add(new JLabel("Case Number:")); form.add(no);
        form.add(new JLabel("Case Title:")); form.add(title);
        form.add(new JLabel("Investigator:")); form.add(inv);
        form.add(new JLabel("Description:")); form.add(desc);
        form.add(new JLabel()); form.add(add);

        DefaultTableModel m=new DefaultTableModel(new String[]{"Case Number","Title","Investigator","Status"},0);
        JTable table=new JTable(m); JButton refresh=new JButton("REFRESH");
        JPanel top=new JPanel(new BorderLayout()); top.add(form,BorderLayout.CENTER); top.add(refresh,BorderLayout.SOUTH);
        root.add(top,BorderLayout.NORTH); root.add(new JScrollPane(table),BorderLayout.CENTER);

        Runnable load=()->{try{m.setRowCount(0);for(String[] r:backend.getCases())m.addRow(r);}catch(Exception ex){error(ex);}};
        add.addActionListener(e->{try{
            if(no.getText().isBlank()||title.getText().isBlank()||inv.getText().isBlank())throw new Exception("Case number, title and investigator are required.");
            backend.addCase(new Backend.CaseRecord(no.getText().trim(),title.getText().trim(),inv.getText().trim(),desc.getText().trim(),"Open"));
            JOptionPane.showMessageDialog(f,"Case added successfully."); no.setText("");title.setText("");inv.setText("");desc.setText("");load.run();
        }catch(Exception ex){error(ex);}});
        refresh.addActionListener(e->load.run());
        f.setContentPane(root); f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); f.setVisible(true); load.run();
    }

    private void evidenceWindow() {
        JFrame f=new JFrame("Evidence Management"); f.setSize(1000,550); f.setLocationRelativeTo(this);
        JPanel root=new JPanel(new BorderLayout(10,10)); root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel form=new JPanel(new GridLayout(5,2,8,8));
        JTextField cn=new JTextField(), name=new JTextField(), file=new JTextField(); file.setEditable(false);
        JComboBox<String> type=new JComboBox<>(new String[]{"Image","Document","Video","Audio","Log","Other"});
        JButton choose=new JButton("CHOOSE FILE"), add=new JButton("ADD EVIDENCE");
        JPanel fp=new JPanel(new BorderLayout(5,0)); fp.add(file,BorderLayout.CENTER); fp.add(choose,BorderLayout.EAST);
        form.add(new JLabel("Case Number:"));form.add(cn);
        form.add(new JLabel("Evidence Name:"));form.add(name);
        form.add(new JLabel("Evidence Type:"));form.add(type);
        form.add(new JLabel("Evidence File:"));form.add(fp);
        form.add(new JLabel());form.add(add);

        DefaultTableModel m=new DefaultTableModel(new String[]{"Case","Evidence","Type","SHA-256","Status"},0);
        JTable table=new JTable(m); JButton refresh=new JButton("REFRESH");
        JPanel top=new JPanel(new BorderLayout());top.add(form,BorderLayout.CENTER);top.add(refresh,BorderLayout.SOUTH);
        root.add(top,BorderLayout.NORTH);root.add(new JScrollPane(table),BorderLayout.CENTER);

        choose.addActionListener(e->{JFileChooser c=new JFileChooser();if(c.showOpenDialog(f)==JFileChooser.APPROVE_OPTION){File x=c.getSelectedFile();file.setText(x.getAbsolutePath());if(name.getText().isBlank())name.setText(x.getName());}});
        Runnable load=()->{try{m.setRowCount(0);for(String[] r:backend.getEvidence()){if(r[3].length()>18)r[3]=r[3].substring(0,18)+"...";m.addRow(r);}}catch(Exception ex){error(ex);}};
        add.addActionListener(e->{try{
            if(file.getText().isBlank())throw new Exception("Choose an evidence file first.");
            String hash=backend.sha256(file.getText());
            backend.addEvidence(new Backend.EvidenceRecord(cn.getText().trim(),name.getText().trim(),String.valueOf(type.getSelectedItem()),file.getText().trim(),hash,"Verified"));
            JOptionPane.showMessageDialog(f,"Evidence saved!\n\nSHA-256:\n"+hash);
            cn.setText("");name.setText("");file.setText("");load.run();
        }catch(Exception ex){error(ex);}});
        refresh.addActionListener(e->load.run());
        f.setContentPane(root);f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);f.setVisible(true);load.run();
    }

    private void error(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}

    public static void main(String[] args){SwingUtilities.invokeLater(()->new Frontend().setVisible(true));}
}
