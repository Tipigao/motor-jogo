/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d.primitivas;

import java.awt.Color;

/**
 *
 * @author Roberto
 */
public class Triangulo {

    private Vetor3D[] p;
    private Color cor;

    public Triangulo() {

    }

    public Triangulo(Vetor3D[] p) {
        this.p = p;
    }

    public Triangulo(Vetor3D p1, Vetor3D p2, Vetor3D p3) {
        this.p = new Vetor3D[]{
            p1, p2, p3
        };
    }

    public Triangulo(double p1x, double p1y, double p1z, double p2x, double p2y, double p2z, double p3x, double p3y, double p3z) {
        this.p = new Vetor3D[]{
            new Vetor3D(p1x, p1y, p1z),
            new Vetor3D(p2x, p2y, p2z),
            new Vetor3D(p3x, p3y, p3z)
        };
    }

    public Vetor3D[] getP() {
        if (p == null) {
            p = new Vetor3D[]{
                new Vetor3D(),
                new Vetor3D(),
                new Vetor3D()
            };
        }
        return p;
    }

    public void setP(Vetor3D[] p) {
        this.p = p;
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    public void zerarPontos() {
        int tot = getP().length;
        for (int i = 0; i < tot; i++) {
            getP()[i].setX(0);
            getP()[i].setY(0);
            getP()[i].setZ(0);
        }

        Vetor3D normal = calculaNormal();
        normal.setX(0);
        normal.setY(0);
        normal.setZ(0);
    }

    public Vetor3D calculaNormal() {
        Vetor3D v = produtoCruzado(this);
        
        double x = v.getX(),
               y = v.getY(),
               z = v.getZ();
        
        double dist = Math.sqrt(x * x + y * y + z * z);

        x /= dist;
        y /= dist;
        z /= dist;

        Vetor3D normal = new Vetor3D(x, y, z);
        return normal;
    }

    public static Vetor3D produtoCruzado(Triangulo t) {
        double x1 = t.getP()[0].getX(),
               y1 = t.getP()[0].getY(),
               z1 = t.getP()[0].getZ(),
               x2 = t.getP()[1].getX(),
               y2 = t.getP()[1].getY(),
               z2 = t.getP()[1].getZ(),
               x3 = t.getP()[2].getX(),
               y3 = t.getP()[2].getY(),
               z3 = t.getP()[2].getZ();

        Vetor3D d1 = new Vetor3D( x2 - x1,   y2 - y1,   z2 - z1 );
        Vetor3D d2 = new Vetor3D( x3 - x1,   y3 - y1,   z3 - z1 ); 
        
        double x = d1.getY() * d2.getZ()   -   d1.getZ() * d2.getY();
        double y = d1.getZ() * d2.getX()   -   d1.getX() * d2.getZ();
        double z = d1.getX() * d2.getY()   -   d1.getY() * d2.getX();
        
        Vetor3D v = new Vetor3D(x, y, z);
        
        return v;
    }

    @Override
    public String toString() {
        var p1 = this.getP()[0];
        var p2 = this.getP()[1];
        var p3 = this.getP()[2];
        return String.format("0: %s; 1: %s; 2: %s;", p1.toString(), p2.toString(), p3.toString());
    }

}
