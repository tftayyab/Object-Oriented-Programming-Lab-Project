import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class WavePanel extends JPanel {
    private float volume = 0f;
    private long lastTime = System.nanoTime();
    private java.util.List<Ripple> ripples = new ArrayList<>();
    private float hueShift = 0f;
    
    public WavePanel() {
        setBackground(new Color(25, 25, 35)); // Dark background for better contrast
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (volume > 0.5f) {
            ripples.add(new Ripple(getWidth() / 2, getHeight() / 2));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2f)); // Thicker lines for better visibility

        int width = getWidth();
        int height = getHeight();
        int waveHeight = (int) (volume * 50);

        long time = System.nanoTime();
        double t = (time - lastTime) / 1_000_000_000.0;
        hueShift = (float)((t * 0.1) % 1.0); // Smooth color cycling

        // Draw background glow
        drawBackgroundGlow(g2, width, height);

        // Enhanced layered sine waves
        drawWaves(g2, width, height, waveHeight, t);

        // Improved radial visualization
        drawRadialVisualization(g2, width, height, t);

        // Enhanced ripple effect
        drawRipples(g2);
    }

    private void drawBackgroundGlow(Graphics2D g2, int width, int height) {
        RadialGradientPaint gradient = new RadialGradientPaint(
            width/2, height/2, Math.max(width, height)/2,
            new float[]{0.0f, 1.0f},
            new Color[]{
                new Color(40, 40, 60, 100),
                new Color(25, 25, 35, 0)
            }
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);
    }

    private void drawWaves(Graphics2D g2, int width, int height, int waveHeight, double t) {
        for (int x = 0; x < width; x++) {
            int baseY = height / 2;
            int y1 = (int) (baseY + waveHeight * Math.sin((x * 0.03) + t * 4));
            int y2 = (int) (baseY + (waveHeight / 1.5) * Math.sin((x * 0.04) + t * 5 + Math.PI / 2));
            int y3 = (int) (baseY + (waveHeight / 2.5) * Math.sin((x * 0.05) + t * 6 + Math.PI));

            Color c1 = Color.getHSBColor((hueShift + 0.5f) % 1.0f, 0.8f, 0.9f);
            Color c2 = Color.getHSBColor((hueShift + 0.7f) % 1.0f, 0.9f, 0.8f);
            
            g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 120));
            g2.drawLine(x, y1, x, y2);
            g2.setColor(new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 100));
            g2.drawLine(x, y2, x, y3);
        }
    }

    private void drawRadialVisualization(Graphics2D g2, int width, int height, double t) {
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (int)(40 + volume * 10);

        for (int i = 0; i < 360; i += 6) {
            double angle = Math.toRadians(i);
            double volumeFactor = Math.sin(t * 2 + i * 0.05);
            int lineLength = (int)(volume * volumeFactor * 25);

            int x1 = (int)(centerX + radius * Math.cos(angle));
            int y1 = (int)(centerY + radius * Math.sin(angle));
            int x2 = (int)(centerX + (radius + lineLength) * Math.cos(angle));
            int y2 = (int)(centerY + (radius + lineLength) * Math.sin(angle));

            Color color = Color.getHSBColor((float)(i) / 360f + hueShift, 0.8f, 1.0f);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawRipples(Graphics2D g2) {
        Iterator<Ripple> iterator = ripples.iterator();
        while (iterator.hasNext()) {
            Ripple r = iterator.next();
            r.update();
            if (r.alpha <= 0) {
                iterator.remove();
                continue;
            }
            Color rippleColor = Color.getHSBColor(hueShift, 0.8f, 1.0f);
            g2.setColor(new Color(rippleColor.getRed(), rippleColor.getGreen(), 
                                rippleColor.getBlue(), r.alpha));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(r.x - r.radius, r.y - r.radius, r.radius * 2, r.radius * 2);
        }
    }

    static class Ripple {
        int x, y;
        int radius = 10;
        int alpha = 150;

        public Ripple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void update() {
            radius += 3;
            alpha -= 3; // Slower fade for smoother animation
        }
    }
}