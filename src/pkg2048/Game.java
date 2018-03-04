/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Dul mu'in
 */
public class Game extends Canvas implements Runnable, KeyListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final double MAX_FPS = 60D;
    private JFrame f;
    private boolean isRunning = false;
    private BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
    private int[][] grid;
    private int size;
    private Font font = new Font("Serif", Font.PLAIN, 40);
    private boolean isWin = false;
    private int score = 0;

    public Game() {
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));

        INIT();
        f = new JFrame("2048 by Myf");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.pack();
        f.addKeyListener(this);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        start();
    }

    public void INIT() {
        grid = new int[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        /* 
            j menunjukan x
            i menunjukan y
         */
        size = WIDTH / 4;
        createNum();
        createNum();
    }

    public void drawGrid(Graphics2D g) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                g.drawRect(i * size, j * size, size, size);
                if (grid[i][j] != 0) {
                    g.setFont(font);
                    drawFont(grid[i][j] + "", size, size, g, i, j);
                }
                score = grid[i][j] > score ? grid[i][j] : score;
                if (grid[i][j] == 2048 && !isWin) {
                    isWin = true;
                    JOptionPane.showMessageDialog(null, "You Won");
                }
            }
        }
    }

    public void drawFont(String s, int w, int h, Graphics2D g, int i, int j) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2 + i * w;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2) + j * h;
        g.drawString(s, x, y);
    }

    @Override
    public void run() {
        final double nsPerUpdate = 1000000000D / MAX_FPS;

        long lastTime = System.nanoTime();
        long frameCounter = System.currentTimeMillis();
        double unporcessedTime = 0;
        int frames = 0;
        int updates = 0;

        while (isRunning) {
            long currentTime = System.nanoTime();
            long passTime = currentTime - lastTime;
            lastTime = currentTime;
            unporcessedTime += passTime;

            if (unporcessedTime >= nsPerUpdate) { //MAX UPDATE
                unporcessedTime = 0;
                update();
                updates++;
            }

            render(); //RENDER
            frames++;

            if (System.currentTimeMillis() - frameCounter >= 1000) {
                System.out.println("FPS : " + frames + " Update/Tick : " + updates);
                frameCounter += 1000;
                frames = 0;
                updates = 0;
            }
        }
        dispose();
    }

    public void createNum() {
        int value = Math.random() > 0.5 ? 4 : 2;
        ArrayList<Point_> p = new ArrayList<Point_>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    p.add(new Point_(i, j));
                }
            }
        }

        if (p.size() == 0) {
            JOptionPane.showMessageDialog(null, "Your Max Score is "+score);
            stop();
        } else {
            int r = (int) (Math.random() * p.size());
            int x = p.get(r).x;
            int y = p.get(r).y;
            grid[x][y] = value;
        }

    }

    public void slide(char d) {
        switch (d) {
            case 'd':
                for (int i = 0; i < grid.length; i++) {
                    for (int j = grid[i].length - 1; j >= 0; j--) {
                        if (grid[i][j] == 0) {
                            int ii = i;
                            int jj = j;
                            while (grid[ii][jj] == 0) {
                                jj--;
                                if (jj == -1) {
                                    jj = 0;
                                    break;
                                }
                            }
                            grid[i][j] = grid[ii][jj];
                            grid[ii][jj] = 0;
                        }
                    }
                }
                break;
            case 'u':
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 0) {
                            int ii = i;
                            int jj = j;
                            while (grid[ii][jj] == 0) {
                                jj++;
                                if (jj == grid[i].length) {
                                    jj = grid[i].length - 1;
                                    break;
                                }
                            }
                            grid[i][j] = grid[ii][jj];
                            grid[ii][jj] = 0;
                        }
                    }
                }
                break;
            case 'l':
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 0) {
                            int ii = i;
                            int jj = j;
                            while (grid[ii][jj] == 0) {
                                ii++;
                                if (ii == grid[i].length) {
                                    ii = grid[i].length - 1;
                                    break;
                                }
                            }
                            grid[i][j] = grid[ii][jj];
                            grid[ii][jj] = 0;
                        }
                    }
                }
                break;
            default:
                for (int i = grid.length - 1; i >= 0; i--) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == 0) {
                            int ii = i;
                            int jj = j;
                            while (grid[ii][jj] == 0) {
                                ii--;
                                if (ii == -1) {
                                    ii = 0;
                                    break;
                                }
                            }
                            grid[i][j] = grid[ii][jj];
                            grid[ii][jj] = 0;
                        }
                    }
                }
                break;
        }
    }

    public void combine(char d) {
        switch (d) {
            case 'd':
                for (int i = 0; i < grid.length; i++) {
                    for (int j = grid[i].length - 1; j >= 1; j--) {
                        if (grid[i][j] == grid[i][j - 1]) {
                            grid[i][j] += grid[i][j - 1];
                            grid[i][j - 1] = 0;
                        }
                    }
                }
                break;
            case 'u':
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length - 1; j++) {
                        if (grid[i][j] == grid[i][j + 1]) {
                            grid[i][j] += grid[i][j + 1];
                            grid[i][j + 1] = 0;
                        }
                    }
                }
                break;
            case 'l':
                for (int i = 0; i < grid.length - 1; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == grid[i + 1][j]) {
                            grid[i][j] += grid[i + 1][j];
                            grid[i + 1][j] = 0;
                        }
                    }
                }
                break;
            default:
                for (int i = grid.length - 1; i >= 1; i--) {
                    for (int j = 0; j < grid[i].length; j++) {
                        if (grid[i][j] == grid[i - 1][j]) {
                            grid[i][j] += grid[i - 1][j];
                            grid[i - 1][j] = 0;
                        }
                    }
                }
                break;
        }
    }

    public void update() {
        //There is no update for this game
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        /* DRAW IMAGES */
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(Color.black);
        drawGrid(g2);
        /* END OF DRAW IMAGES */

        bs.show();
    }

    public void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
    }

    public void dispose() {
        System.exit(0);
    }

    public int RANDOM(int a, int b) {
        return (int) (a + Math.random() * (b - a));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                slide('u');
                combine('u');
                slide('u');
                createNum();
                break;
            case KeyEvent.VK_A:
                slide('l');
                combine('l');
                slide('l');
                createNum();
                break;
            case KeyEvent.VK_S:
                slide('d');
                combine('d');
                slide('d');
                createNum();
                break;
            case KeyEvent.VK_D:
                slide('r');
                combine('r');
                slide('r');
                createNum();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
