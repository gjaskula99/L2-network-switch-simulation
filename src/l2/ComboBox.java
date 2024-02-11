package l2;
import javax.swing.*;    
import java.awt.event.*;    
public class ComboBox {    
JFrame f;    
ComboBox(){    
    f=new JFrame("ComboBox Example");   
    final JLabel label = new JLabel();          
    label.setHorizontalAlignment(JLabel.CENTER);  
    label.setSize(400,100);  
    JButton b=new JButton("Show");  
    b.setBounds(200,100,75,20);  
    String Data[]={"Wykres_1","Wykres_2","Wkres_3","Wykres_4","Wykres_5"};        
    final JComboBox cb=new JComboBox(Data);    
    cb.setBounds(50, 100,90,20);    
    f.add(cb); f.add(label); f.add(b);    
    f.setLayout(null);    
    f.setSize(350,350);    
    f.setVisible(true);       
    b.addActionListener(new ActionListener() {  
       public void actionPerformed(ActionEvent e) {       
String data = "Programming language Selected: "   
   + cb.getItemAt(cb.getSelectedIndex());  
label.setText(data);  
}  
});           
}    
public static void main(String[] args) {    
    new ComboBox();         
}    
}     