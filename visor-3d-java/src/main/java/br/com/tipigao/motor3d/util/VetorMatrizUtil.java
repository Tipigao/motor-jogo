/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d.util;

import br.com.tipigao.motor3d.primitivas.Matriz4x4;
import br.com.tipigao.motor3d.primitivas.Vetor3D;

/**
 *
 * @author Roberto
 */
public class VetorMatrizUtil {
    
    public static void MultiplicaVetorMatriz(Vetor3D i, Vetor3D o, Matriz4x4 m)
    {
        double x = i.getX() * m.getM()[0][0] + i.getY() * m.getM()[1][0] + i.getZ() * m.getM()[2][0] + m.getM()[3][0];
        double y = i.getX() * m.getM()[0][1] + i.getY() * m.getM()[1][1] + i.getZ() * m.getM()[2][1] + m.getM()[3][1];
        double z = i.getX() * m.getM()[0][2] + i.getY() * m.getM()[1][2] + i.getZ() * m.getM()[2][2] + m.getM()[3][2];
        double w = i.getX() * m.getM()[0][3] + i.getY() * m.getM()[1][3] + i.getZ() * m.getM()[2][3] + m.getM()[3][3];

        if (w != 0) {
            x /= w;
            y /= w;
            z /= w;
        }
        
        o.setX(x);
        o.setY(y);
        o.setZ(z);
    }
    
}
