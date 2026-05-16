package Servlets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * CaptchaServlet
 * Generates a distorted image-based CAPTCHA and stores the answer in the session.
 * Mapped to: /CaptchaServlet
 *
 * Authentication & Security — CAPTCHA implementation (image-based, with refresh button)
 */
public class CaptchaServlet extends HttpServlet {

    private static final int    WIDTH          = 190;
    private static final int    HEIGHT         = 52;
    private static final int    CAPTCHA_LENGTH = 5;
    private static final String CHARS          = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    // I, O, 0, 1 excluded to avoid visual ambiguity

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Generate random CAPTCHA text ──────────────────────────────
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        String captchaText = sb.toString();

        // ── Store in session (compared case-insensitively on submit) ──
        HttpSession session = request.getSession(true);
        session.setAttribute("captchaAnswer", captchaText);

        // ── Build image ───────────────────────────────────────────────
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D    g2d   = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background gradient-like fill
        g2d.setColor(new Color(235, 240, 248));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Noise lines
        for (int i = 0; i < 8; i++) {
            g2d.setColor(new Color(
                160 + random.nextInt(80),
                160 + random.nextInt(80),
                160 + random.nextInt(80)
            ));
            g2d.drawLine(
                random.nextInt(WIDTH), random.nextInt(HEIGHT),
                random.nextInt(WIDTH), random.nextInt(HEIGHT)
            );
        }

        // Noise dots
        for (int i = 0; i < 50; i++) {
            g2d.setColor(new Color(
                random.nextInt(200),
                random.nextInt(200),
                random.nextInt(200)
            ));
            g2d.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 3, 3);
        }

        // Draw each character with jitter and slight rotation
        int x = 14;
        for (int i = 0; i < captchaText.length(); i++) {
            // Pick a dark colour for readability
            g2d.setColor(new Color(
                random.nextInt(90),
                random.nextInt(90),
                random.nextInt(90)
            ));
            int fontSize = 24 + random.nextInt(7);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));

            // Vertical jitter
            int y = 36 + random.nextInt(8) - 4;
            g2d.drawString(String.valueOf(captchaText.charAt(i)), x, y);
            x += 30 + random.nextInt(6);
        }

        g2d.dispose();

        // ── Prevent caching so Refresh always fetches a new one ───────
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        ImageIO.write(image, "png", response.getOutputStream());
    }
}
