/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d;

import br.com.tipigao.motor3d.primitivas.Malha;
import br.com.tipigao.motor3d.primitivas.Triangulo;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Roberto
 */
public abstract class MotorJogo implements ActionListener {

    private EventListenerList actionListenerList;
    private int INTERVALO_ATUALIZACAO_MILISEGUNDOS = 30;
    private javax.swing.Timer temporizador;
    private long inicio;

    private JPanel pnlDesenho;
    private BufferedImage img;
    private Graphics2D g2dBufImg;

    protected java.awt.event.MouseEvent botaoMousePressionado;
    protected java.awt.event.MouseEvent cliqueLiberado;
    protected java.awt.event.MouseEvent cliqueMouse;
    protected java.awt.event.MouseEvent movimentoMouse;
    protected java.awt.event.MouseWheelEvent rodaMouse;

    private String frameRate;
    private long lastTime;
    private long delta;
    private int frameCount;
    private ArrayList<Triangulo> triangulos;

    boolean precisaRedimensionarImagem = false;

    abstract boolean aoCriar();

    abstract boolean aoAtualizar(long tempoDecorrido);

    public MotorJogo() {
        actionListenerList = new EventListenerList();
        triangulos = new ArrayList<Triangulo>();
    }

    public void setPainelDesenho(JPanel pnlDesenho) {
        this.pnlDesenho = pnlDesenho;
        img = new BufferedImage(pnlDesenho.getWidth(), pnlDesenho.getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2dBufImg = img.createGraphics();
    }

    public int getLargura() {
        return pnlDesenho.getWidth();
    }

    public int getAltura() {
        return pnlDesenho.getHeight();
    }

    public void limpaTela() {
        g2dBufImg.setColor(Color.black);
        g2dBufImg.fillRect(0, 0, pnlDesenho.getWidth(), pnlDesenho.getHeight());
    }

    public void atualizaValoresCena() {
        precisaRedimensionarImagem = true;
    }

    //Retirado de https://stackoverflow.com/questions/9417356/bufferedimage-resize
    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        g2dBufImg = dimg.createGraphics();
        g2dBufImg.drawImage(tmp, 0, 0, null);

        return dimg;
    }

    protected void addTriangulo(Triangulo tri) {
        triangulos.add(tri);
    }

    protected void limpaTriangulos() {
        triangulos.clear();
    }

    protected void desenhaTriangulos() {
        Collections.sort(triangulos, new Comparator<Triangulo>() {
            @Override
            public int compare(Triangulo t1, Triangulo t2) {
                var z1 = (t1.getP()[0].getZ() + t1.getP()[1].getZ() + t1.getP()[2].getZ()) / 3.0;
                var z2 = (t2.getP()[0].getZ() + t2.getP()[1].getZ() + t2.getP()[2].getZ()) / 3.0;
                return z1 == z2 ? 0 : z1 < z2 ? 1 : -1;
            }
        });
        
        for (Triangulo tri : triangulos) {
            desenhaTriangulo(tri, tri.getCor());
        }
    }

    protected void desenhaTriangulo(Triangulo tri, Color cor) {

        double p1x = tri.getP()[0].getX(), p1y = tri.getP()[0].getY(),
                p2x = tri.getP()[1].getX(), p2y = tri.getP()[1].getY(),
                p3x = tri.getP()[2].getX(), p3y = tri.getP()[2].getY();

        desenhaTriangulo(p1x, p1y, p2x, p2y, p3x, p3y, cor);
    }

    protected void desenhaTrianguloArame(double p1x, double p1y,
            double p2x, double p2y,
            double p3x, double p3y,
            Color cor) {

        g2dBufImg.setColor(cor);
        g2dBufImg.drawLine((int) p1x, (int) p1y, (int) p2x, (int) p2y);
        g2dBufImg.drawLine((int) p2x, (int) p2y, (int) p3x, (int) p3y);
        g2dBufImg.drawLine((int) p3x, (int) p3y, (int) p1x, (int) p1y);
    }

    protected void desenhaTriangulo(double p1x, double p1y,
            double p2x, double p2y,
            double p3x, double p3y,
            Color cor) {

        //Triângulo preenchido
        g2dBufImg.setColor(cor);
        g2dBufImg.fillPolygon(
                new int[]{(int) p1x, (int) p2x, (int) p3x},
                new int[]{(int) p1y, (int) p2y, (int) p3y},
                3);

        //Apenas linhas
        /*g.setColor(Color.black);
        g.drawLine((int)p1x, (int)p1y, (int)p2x, (int)p2y);
        g.drawLine((int)p2x, (int)p2y, (int)p3x, (int)p3y);
        g.drawLine((int)p3x, (int)p3y, (int)p1x, (int)p1y);
         */
    }

    public void inicializa() {
        aoCriar();

        inicio = System.currentTimeMillis();
        lastTime = System.currentTimeMillis();
        frameRate = "";

        temporizador = new javax.swing.Timer(INTERVALO_ATUALIZACAO_MILISEGUNDOS, this);
        temporizador.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (precisaRedimensionarImagem) {
            img = resize(img, pnlDesenho.getWidth(), pnlDesenho.getHeight());
            precisaRedimensionarImagem = false;
            return;
        }

        //Limpa a tela antes de fazer o novo desenho
        limpaTela();

        aoAtualizar(System.currentTimeMillis() - inicio);

        calculaFPS();
        g2dBufImg.setColor(Color.green);
        g2dBufImg.drawString(frameRate, 10, 20);

        Graphics2D g2dPnl = (Graphics2D) pnlDesenho.getGraphics();
        g2dPnl.drawImage(img, 0, 0, null);
    }

    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(ActionListener.class, actionListener);
    }

    public void removeActionListener(ActionListener actionListener) {
        actionListenerList.remove(ActionListener.class, actionListener);
    }

    public void finaliza() {
        //pararTemporizador();
    }

    public void mouseClicado(java.awt.event.MouseEvent evt) {
        cliqueMouse = evt;
    }

    public void mouseLiberado(java.awt.event.MouseEvent evt) {
        cliqueLiberado = evt;
    }

    public void mousePressionado(java.awt.event.MouseEvent evt) {
        botaoMousePressionado = evt;
    }

    public void mouseMovido(java.awt.event.MouseEvent evt) {
        movimentoMouse = evt;
    }

    public void mouseRodaMovida(java.awt.event.MouseWheelEvent evt) {
        rodaMouse = evt;
    }

    private String calculaFPS() {
        long current = System.currentTimeMillis();
        delta += current - lastTime;
        lastTime = current;
        frameCount++;

        if (delta > 1000) {
            delta -= 1000;
            frameRate = String.format("FPS %s", frameCount);
            frameCount = 0;
        }

        return frameRate;
    }

}
