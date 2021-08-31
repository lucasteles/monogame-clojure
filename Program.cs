using System;
using System.IO;
using clojure.lang;
using Microsoft.Xna.Framework;

LoadCljFiles("src/");
var load = clojure.clr.api.Clojure.var("clrgame.game", "-main");
load.invoke();

static void LoadCljFiles(string sDir)
{
    foreach (var d in Directory.GetDirectories(sDir))
    foreach (var file in Directory.GetFiles(d, "*.clj"))
    {
        var filename = Path.GetFileNameWithoutExtension(file);
        var path = Path.Combine(Path.GetDirectoryName(file), filename);
        RT.load(path);
        LoadCljFiles(d);
    }
}
