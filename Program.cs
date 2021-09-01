using System;
using System.IO;
using clojure.lang;
using Microsoft.Xna.Framework;
using clojure.clr.api;


var load = Clojure.var("clojure.core", "load");
load.invoke("/clrgame/interop");
load.invoke("/clrgame/monogame");
load.invoke("/clrgame/game");
var run = Clojure.var("clrgame.game", "-main");
run.invoke();

