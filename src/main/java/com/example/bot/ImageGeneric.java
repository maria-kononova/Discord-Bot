package com.example.bot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.imgscalr.Scalr;

import static com.example.bot.BotApplication.guild;

public class ImageGeneric {

    private int width = 2000;
    private int height = 1000;
    private int avatar = 600;
    private int nameW = 1960;
    private int nameH = 200;
    private int maxFontSize = 100;

    public ImageGeneric() {
    }

    public BufferedImage genericImage(Long userId) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        Member member = guild.getMemberById(userId);
        assert member != null;

        // Устанавливаем параметры сглаживания масштабирования
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //фон
        graphics.setColor(Color.orange);
        graphics.fillRect(10, 10, width - 30, height - 40);
        graphics.setColor(Color.decode("#ff8c00"));
        graphics.fillRect(30, 30, width - 20, height - 40);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(40, 40, width - 50, height - 60);

        //тень имени
        graphics.setColor(Color.decode("#ff8c00"));
        graphics.setFont(new Font("Beef'd", Font.TYPE1_FONT, getFontSizeForName(member.getUser().getName())));
        graphics.drawString(member.getUser().getName(), 110, 225);
        //имя
        graphics.setColor(Color.orange);
        graphics.setFont(new Font("Beef'd", Font.TYPE1_FONT, getFontSizeForName(member.getUser().getName())));
        graphics.drawString(member.getUser().getName(), 100, 220);


        //подложка под аватарка
        graphics.setColor(Color.decode("#ff8c00"));
        graphics.fillOval(width - avatar - 110, height - avatar - 315, avatar + 50, avatar + 50);
        //graphics.setColor(Color.BLACK);
        //graphics.fillRect(width - avatar - 90, height - avatar - 350, avatar+50, avatar-400);
        //линия подводка имени
        graphics.setColor(Color.decode("#ff8c00"));
        graphics.fillRect(40, 250, nameW, 20);
        //подложка под аватар
        graphics.setColor(Color.orange);
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

    public int getFontSizeForName(String name){
        int fontSize = maxFontSize;
        if (name.length() < 5) return fontSize;
        if(name.length() < 10) return  fontSize - 10;
        if(name.length() < 15) return fontSize - 30;
        if(name.length() < 20) return fontSize - 50;
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
