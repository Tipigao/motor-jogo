var WavefrontObj = function(conteudo) {
    var conteudoB64 = conteudo.replace(/data.+base64,(.+)$/ig, '$1');

    var conteudoObj = window.atob(conteudoB64);

    var linhas = conteudoObj.split('\n');
    var vertices = [];
    var faces = [];

    for (let i = 0; i < linhas.length; i++) {
        const linha = linhas[i];

        if (linha.startsWith('#'))
            continue;

        let inf = linha.match(/^([vf])[ ]{1,}([^ ]{1,})[ ]{1,}([^ ]{1,})[ ]{1,}([^ ]{1,})/);

        if (!inf || inf.length !== 5)
            continue;

        if (inf[1] === 'v') {
            var v = inf;
            var vet = { x: parseFloat(v[2]), y: parseFloat(v[3]), z: parseFloat(v[4]) };
            vertices.push(vet);
        } else if (inf[1] === 'f') {
            let f = inf;
            let i1 = parseInt(f[2].split('/')[0]) - 1,
                i2 = parseInt(f[3].split('/')[0]) - 1,
                i3 = parseInt(f[4].split('/')[0]) - 1;

            let t = [vertices[i1], vertices[i2], vertices[i3]];
            let tri = [
                { x: t[0].x, y: t[0].y, z: t[0].z },
                { x: t[1].x, y: t[1].y, z: t[1].z },
                { x: t[2].x, y: t[2].y, z: t[2].z }
            ];

            faces.push(tri);
        }
    }

    return faces;
};