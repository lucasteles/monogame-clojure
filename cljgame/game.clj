(ns cljgame.game
  (:require [cljgame.interop :refer [current current-exe-dir int32 load-monogame]]
            [cljgame.monogame :as g] 
            [cljgame.entities.logo :as logo]
            [cljgame.entities.player :as player]
            [cljgame.entities.player :as player])
  (:import [System Console]
           [System.IO Path])
  (:gen-class))

(load-monogame)
(import [Microsoft.Xna.Framework Color Vector2]
        [Microsoft.Xna.Framework.Input Keyboard Keys])

(defn game-configuration! [game graphics] 
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn exit-on-esc [game] 
  (when (-> (Keyboard/GetState) (.IsKeyDown Keys/Escape))
    (.Exit game)))

(defn initialize [game { graphics :graphics-manager
                         window :window
                         state :state }]
  (game-configuration! game graphics)

  {:logo (logo/init window)
   :player1 (player/init window :player1 state game)
   :player2 (player/init window :player2 state game)})

(defn load-content [game {state :state}]
   (-> state
       (update :logo logo/load- game)))

(defn update- [{:keys [delta-time state game] }]
  (exit-on-esc game)
  (-> state
      (update :logo logo/update- delta-time)
      (update :player1 player/update- delta-time)
      (update :player2 player/update- delta-time)
      
      ))

(defn draw [{:keys [sprite-batch delta-time graphics-device]
             { :keys [player1 player2 logo] } :state}]
    (g/clear graphics-device Color/LightGray)
    (g/begin sprite-batch)

    (logo/draw sprite-batch logo)
    (player/draw sprite-batch player1)
    (player/draw sprite-batch player2)

    (g/end sprite-batch))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run load-content initialize update- draw))
