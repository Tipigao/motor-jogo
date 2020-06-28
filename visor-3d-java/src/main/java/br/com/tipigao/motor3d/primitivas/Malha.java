/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d.primitivas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roberto
 */
public class Malha {
    
    private ArrayList<Triangulo> triangulos;
    
    public ArrayList<Triangulo> getTriangulos(){
        if(triangulos==null){
            triangulos = new ArrayList<>();
        }
        return triangulos;
    }
    
    public void setTriangulos(ArrayList<Triangulo> t){
        this.triangulos = t;
    }
    
    public static Malha carregaArquivoObj(String arquivo){
        Malha m = new Malha();
        
        try(BufferedReader br = new BufferedReader(new FileReader(arquivo))){
            ArrayList<Vetor3D> vertices = new ArrayList<Vetor3D>();
            ArrayList<Triangulo> faces = new ArrayList<Triangulo>();
            
            while(br.ready()){
                String ln = br.readLine();
                if(ln.startsWith("v")){
                    String[] v = ln.split(" ");
                    Vetor3D vet = new Vetor3D(Float.parseFloat(v[1]), Float.parseFloat(v[2]), Float.parseFloat(v[3]));
                    vertices.add(vet);
                }else if(ln.startsWith("f")){
                    String[] f = ln.split(" ");
                    int i1 = Integer.parseInt(f[1]) - 1,
                        i2 = Integer.parseInt(f[2]) - 1,
                        i3 = Integer.parseInt(f[3]) - 1;
                    
                    //if(Math.max(i1, Math.max(i2, i3)) < vertices.size()){
                    Triangulo t = new Triangulo();
                    t.setP(new Vetor3D[]{
                        vertices.get(i1),
                        vertices.get(i2),
                        vertices.get(i3)
                    });
                    faces.add(t);
                }
            }
            m.setTriangulos(faces);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Malha.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Malha.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }
}
