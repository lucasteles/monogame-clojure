(ns cljgame.game
  (:require [cljgame.interop :refer [current current-exe-dir int32 load-monogame]]
            [cljgame.monogame :as g] 
            [cljgame.entities.logo :as logo]
            [cljgame.entities.ball :as ball]
            [cljgame.entities.player :as player]
            [cljgame.entities.ball :as ball]
            [cljgame.entities.score :as score])
  (:import [System Console])
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
                         window :window }]
  (game-configuration! game graphics)

  {:logo (logo/init window)
   :player1 (player/init window :player1 game)
   :player2 (player/init window :player2 game)
   :ball (ball/init window game)
   :score (score/init) })

(defn load-content [game {state :state}]
   (-> state
       (update :logo logo/load- game)))

(defn update- [{:keys [delta-time state game window] }]
  (exit-on-esc game)
  (-> state
      (update :logo logo/update- delta-time)
      (update :ball ball/update- delta-time window (select-keys state [:player1 :player2]))
      (update :player1 player/update- delta-time window)
      (update :player2 player/update- delta-time window)
      (score/update- window)))


(defn draw [{:keys [sprite-batch delta-time graphics-device]
             { :keys [player1 player2 logo ball] } :state}]
    (g/clear graphics-device Color/LightGray)
    (g/begin sprite-batch)

    (logo/draw sprite-batch logo)
    (player/draw sprite-batch player1)
    (player/draw sprite-batch player2)
    (ball/draw sprite-batch ball)

    (g/end sprite-batch))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run load-content initialize update- draw))
