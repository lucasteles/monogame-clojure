using clojure.lang;
using Microsoft.Xna.Framework;

RT.load("src/clrgame/interop");
RT.load("src/clrgame/monogame");
RT.load("src/clrgame/game");

var load = clojure.clr.api.Clojure.var("clrgame.game", "-main");
load.invoke();
