using clojure.lang;
using Microsoft.Xna.Framework;

RT.load("game");

var load = clojure.clr.api.Clojure.var("game", "-main");
load.invoke();
