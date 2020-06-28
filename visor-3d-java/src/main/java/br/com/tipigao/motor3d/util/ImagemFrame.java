/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d.util;

import java.awt.image.BufferedImage;

/**
 *
 * @author Roberto
 */
public class ImagemFrame {
    private int largura;
    private int altura;
    private double[] profundidadePixels;
    private BufferedImage img;
    
    public ImagemFrame(int largura, int altura){
        profundidadePixels = new double[largura * altura];
        img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
    }
    
    public int getLargura(){
        return largura;
    }
    
    public int getAltura(){
        return altura;
    }
    
    public BufferedImage getBuffImg(){
        return img;
    }
    
    
}
