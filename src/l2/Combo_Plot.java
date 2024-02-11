package l2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Combo_Plot {
    JFrame f;

    Combo_Plot() {
        f = new JFrame("ComboBox Example");
        final JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setSize(400, 100);
        JButton b = new JButton("Show");
        b.setBounds(200, 100, 75, 20);
        String Data[] = {"Wykres_1", "Wykres_2", "Wykres_3", "Wykres_4", "Wykres_5"};
        final JComboBox cb = new JComboBox(Data);
        cb.setBounds(50, 100, 90, 20);
        f.add(cb);
        f.add(label);
        f.add(b);
        f.setLayout(null);
        f.setSize(350, 350);
        f.setVisible(true);

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = "Selected Plot: " + cb.getItemAt(cb.getSelectedIndex());
                label.setText(data);
                String plot = cb.getItemAt(cb.getSelectedIndex()).toString();
                if (plot.equals("Wykres_1")) {
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.add(new Plots());
                    frame.setSize(600, 400);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } else if (plot.equals("Wykres_2")) {
                    // KOD TWORZĄCY DRUGI WYKRES
                } else if (plot.equals("Wykres_3")) {
                    // KOD TWORZĄCY TRZECI WYKRES
                } else if (plot.equals("Wykres_4")) {
                    // KOD TWORZĄCY CZWARTY WYKRES
                } else if (plot.equals("Wykres_5")) {
                    // KOD TWORZĄCY PIĄTY WYKRES
                }
            }
        });
    }

    public static void main(String[] args) {
        new Combo_Plot();
    }
}

class Plots extends JPanel {
    int[] cord = {65, 20, 40, 80};
    int marg = 60;

    protected void paintComponent(Graphics grf) {
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D) grf;
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        graph.draw(new Line2D.Double(marg, marg, marg, height - marg));
        graph.draw(new Line2D.Double(marg, height - marg, width - marg, height - marg));

        double x = (double) (width - 2 * marg) / (cord.length - 1);
        double scale = (double) (height - 2 * marg) / getMax();

        graph.setPaint(Color.RED);

        for (int i = 0; i < cord.length; i++) {
            double x1 = marg + i * x;
            double y1 = height - marg - scale * cord[i];
            graph.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
        }
    }

    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for (int i = 0; i < cord.length; i++) {
            if (cord[i] > max)
                max = cord[i];
        }
        return max;
    }
}