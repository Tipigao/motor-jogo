/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.tipigao.motor3d;

import br.com.tipigao.motor3d.primitivas.Malha;
import br.com.tipigao.motor3d.primitivas.Matriz4x4;
import br.com.tipigao.motor3d.primitivas.ObjetoCena;
import br.com.tipigao.motor3d.primitivas.Triangulo;
import br.com.tipigao.motor3d.primitivas.Vetor3D;
import br.com.tipigao.motor3d.util.VetorMatrizUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author Roberto
 */
public class Visor3D extends MotorJogo {

    private ArrayList<Malha> malhas;

    private double distanciaZ;
    private Vetor3D camera;
    private Matriz4x4 matProjecao, matRotacaoX, matRotacaoY, matRotacaoZ;
    private PontoMouse pontoMouse, pontoMouseAnterior;
    private boolean bAtivaRotacao;

    public Visor3D() {
        malhas = new ArrayList<Malha>();
        camera = new Vetor3D();
        distanciaZ = 6.0f;
        pontoMouse = new PontoMouse();
        pontoMouseAnterior = new PontoMouse();
    }

    @Override
    boolean aoCriar() {

        Malha malha;

        malha = Malha.carregaArquivoObj("D:\\Desenvolvimento\\ApacheServer\\mini-app\\visor-3d\\macaco.obj");
        //malha = criaCubo();

        malhas.add(malha);

        atualizaMatrizProjecao();
        atualizaMatrizRotacaoX(0);
        atualizaMatrizRotacaoY(0);
        atualizaMatrizRotacaoZ(0);

        return true;
    }

    @Override
    boolean aoAtualizar(long tempoDecorrido) {

        ArrayList<ObjetoCena> objs = aplicaTransformacoes(malhas);

        limpaTriangulos();
        
        for (ObjetoCena o : objs) {
            Malha mlh = o.getMalha();

            for (Triangulo tri : mlh.getTriangulos()) {
                addTriangulo(tri);
            }
        }

        desenhaTriangulos();
        
        return true;
    }

    private Malha criaCubo() {
        Malha cubo = new Malha();

        ArrayList<Triangulo> t = new ArrayList<Triangulo>();

        //FRENTE
        t.add(new Triangulo(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f));
        t.add(new Triangulo(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f));

        //DIREITA
        t.add(new Triangulo(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f));
        t.add(new Triangulo(1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f));

        //TRAS
        t.add(new Triangulo(1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f));
        t.add(new Triangulo(1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f));

        //ESQUERDA
        t.add(new Triangulo(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f));
        t.add(new Triangulo(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f));

        // TOPO
        t.add(new Triangulo(0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f));
        t.add(new Triangulo(0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f));

        // BAIXO
        t.add(new Triangulo(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        t.add(new Triangulo(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));

        cubo.setTriangulos(t);

        return cubo;
    }

    private ArrayList<ObjetoCena> aplicaTransformacoes(ArrayList<Malha> malhasRef) {

        ArrayList<ObjetoCena> malhasTransformadas = new ArrayList<ObjetoCena>();

        //Luz
        Vetor3D luzDirecao = new Vetor3D(0f, 0f, -1f);

        for (int i = 0; i < malhasRef.size(); i++) {
            Malha malha = malhasRef.get(i);

            for (int j = 0; j < malha.getTriangulos().size(); j++) {
                Triangulo tri = malha.getTriangulos().get(j);

                Triangulo triProjetado = new Triangulo();

                for (int k = 0; k < tri.getP().length; k++) {
                    Vetor3D p = tri.getP()[k];
                    triProjetado.getP()[k].setX(p.getX());
                    triProjetado.getP()[k].setY(p.getY());
                    triProjetado.getP()[k].setZ(p.getZ());
                }

                //Rotaciona no eixo Y
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[0], triProjetado.getP()[0], matRotacaoY);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[1], triProjetado.getP()[1], matRotacaoY);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[2], triProjetado.getP()[2], matRotacaoY);

                //Rotaciona no eixo Z
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[0], triProjetado.getP()[0], matRotacaoZ);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[1], triProjetado.getP()[1], matRotacaoZ);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[2], triProjetado.getP()[2], matRotacaoZ);

                //Rotaciona no eixo X
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[0], triProjetado.getP()[0], matRotacaoX);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[1], triProjetado.getP()[1], matRotacaoX);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[2], triProjetado.getP()[2], matRotacaoX);

                //Afasta o objeto pra não ficar dentro da área da matriz de projeção
                triProjetado.getP()[0].setZ(triProjetado.getP()[0].getZ() + distanciaZ);
                triProjetado.getP()[1].setZ(triProjetado.getP()[1].getZ() + distanciaZ);
                triProjetado.getP()[2].setZ(triProjetado.getP()[2].getZ() + distanciaZ);

                Vetor3D normal = triProjetado.calculaNormal();

                if (normal.getX() * (triProjetado.getP()[0].getX() - camera.getX())
                        + normal.getY() * (triProjetado.getP()[0].getY() - camera.getY())
                        + normal.getZ() * (triProjetado.getP()[0].getZ() - camera.getZ()) >= 0) {
                    //if(normal.getZ() >= 0){
                    continue;
                }

                //Aplica a matriz de projeção 2D
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[0], triProjetado.getP()[0], matProjecao);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[1], triProjetado.getP()[1], matProjecao);
                VetorMatrizUtil.MultiplicaVetorMatriz(triProjetado.getP()[2], triProjetado.getP()[2], matProjecao);

                //Escala para o tamanho da tela
                triProjetado.getP()[0].setX(triProjetado.getP()[0].getX() + 1.0);
                triProjetado.getP()[0].setY(triProjetado.getP()[0].getY() + 1.0);

                triProjetado.getP()[1].setX(triProjetado.getP()[1].getX() + 1.0);
                triProjetado.getP()[1].setY(triProjetado.getP()[1].getY() + 1.0);

                triProjetado.getP()[2].setX(triProjetado.getP()[2].getX() + 1.0);
                triProjetado.getP()[2].setY(triProjetado.getP()[2].getY() + 1.0);

                triProjetado.getP()[0].setX(triProjetado.getP()[0].getX() * 0.5 * (double) getLargura());
                triProjetado.getP()[0].setY(triProjetado.getP()[0].getY() * 0.5 * (double) getAltura());

                triProjetado.getP()[1].setX(triProjetado.getP()[1].getX() * 0.5 * (double) getLargura());
                triProjetado.getP()[1].setY(triProjetado.getP()[1].getY() * 0.5 * (double) getAltura());

                triProjetado.getP()[2].setX(triProjetado.getP()[2].getX() * 0.5 * (double) getLargura());
                triProjetado.getP()[2].setY(triProjetado.getP()[2].getY() * 0.5 * (double) getAltura());

                double d = Math.sqrt(luzDirecao.getX() * luzDirecao.getX() + luzDirecao.getY() * luzDirecao.getY() + luzDirecao.getZ() * luzDirecao.getZ());
                luzDirecao.setX(luzDirecao.getX() / d);
                luzDirecao.setY(luzDirecao.getY() / d);
                luzDirecao.setZ(luzDirecao.getZ() / d);

                double dp = normal.getX() * luzDirecao.getX() + normal.getY() * luzDirecao.getY() + normal.getZ() * luzDirecao.getZ();

                int vCor = (int) Math.round(Math.abs(dp * 255.0));

                Color cor = new Color(vCor, vCor, vCor);

                triProjetado.setCor(cor);

                Malha m = new Malha();
                m.getTriangulos().add(triProjetado);

                var o = new ObjetoCena();
                o.setMalha(m);

                malhasTransformadas.add(o);
            }

        }

        return malhasTransformadas;
    }

    private void atualizaMatrizRotacaoX(double angulo) {

        double radianos = angulo * Math.PI / 180;

        matRotacaoX = matRotacaoX == null ? new Matriz4x4() : matRotacaoX;

        matRotacaoX.getM()[0][0] = 1.0f;
        matRotacaoX.getM()[1][1] = Math.cos(radianos);
        matRotacaoX.getM()[1][2] = Math.sin(radianos);
        matRotacaoX.getM()[2][1] = -Math.sin(radianos);
        matRotacaoX.getM()[2][2] = Math.cos(radianos);
        matRotacaoX.getM()[3][3] = 1.0f;
    }

    private void atualizaMatrizRotacaoY(double angulo) {
        double radianos = angulo * Math.PI / 180;

        matRotacaoY = matRotacaoY == null ? new Matriz4x4() : matRotacaoY;
        matRotacaoY.getM()[0][0] = Math.cos(radianos);
        matRotacaoY.getM()[0][2] = -Math.sin(radianos);
        matRotacaoY.getM()[1][1] = 1.0f;
        matRotacaoY.getM()[2][0] = Math.sin(radianos);
        matRotacaoY.getM()[2][2] = Math.cos(radianos);
        matRotacaoY.getM()[3][3] = 1.0f;
    }

    private void atualizaMatrizRotacaoZ(double angulo) {
        double radianos = angulo * Math.PI / 180;

        matRotacaoZ = matRotacaoZ == null ? new Matriz4x4() : matRotacaoZ;
        matRotacaoZ.getM()[0][0] = Math.cos(radianos);
        matRotacaoZ.getM()[0][1] = Math.sin(radianos);
        matRotacaoZ.getM()[1][0] = -Math.sin(radianos);
        matRotacaoZ.getM()[1][1] = Math.cos(radianos);
        matRotacaoZ.getM()[2][2] = 1.0f;
        matRotacaoZ.getM()[3][3] = 1.0f;
    }

    private void atualizaMatrizProjecao() {
        //Matriz de projeção
        //perto ou longe
        double fPerto = 0.1f;
        double fLonge = 1000.0f;
        double fFov = 90.0f;
        double fProporcaoTela = getAltura() / (double) getLargura();
        double fFovRad = 1.0f / Math.tan(fFov * 0.5f / 180.0f * Math.PI);

        matProjecao = new Matriz4x4();
        matProjecao.getM()[0][0] = fProporcaoTela * fFovRad;
        matProjecao.getM()[1][1] = fFovRad;
        matProjecao.getM()[2][2] = fLonge / (double) (fLonge - fPerto);
        matProjecao.getM()[3][2] = (-fLonge * fPerto) / (double) (fLonge - fPerto);
        matProjecao.getM()[2][3] = 1.0f;
        matProjecao.getM()[3][3] = 0.0f;
    }

    @Override
    public void mousePressionado(java.awt.event.MouseEvent evt) {
        super.mousePressionado(evt);

        bAtivaRotacao = evt.getButton() == MouseEvent.BUTTON1;
    }

    @Override
    public void mouseLiberado(java.awt.event.MouseEvent evt) {
        super.mouseLiberado(evt);

        bAtivaRotacao = evt.getButton() == MouseEvent.BUTTON1 ? false : bAtivaRotacao;

        pontoMouseAnterior.x = evt.getX();
        pontoMouseAnterior.y = evt.getY();
    }

    @Override
    public void mouseMovido(java.awt.event.MouseEvent evt) {
        super.mouseMovido(evt);

        if (bAtivaRotacao) {

            int x = evt.getX();
            int y = evt.getY();

            int difX = (x - pontoMouseAnterior.x);
            int difY = (y - pontoMouseAnterior.y);

            if (difX > 0) {
                pontoMouse.x += 1;
            } else if (difX < 0) {
                pontoMouse.x -= 1;
            }

            if (difY > 0) {
                pontoMouse.y += 1;
            } else if (difY < 0) {
                pontoMouse.y -= 1;
            }

            pontoMouseAnterior.x = x;
            pontoMouseAnterior.y = y;

            double vRotX = pontoMouse.x;
            double vRotY = pontoMouse.y;

            atualizaMatrizRotacaoY(vRotX);
            atualizaMatrizRotacaoX(vRotY);
        }
    }

    @Override
    public void mouseRodaMovida(java.awt.event.MouseWheelEvent evt) {
        rodaMouse = evt;
        this.distanciaZ += evt.getPreciseWheelRotation();
    }

    @Override
    public void atualizaValoresCena() {
        super.atualizaValoresCena();
        atualizaMatrizProjecao();
    }

    class PontoMouse {

        public int x;
        public int y;
    }

}
