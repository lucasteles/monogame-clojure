using clojure.lang;
using Microsoft.Xna.Framework;

RT.load("program");
var load = clojure.clr.api.Clojure.var("program", "-main");
load.invoke();