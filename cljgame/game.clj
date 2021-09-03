(ns cljgame.game
  (:require [cljgame.interop :refer [int32]]
            [cljgame.monogame :as g]
            [cljgame.physics :as physics]
            [cljgame.entities.floor :as floor]
            [cljgame.entities.pipes :as pipes]
            [cljgame.entities.bird :as bird]
            [cljgame.entities.background :as background])
  (:import [System Console])
  (:gen-class))

(defn game-configuration! [game graphics]
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn exit-on-esc [game]
  (when (-> (g/keyboard-state) (g/is-key-dowm :escape))
    (g/exit game)))

(defn initialize [game { graphics :graphics-manager window :window }]
  (let [world (physics/create-world)]
    (game-configuration! game graphics)
    {:world world
     :floor (floor/init game window world)
     :background (background/init game window)
     :bird (bird/init game window world)
     :pipes (pipes/init game)}))

(defn update- [{:keys [delta-time state game window] 
                {world :world} :state}]
  (exit-on-esc game)
  (physics/step world delta-time)
  (-> state
      (update :background background/update- delta-time)
      (update :floor floor/update- delta-time)
      (update :pipes pipes/update- delta-time window)))

(defn draw [{:keys [sprite-batch graphics-device window]
            {:keys [floor background pipes bird] } :state}]
  (g/clear graphics-device :light-gray)
  (g/begin sprite-batch)

  (background/draw sprite-batch background)
  (pipes/draw sprite-batch pipes)
  (floor/draw sprite-batch floor)
  (bird/draw sprite-batch bird)

  (g/end sprite-batch))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run (constantly nil) initialize update- draw))
