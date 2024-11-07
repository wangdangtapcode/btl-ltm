/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

public class CirclePanel extends JPanel {

    private int radius;
    private ArrayList<WheatAndRice> grains;

    public CirclePanel(int radius) {
        this.radius = radius;
        this.grains = new ArrayList<>();
        setPreferredSize(new Dimension(radius, radius));
        setLayout(null);
        setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(new DataFlavor(WheatAndRice.class, "WheatAndRice"));
            }

            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                try {
                    WheatAndRice grain = (WheatAndRice) support.getTransferable().getTransferData(new DataFlavor(WheatAndRice.class, "WheatAndRice"));
                    addWheatAndRice(grain);
                    setRandomPosition(grain,radius-6);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }

    public void addWheatAndRice(WheatAndRice rice) {
        grains.add(rice); // Thêm đối tượng vào danh sách
        repaint(); // Vẽ lại panel để hiển thị đối tượng mới
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(210, 180, 140));
        g2d.fillOval(0, 0, radius - 6, radius - 6);

        g2d.setColor(new Color(139, 69, 19));
        g2d.drawOval(0, 0, radius - 6, radius - 6);

        // Vẽ tất cả các đối tượng `WheatAndRice` trong danh sách
        for (WheatAndRice rice : grains) {
            g2d.setColor(rice.getColor());
            g2d.fillOval((int) (rice.getX() - rice.getWidth() / 2),
                    (int) (rice.getY() - rice.getHeight() / 2),
                    (int) rice.getWidth(), (int) rice.getHeight());
        }
    }

    public void setRandomPosition(WheatAndRice rice, int diameter) {
        double x, y;
        double radius = diameter / 2.0;
        do {
            // Sinh tọa độ ngẫu nhiên trong hình vuông bao quanh hình tròn
            x = Math.random() * diameter;
            y = Math.random() * diameter;
        } while (!isInsideCircle(x, y, radius-6));

        // Cập nhật tọa độ cho đối tượng
        rice.setX(x);
        rice.setY(y);
        handleCollision(rice);
    }

// Phương thức kiểm tra tọa độ có nằm trong hình tròn hay không
    public boolean isInsideCircle(double x, double y, double radius) {
        // Tính toán tọa độ trung tâm của hình tròn
        double centerX = radius;
        double centerY = radius;

        // Kiểm tra xem tọa độ (x, y) có nằm trong hình tròn không
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }
    public void handleCollision(WheatAndRice draggedRice) {
        for (WheatAndRice rice : grains) {
            if (rice != draggedRice && rice!=null) {
                double distance = Math.sqrt(Math.pow(draggedRice.getX() - rice.getX(), 2) + Math.pow(draggedRice.getY() - rice.getY(), 2));
                double minDistance = (draggedRice.getWidth() + rice.getWidth()) / 4;

                if (distance < minDistance) {
                    // Điều chỉnh vị trí của hạt để tránh chồng lên nhau
                    double angle = Math.atan2(draggedRice.getY() - rice.getY(), draggedRice.getX() - rice.getX());
                    draggedRice.setX(rice.getX() + Math.cos(angle) * minDistance);
                    draggedRice.setY(rice.getY() + Math.sin(angle) * minDistance);
                }
            }
        }
    }

    public ArrayList<WheatAndRice> getGrains() {
        return grains;
    }
}

