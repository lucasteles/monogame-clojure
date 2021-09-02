(ns cljgame.game
  (:require [cljgame.interop :refer [current current-exe-dir int32 load-monogame]]
            [cljgame.monogame :as g] 
            [cljgame.entities.floor :as floor]
            [cljgame.entities.pipes :as pipes]
            [cljgame.entities.background :as background])
  (:import [System Console]
           [Microsoft.Xna.Framework Color Vector2]
           [Microsoft.Xna.Framework.Input Keyboard Keys])
  (:gen-class))

(defn game-configuration! [game graphics] 
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn exit-on-esc [game] 
  (when (-> (Keyboard/GetState) (.IsKeyDown Keys/Escape))
    (.Exit game)))

(defn initialize [game { graphics :graphics-manager
                         window :window }]
  (game-configuration! game graphics)

  {:floor (floor/init game window) 
   :background (background/init game window) 
   :pipes (pipes/init game) }) 

(defn update- [{:keys [delta-time state game window] }]
  (exit-on-esc game)
  (-> state
      (update :background background/update- delta-time)
      (update :floor floor/update- delta-time)
      (update :pipes pipes/update- delta-time window)))

(defn draw [{:keys [sprite-batch graphics-device window]
             { :keys [floor background pipes] } :state}]
    (g/clear graphics-device Color/LightGray)
    (g/begin sprite-batch)
    (background/draw sprite-batch background)
    (floor/draw sprite-batch floor)
    (pipes/draw sprite-batch pipes)
    (g/end sprite-batch))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run (constantly nil) initialize update- draw))
