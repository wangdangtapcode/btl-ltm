/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import static javax.swing.TransferHandler.MOVE;

public class OvalPanel extends JPanel {

    private double xOffset = 0;
    private double yOffset = 0;
    private WheatAndRice[] arr;
    private WheatAndRice dragged;
    private ArrayList<WheatAndRice> grains;
    private WheatAndRice selectedGrain;

    public OvalPanel(int width, int height, ArrayList<WheatAndRice> grains) {
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        this.grains = grains;
        int size = grains.size();
        // Tính toán bán kính
        int ovalWidth = width - 6;  // Trừ viền
        int ovalHeight = height - 6; // Trừ viền
        for (int i = 0; i < size / 2; i++) {
            // Hạt gạo màu trắng
            setRandomPosition(grains.get(i), ovalWidth, ovalHeight);
        }

        for (int i = size / 2; i < size; i++) {
            // Hạt thóc màu vàng
            setRandomPosition(grains.get(i), ovalWidth, ovalHeight);
        }
        setTransferHandler(new GrainTransferHandler());
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (WheatAndRice grain : grains) {
                    if (grain.contains(e.getX(), e.getY())) {
                        selectedGrain = grain;
                        TransferHandler handler = getTransferHandler();
                        handler.exportAsDrag(OvalPanel.this, e, TransferHandler.MOVE);
                        break;
                    }
                }
            }
        });
    }

    public double getxOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public WheatAndRice[] getArr() {
        return arr;
    }

    public void setArr(WheatAndRice[] arr) {
        this.arr = arr;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Bật khử răng cưa để hình oval mịn hơn
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Đặt màu nền
        g2d.setColor(new Color(210, 180, 140));
        g2d.fillOval(0, 0, getWidth() - 6, getHeight() - 6); // Vẽ hình oval đầy đủ

        // Đặt màu viền
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawOval(0, 0, getWidth() - 6, getHeight() - 6); // Vẽ viền của hình oval

        // Draw rice-shaped objects
        for (WheatAndRice rice : grains) {
            g2d.setColor(rice.getColor());
            // Vẽ hình elip (gạo/trấu đều có hình giống nhau, chỉ khác màu)
            g2d.fillOval((int) (rice.getX() - rice.getWidth() / 2), (int) (rice.getY() - rice.getHeight() / 2),
                    (int) rice.getWidth(), (int) rice.getHeight());
        }
    }
// Phương thức đặt vị trí ngẫu nhiên cho đối tượng trong oval

    public void setRandomPosition(WheatAndRice rice, int ovalWidth, int ovalHeight) {
        double x, y;
        do {
            // Sinh tọa độ ngẫu nhiên
            x = Math.random() * ovalWidth;
            y = Math.random() * ovalHeight;
        } while (!isInsideOval(x, y, ovalWidth - 40, ovalHeight - 40));

        // Cập nhật tọa độ cho đối tượng
        rice.setX(x);
        rice.setY(y);
        handleCollision(rice);
    }

// Phương thức kiểm tra tọa độ có nằm trong oval hay không
    public boolean isInsideOval(double x, double y, int ovalWidth, int ovalHeight) {

        // Tính toán tọa độ trung tâm của oval
        double centerX = ovalWidth / 2.0;
        double centerY = ovalHeight / 2.0;

        // Kiểm tra xem tọa độ (x, y) có nằm trong oval không
        return Math.pow((x - centerX) / (ovalWidth / 2.0), 2) + Math.pow((y - centerY) / (ovalHeight / 2.0), 2) <= 1;
    }

    public void bringToFront(WheatAndRice rice) {
        // Move the dragged rice shape to the end of the riceShapes array
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == rice) {
                WheatAndRice temp = arr[i];
                System.arraycopy(arr, i + 1, arr, i, arr.length - 1 - i);
                arr[arr.length - 1] = temp; // Place it at the end
                break;
            }
        }
    }

    // Handle collision between dragged rice shape and other shapes
    public void handleCollision(WheatAndRice draggedRice) {
        for (WheatAndRice rice : grains) {
            if (rice != draggedRice && rice != null) {
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

    public boolean isInsideShape(int mouseX, int mouseY, WheatAndRice rice) {
        double distance = Math.sqrt(Math.pow(mouseX - rice.getX(), 2) + Math.pow(mouseY - rice.getY(), 2));
        return distance <= rice.getWidth() / 2;  // Sử dụng chiều rộng để xác định phạm vi
    }

    public WheatAndRice getDragged() {
        return dragged;
    }

    public void setDragged(WheatAndRice dragged) {
        this.dragged = dragged;
    }

    public void clearDragged() {
        dragged = null;
    }

    private static class GrainTransferable implements Transferable {

        private WheatAndRice grain;

        public GrainTransferable(WheatAndRice grain) {
            this.grain = grain;
        }

        public Object getTransferData(DataFlavor flavor) {
            return grain;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{new DataFlavor(WheatAndRice.class, "WheatAndRice")};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.getRepresentationClass() == WheatAndRice.class;
        }
    }

    private static class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonColor = new JsonObject();
            jsonColor.addProperty("r", src.getRed());
            jsonColor.addProperty("g", src.getGreen());
            jsonColor.addProperty("b", src.getBlue());
            jsonColor.addProperty("alpha", src.getAlpha());
            return jsonColor;
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonColor = json.getAsJsonObject();
            int r = jsonColor.get("r").getAsInt();
            int g = jsonColor.get("g").getAsInt();
            int b = jsonColor.get("b").getAsInt();
            int alpha = jsonColor.get("alpha").getAsInt();
            return new Color(r, g, b, alpha);
        }
    }

    private class GrainTransferHandler extends TransferHandler {

        protected Transferable createTransferable(JComponent c) {
            return new GrainTransferable(selectedGrain);
        }

        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            // Chỉ xóa đối tượng khỏi OvalPanel nếu thao tác kéo thả thành công
            if (action == MOVE) {
                grains.remove(selectedGrain);
                repaint();
            }
            selectedGrain = null; // Reset lựa chọn
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter()) // Đăng ký TypeAdapter cho Color
                .setPrettyPrinting() // Đảm bảo JSON có định dạng đẹp
                .create();

        return gson.toJson(this.grains); // Tuần tự hóa chỉ danh sách grains
    }

    public static OvalPanel fromJson(String json) {
        // In ra chuỗi JSON nhận được để kiểm tra
        System.out.println("Received JSON: " + json);

        // Loại bỏ tất cả khoảng trắng thừa ở giữa các đối tượng JSON
        // Cách này chỉ cần thiết nếu khoảng trắng trong JSON gây vấn đề
        json = json.replaceAll("\\s+", "");  // Loại bỏ tất cả khoảng trắng dư thừa

        // Sau khi loại bỏ khoảng trắng, bạn có thể kiểm tra lại chuỗi JSON
        System.out.println("Normalized JSON: " + json);

        // Khởi tạo Gson với custom type adapter cho đối tượng Color
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .create();

        // Phân tích chuỗi JSON thành đối tượng JsonElement
        JsonElement element = JsonParser.parseString(json);

        // Kiểm tra xem dữ liệu có phải là một mảng hay không
        if (!element.isJsonArray()) {
            throw new IllegalArgumentException("Invalid JSON format: Expected an array.");
        }

        // Chuyển JsonElement thành ArrayList<WheatAndRice>
        ArrayList<WheatAndRice> grains = gson.fromJson(element, new TypeToken<ArrayList<WheatAndRice>>() {
        }.getType());

        // Tạo và trả về đối tượng OvalPanel với danh sách grains
        return new OvalPanel(grains);
    }
    public OvalPanel(ArrayList<WheatAndRice> grains) {
        int width = 685;
        int height = 419;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);
        this.grains = grains;
        setTransferHandler(new GrainTransferHandler());
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (WheatAndRice grain : grains) {
                    if (grain.contains(e.getX(), e.getY())) {
                        selectedGrain = grain;
                        TransferHandler handler = getTransferHandler();
                        handler.exportAsDrag(OvalPanel.this, e, TransferHandler.MOVE);
                        break;
                    }
                }
            }
        });
    }
    public ArrayList<WheatAndRice> getGrains() {
        return grains;
    }

    public void setGrains(ArrayList<WheatAndRice> grains) {
        this.grains = grains;
    }

}
