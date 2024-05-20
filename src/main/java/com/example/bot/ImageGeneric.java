package com.example.bot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import com.example.bot.entity.Event;
import com.example.bot.entity.EventType;
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

    private Color colorPrimary;
    private Color colorSecondary;

    private String font1 = "Beef'd";
    private String font2 = "Minecraftia";

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
        GradientPaint gp = new GradientPaint(0, 0, new Color(colorSecondary.getRed(), colorSecondary.getGreen(), colorSecondary.getBlue(), 80), 0, 500, new Color(0, 0, 0, 0), true);
        // Устанавливаем эту кисть в Graphics2D
        graphics.setPaint(gp);
        // Рисуем прямоугольник
        graphics.fillRect(40, 500, width - 50, height - 520);
        graphics.setPaint(null);

        //тень имени
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font(font1, Font.TYPE1_FONT, getFontSizeForName(100, member.getEffectiveName())));
        graphics.drawString(member.getEffectiveName(), 110, 225);
        //имя
        graphics.setColor(colorPrimary);
        graphics.drawString(member.getEffectiveName(), 100, 220);

        //урвоень цифра тень
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font(font1, Font.BOLD, 100 - 20));
        graphics.drawString(String.valueOf(user.getLvl()), xForLvl(160, String.valueOf(user.getLvl())), 415);
        //уровень цифра
        graphics.setColor(colorPrimary);
        graphics.drawString(String.valueOf(user.getLvl()), xForLvl(150, String.valueOf(user.getLvl())), 410);

        //подложка под статистику
        graphics.setColor(new Color(0, 0, 0, 80));
        graphics.fillRect(400, 430, 850, 500);

        //подложка под рейтинг
        graphics.fillRect(width - 650, height - 250, 530, 180);

        //топ надпись
        String rating = String.valueOf(user.ratingUser(userRepository.findAll(), user));
        graphics.setColor(colorSecondary);
        graphics.drawString(rating, width - 360, height - 95);
        graphics.setColor(colorPrimary);
        graphics.drawString(rating, width - 370, height - 100);
        //топ иконка
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/top.png", colorSecondary), 250, 250), width - 640, height - 280, null);

        //уровень фон
        graphics.setColor(colorPrimary);
        graphics.fillRect(100, 430, 220, 500);
        //уровень градиент внутри
        //graphics.setColor(colorSecondary.darker());
        GradientPaint gp2 = new GradientPaint(0, 0, new Color(colorSecondary.getRed(), colorSecondary.getGreen(), colorSecondary.getBlue(), 100), 0, 500, colorPrimary, true);
        graphics.setPaint(gp2);
        graphics.fillRect(120, 450, 180, 460);
        graphics.setPaint(null);
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


        //иконки
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/microphone.png", colorSecondary), 150, 150), 430, 370, null);
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/message.png", colorSecondary), 130, 130), 440, 550, null);
        graphics.drawImage(resizeImage(recolorIcon("src/main/resources/static/coins.png", colorSecondary), 130, 130), 440, 730, null);
        graphics.setFont(new Font(font1, Font.BOLD, 100 - 40));
        /*graphics.setColor(colorSecondary);
        graphics.drawString(String.valueOf(user.getCoins()),610, 735);*/
        //статистика
        graphics.setColor(colorPrimary);
        graphics.drawString(user.getMinuteToString(), 600, 480);
        graphics.drawString(String.valueOf(user.getMessage()), 600, 650);
        graphics.drawString(String.valueOf(user.getCoins()), 600, 835);

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


    public BufferedImage genericImageEvent(EventType event) throws Exception {
        colorSecondary = Color.decode(event.getColor());
        colorPrimary = colorSecondary.brighter();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        // Устанавливаем параметры сглаживания масштабирования
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //фон
        graphics.setColor(colorPrimary);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(colorSecondary);
        graphics.fillRect(20, 20, width - 20, height);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(40, 40, width - 50, height - 60);
        /*int layer = 0;
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int a = random.nextInt(15, 50);
            int r = random.nextInt(0, 10);
            if (r > 5) graphics.setColor(colorPrimary);
            else graphics.setColor(colorSecondary);
            graphics.fillRect(random.nextInt(40, width - 80), random.nextInt(40, height - 80), a, a);
            *//*if (layer == 50) {
                graphics.setColor(new Color(0, 0, 0, 90));
                graphics.fillRect(40, 40, width - 50, height - 60);
            }
            layer++;*//*
        }*/

        //фон изображения
        graphics.drawImage(recolorImageForEvent("src/main/resources/static/eventfon.png", colorSecondary, colorPrimary), 10, 10, null);
        graphics.setColor(new Color(0, 0, 0, 90));
        graphics.fillRect(40, 40, width - 50, height - 60);
        // Создаем градиентную кисть
        GradientPaint gp = new GradientPaint(0, 0, new Color(colorSecondary.getRed(), colorSecondary.getGreen(), colorSecondary.getBlue(), 80), 0, 500, new Color(0, 0, 0, 0), true);
        // Устанавливаем эту кисть в Graphics2D
        graphics.setPaint(gp);
        // Рисуем прямоугольник
        graphics.fillRect(40, 500, width - 50, height - 520);
        graphics.setPaint(null);

        //тень имени
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font(font2, Font.TYPE1_FONT, getFontSizeForNameEvent(250, event.getName())));
        graphics.drawString(event.getName(), 580 - getXMinusForNameEvent(event.getName()), 710);
        //имя
        graphics.setColor(colorPrimary);
        graphics.drawString(event.getName(), 570 - getXMinusForNameEvent(event.getName()), 700);

        /*event.setName("Расписание");
        //тень имени
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font(font2, Font.TYPE1_FONT, getFontSizeForNameEvent(200, event.getName())));
        graphics.drawString(event.getName(), 810 - getXMinusForNameEvent(event.getName()), 580);
        //имя
        graphics.setColor(colorPrimary);
        graphics.drawString(event.getName(), 800 - getXMinusForNameEvent(event.getName()), 570);

        event.setName("Мероприятий");
        //тень имени
        graphics.setColor(colorSecondary);
        graphics.setFont(new Font(font2, Font.TYPE1_FONT, getFontSizeForNameEvent(230, event.getName())));
        graphics.drawString(event.getName(), 580 - getXMinusForNameEvent(event.getName()), 790);
        //имя
        graphics.setColor(colorPrimary);
        graphics.drawString(event.getName(), 570 - getXMinusForNameEvent(event.getName()), 780);*/

        graphics.setColor(colorSecondary);
        graphics.fillRect(580 - getXMinusForNameEvent(event.getName()), 620, 900 + getPlusWidthEventLine(event.getName()) , 20);
        graphics.fillRect(540 - getXMinusForNameEvent(event.getName()), 660, 1000 + getPlusWidthEventLine(event.getName()), 25);
        graphics.setColor(colorPrimary);
        graphics.fillRect(570 - getXMinusForNameEvent(event.getName()), 610, 900 +  getPlusWidthEventLine(event.getName()), 20);
        graphics.fillRect(530 - getXMinusForNameEvent(event.getName()), 650, 1000 + getPlusWidthEventLine(event.getName()), 25);

        //graphics.drawString(user.getId().toString(), 150, 100);
        graphics.dispose();
        return image;
    }

    public int getPlusWidthEventLine(String name){
        int size = getFontSizeForNameEvent(250, name);
        if(name.length() <=5 ) return 0;
        if(name.length() <=6 ) return (name.length() - 5) * 100;
        if(name.length() <= 10) return (name.length() - 5) * 140;
        if(name.length() <= 15) return (name.length() - 5) * 100;
        if(name.length() <= 20) return (name.length() - 5) * 60;
        return (name.length() - 5) * 90;
    }

    public int getXMinusForNameEvent(String name) {
        if (name.length() <= 5) return 0;
        if (name.length() <= 10) return (name.length() - 5) * 70;
        if (name.length() <= 15) return (name.length() - 5) * 50;
        return (name.length() - 5) * 30;

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

    public BufferedImage recolorImageForEvent(String imagePath, Color color1, Color color2) throws IOException {
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
                    replacedImage.setRGB(x, y, color1.getRGB());
                }
            }
        }
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                if (rgb == Color.decode("#fafafa").getRGB() || rgb == Color.decode("#ffffff").getRGB() ||
                rgb == Color.decode("#9f9f9").getRGB()) {
                    replacedImage.setRGB(x, y, color2.getRGB());
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

    public int getFontSizeForName(int size, String name) {
        if (name.length() < 5) return size;
        if (name.length() < 10) return size - 10;
        if (name.length() < 15) return size - 30;
        if (name.length() < 20) return size - 50;
        else return 50;
    }

    public int getFontSizeForNameEvent(int size, String name) {
        if (name.length() < 5) return size;
        if (name.length() < 10) return size - 30;
        if (name.length() < 15) return size - 50;
        if (name.length() < 20) return size - 70;
        else return 100;
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
