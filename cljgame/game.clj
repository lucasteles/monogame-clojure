(ns cljgame.game
  (:require [cljgame.interop :refer [int32]]
            [cljgame.monogame :as g]
            [cljgame.physics :as physics]
            [cljgame.entities.floor :as floor]
            [cljgame.entities.pipes :as pipes]
            [cljgame.entities.bird :as bird]
            [cljgame.entities.score :as score]
            [cljgame.entities.background :as background])
  (:import [System Console])
  (:gen-class))

(defn game-configuration! [game graphics]
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn exit-on-esc [game keyboard-state]
  (when (g/is-key-dowm keyboard-state :escape)
    (g/exit game)))

(defn handle-pause [state keyboard-state]
  (if (g/is-key-dowm keyboard-state :space)
    (assoc state :paused false) state))

(defn initialize [game { graphics :graphics-manager window :window }]
  (let [world (physics/create-world (g/vect 0 20))]
    (game-configuration! game graphics)

    {:world world
     :floor (floor/init game window world)
     :background (background/init game window)
     :bird (bird/init game world)
     :pipe-manager (pipes/init game)
     :score (score/init game window)
     :paused true}))

(defn update- [{:keys [delta-time state game window]
                {world :world paused :paused} :state}]
  (let [keyboard (g/keyboard-state)]
    (exit-on-esc game keyboard)

    (if paused
      (handle-pause state keyboard)

      (do
        (physics/step world delta-time)
        (-> state
            (update :background background/update- delta-time)
            (update :floor floor/update- delta-time)
            (update :bird bird/update- keyboard delta-time)
            (pipes/update- window world delta-time))))))

(defn draw [{:keys [sprite-batch graphics-device window]
             {:keys [floor background pipe-manager bird score] } :state}]
  (g/clear graphics-device :light-gray)
  (g/begin sprite-batch)

  (background/draw sprite-batch background)
  (pipes/draw sprite-batch pipe-manager)
  (floor/draw sprite-batch floor)
  (bird/draw sprite-batch bird)
  (score/draw sprite-batch score)
  (g/end sprite-batch))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run (constantly nil) initialize update- draw))
