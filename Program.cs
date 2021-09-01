using System;
using System.IO;
using clojure.lang;
using Microsoft.Xna.Framework;
using clojure.clr.api;


var load = Clojure.var("clojure.core", "load");
load.invoke("/src/clrgame/interop");
load.invoke("/src/clrgame/monogame");
load.invoke("/src/clrgame/game");
var run = Clojure.var("clrgame.game", "-main");
run.invoke();

