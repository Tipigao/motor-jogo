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
public class Matriz4x4 {
    private double[][] m;
    
    public double[][] getM(){
        if(m==null){
            m = new double[4][4];
        }
        return m;
    }
    
    public void setM(double[][] m){
        this.m = m;
    }
    
    @Override
    public String toString(){
        var m = this.getM();
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < m.length; i++) {
            sb.append(String.format("[%f, %f, %f, %f]; ", m[i][0], m[i][1], m[i][2], m[i][3]));
        }
        
        return sb.toString();
    }
}
