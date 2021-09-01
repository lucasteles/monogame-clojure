using System;
using System.IO;
using clojure.lang;
using Microsoft.Xna.Framework;
using clojure.clr.api;


var load = Clojure.var("clojure.core", "load");
load.invoke("/cljgame/game");
var run = Clojure.var("cljgame.game", "-main");
run.invoke();

