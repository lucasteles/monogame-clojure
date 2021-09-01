(ns cljgame.game
  (:require [cljgame.interop :refer [current current-exe-dir int32 load-monogame]]
            [cljgame.monogame :as g] 
            [cljgame.entities.logo :as logo])
  (:import [System Console]
           [System.IO Path])
  (:gen-class))

(load-monogame)
(import [Microsoft.Xna.Framework Color Vector2]
        [Microsoft.Xna.Framework.Input Keyboard Keys]
        [Microsoft.Xna.Framework.Graphics SpriteEffects])

(defn game-configuration! [game graphics] 
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn initialize [game { graphics :graphics-manager
                        window :window }]
  (game-configuration! game graphics)
  {:logo (logo/init window)})

(defn read-keys []
  (let [keyboard (Keyboard/GetState)
        pressed (fn [k] (.IsKeyDown keyboard k))]
    (cond 
      (and (pressed Keys/W) (pressed Keys/A)) (g/vect -2 -2)
      (and (pressed Keys/W) (pressed Keys/D)) (g/vect 2 -2)
      (and (pressed Keys/S) (pressed Keys/A)) (g/vect -2 2)
      (and (pressed Keys/S) (pressed Keys/D)) (g/vect 2 2)
      (pressed Keys/W) (g/vect 0 -2)
      (pressed Keys/S) (g/vect 0 2)
      (pressed Keys/A) (g/vect -2 0)
      (pressed Keys/D) (g/vect 2 0)
      :else Vector2/Zero)))

(defn load-content [game {state :state}]
  (update state
         :logo (partial logo/load- game)))

(defn tick [{:keys [delta-time state] }]
  (update state
          :logo (partial logo/update- delta-time)))

(defn draw [{:keys [sprite-batch delta-time graphics-device]
             { logo :logo } :state}]
    (g/clear graphics-device Color/LightGray)
    (logo/draw sprite-batch logo))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run load-content initialize tick draw))
