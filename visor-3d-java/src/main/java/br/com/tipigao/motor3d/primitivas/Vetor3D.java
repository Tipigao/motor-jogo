/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d.primitivas;

/**
 *
 * @author Roberto
 */
public class Vetor3D {
    private double x, y, z;
    
    public Vetor3D(){ }
    
    public Vetor3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX(){ return x;}
    public void setX(double x){ this.x = x;}
    
    public double getY(){ return y;}
    public void setY(double y){ this.y = y;}
    
    public double getZ(){ return z;}
    public void setZ(double z){ this.z = z;}
    
    @Override
    public String toString(){
        return String.format("{x: %f, y: %f, z: %f}", x, y, z);
    }
}
