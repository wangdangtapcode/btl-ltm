/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author quang
 */
public class WheatAndRice {

    private double x, y;
    private double width, height;
    private Color color;
    private boolean isDragging = false;
    private double xOffset, yOffset;

    public WheatAndRice(double width, double height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.x = 100; // vị trí ban đầu
        this.y = 100;
    }
    public void draw(Graphics2D g2d) {
        g2d.setColor(this.color);
        g2d.fillOval((int) (this.x - this.width / 2), (int) (this.y - this.height / 2), (int) this.width, (int) this.height);
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public boolean isDragging() {
        return isDragging;
    }

    public void startDragging(double xOffset, double yOffset) {
        this.isDragging = true;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void stopDragging() {
        this.isDragging = false;
    }
    public boolean contains(int px, int py) {
        double dx = px - x;
        double dy = py - y;
        // Kiểm tra điểm có nằm trong hình oval
        return (dx * dx) / (width * width / 4) + (dy * dy) / (height * height / 4) <= 1;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}