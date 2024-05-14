package com.example.bot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.example.bot.entity.User;
import net.dv8tion.jda.api.entities.Member;
import org.imgscalr.Scalr;

import static com.example.bot.BotApplication.guild;
import static com.example.bot.BotCommands.userRepository;

public class ImageGeneric {

    private int width = 2000;
    private int height = 1000;
    private int avatar = 600;
    private int nameW = 1960;
    private int nameH = 200;
    private int maxFontSize = 100;

    private Color colorPrimary;
    private Color colorSecondary;

    public ImageGeneric() {
    }

    public BufferedImage genericImage(Long userId) throws Exception {
        //жёлтый
        colorSecondary = Color.decode("#ff8c00");
        colorPrimary = Color.orange;
        //зелёный
        /*colorSecondary = Color.decode("#138708");
        colorPrimary = Color.decode("#20e80e");*/
        //красный
        /*colorSecondary = Color.decode("#b00000");
        colorPrimary = Color.decode("#ff4e33");*/
        //фиолетовый
        /*colorSecondary = Color.decode("#561394");
        colorPrimary = Color.decode("#8b00ff");*/

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        Member member = guild.getMemberById(userId);
        assert member != null;
        User user = userRepository.getUserById(userId);


        // Устанавливаем параметры сглаживания масштабирования
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //фон
        graphics.setColor(colorPrimary);
        graphics.fillRect(10, 10, width - 30, height - 40);
        graphics.setColor(colorSecondary);
        graphics.fillRect(30, 30, width - 20, height - 40);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(40, 40, width - 50, height - 60);

        // Создаем градиентную кисть
        GradientPaint gp = new GradientPaint(0, 0, colorSecondary, 0, 500, new Color(0, 0, 0, 0), true);
        // Устанавливаем эту кисть в Graphics2D
        graphics.setPaint(gp);
        // Рисуем прямоугольник
        graphics.fillRect(40, 500, width - 50, height - 520);
        graphics.setPaint(null);

        //тень имени
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font("Beef'd", Font.TYPE1_FONT, getFontSizeForName(member.getUser().getName())));
        graphics.drawString(member.getUser().getName(), 110, 225);
        //имя
        graphics.setColor(colorPrimary);
        graphics.drawString(member.getUser().getName(), 100, 220);

        //урвоень цифра тень
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font("Beef'd", Font.BOLD, maxFontSize - 20));
        graphics.drawString(String.valueOf(user.getLvl()), xForLvl(150, String.valueOf(user.getLvl())), 415);
        //уровень цифра
        graphics.setColor(colorPrimary);
        graphics.drawString(String.valueOf(user.getLvl()), xForLvl(140, String.valueOf(user.getLvl())), 410);
        //уровень обводка
        /*graphics.setColor(colorSecondary);
        graphics.fillRect(90, 420, 240, 520);*/
        //уровень фон
        graphics.setColor(colorPrimary);
        graphics.fillRect(100, 430, 220, 500);
        //уровень шкала
        graphics.setColor(colorSecondary);
        int h = hForLvlShcal(user, 460);
        graphics.fillRect(120, 450 + (460 - h), 180, h);
        //разграничители
        graphics.setColor(colorPrimary);
        graphics.fillRect(120, 780, 180, 20);
        graphics.fillRect(120, 670, 180, 20);
        graphics.fillRect(120, 575, 180, 20);
        graphics.fillRect(120, 490, 180, 20);

        //Линия разграничитель
        graphics.setColor(colorSecondary);
        graphics.fillRect(350, 330, 20, 600);

        //подложка под статистику

        graphics.setColor(new Color(0, 0, 0, 80));
        graphics.fillRect(400, 430, 850, 500);

        //иконки
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/microphone.png", colorSecondary), 150, 150), 430, 370, null);
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/message.png", colorSecondary), 130, 130), 440, 550, null);
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/coins.png", colorSecondary), 130, 130), 440, 730, null);
        graphics.setFont(new Font("Beef'd", Font.BOLD, maxFontSize - 40));
        /*graphics.setColor(colorSecondary);
        graphics.drawString(String.valueOf(user.getCoins()),610, 735);*/
        graphics.setColor(colorPrimary);
        graphics.drawString(user.getMinuteToString(),600, 480);
        graphics.drawString(String.valueOf(user.getMessage()),600, 650);
        graphics.drawString(String.valueOf(user.getCoins()),600, 835);

        //подложка под аватарка
        graphics.setColor(colorSecondary);
        graphics.fillOval(width - avatar - 110, height - avatar - 315, avatar + 50, avatar + 50);
        //graphics.setColor(Color.BLACK);
        //graphics.fillRect(width - avatar - 90, height - avatar - 350, avatar+50, avatar-400);
        //линия подводка имени
        graphics.setColor(colorSecondary);
        graphics.fillRect(40, 250, nameW, 20);
        //подложка под аватар
        graphics.setColor(colorPrimary);
        graphics.fillOval(width - avatar - 110, height - avatar - 305, avatar + 20, avatar + 20);


        //маска под изображение
        Ellipse2D.Double circle = new Ellipse2D.Double(width - avatar - 100, height - avatar - 295, avatar, avatar);

        graphics.setClip(circle);
        // Масштабируем и отображаем изображение в указанном прямоугольнике
        graphics.drawImage(getImage(member), width - avatar - 100, height - avatar - 295, null);


        //graphics.drawString(user.getId().toString(), 150, 100);
        graphics.dispose();
        return image;
    }

    public int hForLvlShcal(User user, int heightR) {
        int percentExp = (user.expOnLvl() * 100) / user.expToNextLvl(user.getLvl());
        int h = (heightR * percentExp) / 100;
        return h;
    }

    public int xForLvl(int x, String lvl) {
        if (lvl.length() == 2) return x - 20;
        if (lvl.length() == 3) return x - 55;
        return x;
    }

    public BufferedImage recolorIcon(String imagePath, Color color) throws IOException {
        // Считываем PNG изображение из файла
        File file = new File(imagePath);
        BufferedImage image = ImageIO.read(file);

        // Создаем копию изображения для изменения
        BufferedImage replacedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Проходим по каждому пикселю изображения и заменяем черный цвет на белый
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                if (rgb == Color.BLACK.getRGB()) {
                    replacedImage.setRGB(x, y, color.getRGB());
                } else {
                    replacedImage.setRGB(x, y, rgb);
                }
            }
        }
        return replacedImage;
    }

    public static BufferedImage colorImage(BufferedImage loadImg, Color color) {
        Graphics g = loadImg.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, loadImg.getWidth(), loadImg.getHeight());
        g.dispose();
        return loadImg;
    }

    public int getFontSizeForName(String name) {
        int fontSize = maxFontSize;
        if (name.length() < 5) return fontSize;
        if (name.length() < 10) return fontSize - 10;
        if (name.length() < 15) return fontSize - 30;
        if (name.length() < 20) return fontSize - 50;
        else return 50;
    }


    public Image getImage(Member member) {
        String imageUrl = member.getUser().getAvatar().getUrl(2048); // URL аватара пользователя Discord
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return resizeImage(bufferedImage, avatar, avatar);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }
}
