function Cena3D(areaDesenho) {
    var self = this;
    var canvas = areaDesenho;
    var ctx = canvas.getContext("2d");
    var malhas = [];
    var theta;
    var matProjecao, matRotacaoX, matRotacaoY, matRotacaoZ;
    var camera;
    var imgDados = null,
        bufTri = [];


    this.distanciaZ = 6;

    this.fov = 90.0;

    this.inicializa = function() {
        var cubo = self.criaMalhaCubo();
        malhas.push(cubo);

        atualizaMatrizProjecao();
        atualizaMatrizRotacaoX(0);
        atualizaMatrizRotacaoY(0);
        atualizaMatrizRotacaoZ(0);

        camera = { x: 0.0, y: 0.0, z: 0.0 };

        ctx.imageSmoothingEnabled = false;
    };

    var larguraTela = function() { return canvas.width; };
    var alturaTela = function() { return canvas.height; };

    var multiplicaVetorMatriz = function(i, o, m) {
        var x = i.x * m[0][0] + i.y * m[1][0] + i.z * m[2][0] + m[3][0];
        var y = i.x * m[0][1] + i.y * m[1][1] + i.z * m[2][1] + m[3][1];
        var z = i.x * m[0][2] + i.y * m[1][2] + i.z * m[2][2] + m[3][2];
        var w = i.x * m[0][3] + i.y * m[1][3] + i.z * m[2][3] + m[3][3];

        if (w != 0) {
            x /= w;
            y /= w;
            z /= w;
        }

        o.x = x;
        o.y = y;
        o.z = z;
    }

    var atualizaMatrizRotacaoX = function(vTheta) {
        matRotacaoX = matRotacaoX || [
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0]
        ];
        matRotacaoX[0][0] = 1.0;
        matRotacaoX[1][1] = Math.cos(vTheta);
        matRotacaoX[1][2] = Math.sin(vTheta);
        matRotacaoX[2][1] = -Math.sin(vTheta);
        matRotacaoX[2][2] = Math.cos(vTheta);
        matRotacaoX[3][3] = 1.0;
    };

    var atualizaMatrizRotacaoY = function(vTheta) {
        matRotacaoY = matRotacaoY || [
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0]
        ];
        matRotacaoY[0][0] = Math.cos(vTheta);
        matRotacaoY[0][2] = -Math.sin(vTheta);
        matRotacaoY[1][1] = 1.0;
        matRotacaoY[2][0] = Math.sin(vTheta);
        matRotacaoY[2][2] = Math.cos(vTheta);
        matRotacaoY[3][3] = 1.0;
    };

    var atualizaMatrizRotacaoZ = function(vTheta) {
        matRotacaoZ = matRotacaoZ || [
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0]
        ];
        matRotacaoZ[0][0] = Math.cos(vTheta);
        matRotacaoZ[0][1] = Math.sin(vTheta);
        matRotacaoZ[1][0] = -Math.sin(vTheta);
        matRotacaoZ[1][1] = Math.cos(vTheta);
        matRotacaoZ[2][2] = 1.0;
        matRotacaoZ[3][3] = 1.0;
    };

    var atualizaMatrizProjecao = function() {
        var perto = 0.1;
        var longe = 1000.0;
        var fFov = self.fov;
        var proporcaoTela = alturaTela() / larguraTela();
        var fFovRad = 1.0 / Math.tan(fFov * 0.5 / 180.0 * Math.PI);

        matProjecao = matProjecao || [
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0],
            [0, 0, 0, 0]
        ];

        matProjecao[0][0] = proporcaoTela * fFovRad;
        matProjecao[1][1] = fFovRad;
        matProjecao[2][2] = longe / (longe - perto);
        matProjecao[3][2] = (-longe * perto) / (longe - perto);
        matProjecao[2][3] = 1.0;
        matProjecao[3][3] = 0.0;
    };

    var limpaTela = function() {
        //ctx.clearRect(0, 0, larguraTela(), alturaTela());
        ctx.fillStyle = 'black';
        ctx.fillRect(0, 0, larguraTela(), alturaTela());
    };

    var desenhaTriangulo = function(p1x, p1y,
        p2x, p2y,
        p3x, p3y,
        cor) {

        ctx.fillStyle = cor;

        ctx.beginPath();

        ctx.moveTo(p1x, p1y);
        ctx.lineTo(p2x, p2y);
        ctx.lineTo(p3x, p3y);
        ctx.lineTo(p1x, p1y);

        ctx.fill();


        // ctx.lineWidth = 0.75;

        // ctx.beginPath();

        // ctx.moveTo(p1x, p1y);
        // ctx.lineTo(p2x, p2y);
        // ctx.lineTo(p3x, p3y);
        // ctx.lineTo(p1x, p1y);

        // ctx.strokeStyle = cor;
        // ctx.stroke();

        //pixelsDesenho();
    };

    var desenhaImagem = function() {

        for (let i = 0; i < bufTri.length; i++) {
            const t = bufTri[i].t;
            const cor = bufTri[i].cor;

            desenhaTriangulo(
                t[0].x, t[0].y,
                t[1].x, t[1].y,
                t[2].x, t[2].y,
                cor);
        }

        // for (let i = 0; i < bufTri.length; i++) {
        //     const r = bufTri[i].r;

        //     ctx.lineWidth = 0.75;
        //     ctx.strokeStyle = "red";

        //     ctx.beginPath();
        //     ctx.rect(r[0], r[1], r[2], r[3]);
        //     ctx.stroke();

        // }



        // if (imgDados) {
        //     desenhaPixel();
        //     ctx.putImageData(imgDados, 0, 0);
        // }

        // imgDados = ctx.createImageData(larguraTela(), alturaTela());
    };

    var desenhaPixel = function() {
        var data = imgDados.data;

        for (var i = 0; i < data.length; i += 4) {

            data[i + 0] = i % (larguraTela() / 2) === 0 ? 0 : 255; // R value
            data[i + 1] = 0; // G value
            data[i + 2] = 255; // B value
            data[i + 3] = 255; // A value

        }
    };

    var ordenaTriangulosMalha = function(itens) {
        itens.sort(function(t1, t2) {
            var z1 = t1.zMax;
            var z2 = t2.zMax;
            return z1 == z2 ? 0 : z1 < z2 ? 1 : -1;
        });
    };

    var ordenaMalha = function(itens) {
        itens.sort(function(t1, t2) {
            var z1 = (t1.t[0].z + t1.t[1].z + t1.t[2].z) / 3.0;
            var z2 = (t2.t[0].z + t2.t[1].z + t2.t[2].z) / 3.0;
            z1 = Math.min(z1, t1.zMin);
            z2 = Math.min(z2, t2.zMin);
            return z1 == z2 ? 0 : z1 < z2 ? 1 : -1;
            // if (z1 < z2) {
            //     return 1;
            // }
            // if (z1 > z2) {
            //     return -1;
            // }
            // return 0;
        });
    };

    var aplicaTransformacoes = function(malhasRef) {

        var malhasTransformadas = [];

        var luzDirecao = { x: 0.0, y: 0.0, z: -1.0 };

        for (let i = 0; i < malhasRef.length; i++) {
            const malha = malhasRef[i];

            for (let j = 0; j < malha.length; j++) {
                const tri = malha[j];
                const triProjetado = [{ x: 0.0, y: 0.0, z: 0.0 }, { x: 0.0, y: 0.0, z: 0.0 }, { x: 0.0, y: 0.0, z: 0.0 }];

                for (let k = 0; k < tri.length; k++) {
                    const p = tri[k];
                    triProjetado[k].x = p.x;
                    triProjetado[k].y = p.y;
                    triProjetado[k].z = p.z;
                }

                //Rotaciona no eixo Y
                multiplicaVetorMatriz(triProjetado[0], triProjetado[0], matRotacaoY);
                multiplicaVetorMatriz(triProjetado[1], triProjetado[1], matRotacaoY);
                multiplicaVetorMatriz(triProjetado[2], triProjetado[2], matRotacaoY);

                //Rotaciona no eixo Z
                multiplicaVetorMatriz(triProjetado[0], triProjetado[0], matRotacaoZ);
                multiplicaVetorMatriz(triProjetado[1], triProjetado[1], matRotacaoZ);
                multiplicaVetorMatriz(triProjetado[2], triProjetado[2], matRotacaoZ);

                //Rotaciona no eixo X
                multiplicaVetorMatriz(triProjetado[0], triProjetado[0], matRotacaoX);
                multiplicaVetorMatriz(triProjetado[1], triProjetado[1], matRotacaoX);
                multiplicaVetorMatriz(triProjetado[2], triProjetado[2], matRotacaoX);

                triProjetado[0].z += self.distanciaZ;
                triProjetado[1].z += self.distanciaZ;
                triProjetado[2].z += self.distanciaZ;

                var normal = { x: 0.0, y: 0.0, z: 0.0 },
                    linha1 = { x: 0.0, y: 0.0, z: 0.0 },
                    linha2 = { x: 0.0, y: 0.0, z: 0.0 };

                linha1.x = triProjetado[1].x - triProjetado[0].x;
                linha1.y = triProjetado[1].y - triProjetado[0].y;
                linha1.z = triProjetado[1].z - triProjetado[0].z;

                linha2.x = triProjetado[2].x - triProjetado[0].x;
                linha2.y = triProjetado[2].y - triProjetado[0].y;
                linha2.z = triProjetado[2].z - triProjetado[0].z;

                normal.x = linha1.y * linha2.z - linha1.z * linha2.y;
                normal.y = linha1.z * linha2.x - linha1.x * linha2.z;
                normal.z = linha1.x * linha2.y - linha1.y * linha2.x;

                var l = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);

                normal.x /= l;
                normal.y /= l;
                normal.z /= l;

                if (normal.x * (triProjetado[0].x - camera.x) +
                    normal.y * (triProjetado[0].y - camera.y) +
                    normal.z * (triProjetado[0].z - camera.z) >= 0) {
                    //if(normal.x >= 0){
                    continue;
                }

                //Aplica a matriz de projeção 2D
                multiplicaVetorMatriz(triProjetado[0], triProjetado[0], matProjecao);
                multiplicaVetorMatriz(triProjetado[1], triProjetado[1], matProjecao);
                multiplicaVetorMatriz(triProjetado[2], triProjetado[2], matProjecao);

                //Escala para o tamanho da tela
                triProjetado[0].x += 1.0;
                triProjetado[0].y += 1.0;
                triProjetado[1].x += 1.0;
                triProjetado[1].y += 1.0;
                triProjetado[2].x += 1.0;
                triProjetado[2].y += 1.0;

                triProjetado[0].x *= 0.5 * larguraTela();
                triProjetado[0].y *= 0.5 * alturaTela();
                triProjetado[1].x *= 0.5 * larguraTela();
                triProjetado[1].y *= 0.5 * alturaTela();
                triProjetado[2].x *= 0.5 * larguraTela();
                triProjetado[2].y *= 0.5 * alturaTela();

                var d = Math.sqrt(luzDirecao.x * luzDirecao.x + luzDirecao.y * luzDirecao.y + luzDirecao.z * luzDirecao.z);
                luzDirecao.x /= d;
                luzDirecao.y /= d;
                luzDirecao.z /= d;

                var dp = normal.x * luzDirecao.x + normal.y * luzDirecao.y + normal.z * luzDirecao.z;
                var vCor = Math.max(Math.round(dp * 255), 64);
                var cor = `rgb(${vCor},${vCor},${vCor},1)`;

                let p1 = triProjetado[0];
                let p2 = triProjetado[1];
                let p3 = triProjetado[2];

                let xMin = Math.min(p1.x, Math.min(p2.x, p3.x));
                let xMax = Math.max(p1.x, Math.max(p2.x, p3.x));
                let yMin = Math.min(p1.y, Math.min(p2.y, p3.y));
                let yMax = Math.max(p1.y, Math.max(p2.y, p3.y));
                let zMin = Math.min(p1.z, Math.min(p2.z, p3.z));
                let zMax = Math.max(p1.z, Math.max(p2.z, p3.z));

                malhasTransformadas.push({
                    t: triProjetado,
                    normal: normal,
                    cor: cor,
                    r: [xMin, yMin, xMax - xMin, yMax - yMin],
                    zMin: zMin,
                    zMax: zMax
                });
            }

        }

        return malhasTransformadas;
    };

    var exibeCena = function(timestamp) {

        limpaTela();

        theta = timestamp / 2500;

        var mt = aplicaTransformacoes(malhas);

        ordenaMalha(mt);

        for (let i = 0; i < mt.length; i++) {
            let t = mt[i].t;
            let normal = mt[i].normal;
            let cor = mt[i].cor;

            desenhaTriangulo(
                t[0].x, t[0].y,
                t[1].x, t[1].y,
                t[2].x, t[2].y,
                cor);
        }

        bufTri = mt;

        // desenhaImagem();

        window.requestAnimationFrame(exibeCena);
    };

    this.criaMalhaObj = function(nmArq, conteudo) {
        var faces;

        if (nmArq.match(/\.stl$/ig)) {
            var stlArq = new ArquivoSTL(conteudo);
            faces = stlArq.faces;
        }else{
            faces = WavefrontObj(conteudo);
        }


        malhas = [faces];
    };

    this.criaMalhaCubo = function() {
        var cubo = [];
        //FRENTE
        cubo.push([{ x: 0.0, y: 0.0, z: 0.0 }, { x: 0.0, y: 1.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 0.0 }]);
        cubo.push([{ x: 0.0, y: 0.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 0.0 }, { x: 1.0, y: 0.0, z: 0.0 }]);

        //DIREITA
        cubo.push([{ x: 1.0, y: 0.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 1.0 }]);
        cubo.push([{ x: 1.0, y: 0.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 1.0 }, { x: 1.0, y: 0.0, z: 1.0 }]);

        //TRAS
        cubo.push([{ x: 1.0, y: 0.0, z: 1.0 }, { x: 1.0, y: 1.0, z: 1.0 }, { x: 0.0, y: 1.0, z: 1.0 }]);
        cubo.push([{ x: 1.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 1.0, z: 1.0 }, { x: 0.0, y: 0.0, z: 1.0 }]);

        //ESQUERDA
        cubo.push([{ x: 0.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 1.0, z: 1.0 }, { x: 0.0, y: 1.0, z: 0.0 }]);
        cubo.push([{ x: 0.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 1.0, z: 0.0 }, { x: 0.0, y: 0.0, z: 0.0 }]);

        // TOPO
        cubo.push([{ x: 0.0, y: 1.0, z: 0.0 }, { x: 0.0, y: 1.0, z: 1.0 }, { x: 1.0, y: 1.0, z: 1.0 }]);
        cubo.push([{ x: 0.0, y: 1.0, z: 0.0 }, { x: 1.0, y: 1.0, z: 1.0 }, { x: 1.0, y: 1.0, z: 0.0 }]);

        // BAIXO
        cubo.push([{ x: 1.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 0.0, z: 0.0 }]);
        cubo.push([{ x: 1.0, y: 0.0, z: 1.0 }, { x: 0.0, y: 0.0, z: 0.0 }, { x: 1.0, y: 0.0, z: 0.0 }]);

        return cubo;
    };

    var posicaoMouseAnterior = { x: 0, y: 0 };
    var posicaoMouse = { x: 0, y: 0 };
    var bAtivaRotacao = false;

    this.ativaRotacao = function(bVl, x, y) {
        bAtivaRotacao = bVl;

        posicaoMouseAnterior.x = x;
        posicaoMouseAnterior.y = y;
    }

    this.moveMouse = function(x, y) {
        if (bAtivaRotacao) {
            let difX = (x - posicaoMouseAnterior.x);
            let difY = (y - posicaoMouseAnterior.y);

            posicaoMouse.x += difX;
            posicaoMouse.y += difY;

            posicaoMouseAnterior.x = x;
            posicaoMouseAnterior.y = y;

            atualizaMatrizRotacaoY(posicaoMouse.x / 100);
            atualizaMatrizRotacaoX(posicaoMouse.y / 100);
        }
    };

    this.atualizaValoresCena = function() {
        atualizaMatrizProjecao();
    };

    window.requestAnimationFrame(exibeCena);

    return this;
}