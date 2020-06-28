var ArquivoSTL = function(conteudo) {
    var self = this;

    var _triangulos = [],
        qtdTriangulos;

    this.carregaSTL = function(conteudo) {
        var conteudoB64 = conteudo.replace(/data.+base64,(.+)$/ig, '$1');
        // var conteudoBin = base64ToBlob(conteudoB64);

        var conteudoBin2 = convertDataURIToBinary(conteudo);

        var conteudoBin = window.atob(conteudoB64);

        if (conteudoBin.length < 100) {
            return;
        }

        // const cabTemp = [];
        const dados = new Uint8Array(conteudoBin);
        const cabecalho = new Uint8Array(80);
        let i = 0;
        for (i = 0; i < 80; i++) {
            cabecalho[i] = conteudoBin.charCodeAt(i);
            // cabTemp.push(conteudoBin[i]);
        }

        var buf = new ArrayBuffer(conteudoBin2.length);
        var view = new DataView(buf);

        conteudoBin2.forEach(function(b, idx) {
            view.setUint8(idx, b);
        });

        const qtdTriangulos = view.getUint32(i, true);

        i += 4;

        var x, y, z,
            v, c;

        var n = [3];
        var t = [3];

        while (_triangulos.length < qtdTriangulos) {

            n[0] = view.getFloat32(i, true);
            n[1] = view.getFloat32((i += 4), true);
            n[2] = view.getFloat32((i += 4), true);

            for (let j = 0; j < 3; j++) {
                x = view.getFloat32((i += 4), true);
                y = view.getFloat32((i += 4), true);
                z = view.getFloat32((i += 4), true);

                v = { x: x, y: y, z: z };

                t[j] = v;
            }

            let tri = [
                { x: t[0].x, y: t[0].y, z: t[0].z },
                { x: t[1].x, y: t[1].y, z: t[1].z },
                { x: t[2].x, y: t[2].y, z: t[2].z }
            ];

            const c = view.getUint16((i += 4), true);

            i += 2;

            _triangulos.push(tri);
        }
    };

    Object.defineProperty(this, 'faces', {
        get: function() {
            return _triangulos;
        },
        set: function(v) {
            _triangulos = v;
        },
    });

    if (conteudo) {
        self.carregaSTL(conteudo);
    }

    return this;
};