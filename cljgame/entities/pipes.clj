(ns cljgame.entities.pipes
  (:require [cljgame.monogame :as g]
            [cljgame.physics :as physics])
  (:import [System Math]))

(def speed 100)
(def gap 280)
(def frequency 4)
(def scale 2)
(def remove-trashold -300)
(def velocity (g/vect (- speed) 0))

(defn init [game]
  (let [texture (g/load-texture-2d game "pipe")
        texture-height (-> texture .Height (* scale))
        texture-width (-> texture .Width (* scale))]
    {:texture texture
     :pipes []
     :last-spawn frequency
     :score-sound (-> game (g/load-sound-effect "sfxpoint") g/sound-effect-instance)
     :height texture-height
     :width texture-width }))

(defn create-pipe [position width texture-height window world]
  (let [center (/ texture-height 2)
        random-top (-> (g/random-int 80 310))
        spawn-x (-> window g/width (+ texture-height) inc)
        window-height (g/height window) 
        spawn-y-up (- random-top center)
        spawn-y-down (-> window-height (+ center) (- (- window-height random-top)) (+ gap))
        body-up (physics/create-body world :kinematic
                                     (g/rect spawn-x
                                             spawn-y-up
                                             width texture-height)
                                     :pipe)
        body-down (physics/create-body world :kinematic
                                     (g/rect spawn-x
                                             spawn-y-down
                                             width texture-height)
                                     :pipe Math/PI)]
    (physics/set-linear-velocity! body-up velocity)
    (physics/set-linear-velocity! body-down velocity)
    {:body-up body-up
     :body-down body-down
     :counted false}))

(defn spawn-handler
  [{:keys [position width height last-spawn] :as state} window world delta-time]
  (if (<= last-spawn frequency) 
    (assoc state :last-spawn (+ last-spawn delta-time))
    (-> state
        (update :pipes conj (create-pipe position width height window world))
        (assoc :last-spawn 0))))

(defn pipe-offscreen? [pipe]
  (-> pipe :body-up physics/position .X (<= remove-trashold)))

(defn clear-pipes [pipes world]
  (->>
    (for [pipe pipes]
      (if (pipe-offscreen? pipe)
        (do (physics/destroy-body! world (:body-up pipe))
            (physics/destroy-body! world (:body-down pipe)))
        pipe))
    (filter some?)))

(defn check-point [pipes sound player-pos]
  (for [pipe pipes]
    (if (and (false? (:counted pipe)) 
             (-> pipe :body-up physics/position .X (< (.X player-pos))))
      (do (g/play sound)
          (assoc pipe :counted true))
      pipe)))

(defn update-pipes [{:keys [offset width position score-sound] :as state} window world delta-time player]
  (-> state
      (spawn-handler window world delta-time)
      (update :pipes clear-pipes world)
      (update :pipes check-point score-sound (-> player :body physics/position))))

(defn update- [{player :bird :as state} state-key window world delta-time]
  (update state state-key update-pipes window world delta-time player))

(defn draw [sprite-batch {:keys [texture pipes]}]
  (doseq [pipe pipes]
    (let [{body-up :body-up
           body-down :body-down} pipe
          bounds (.Bounds texture)]
       (g/draw sprite-batch
              {:texture texture
               :position (physics/position body-up)
               :source-rectangle bounds
               :color :white
               :rotation (physics/rotation body-up)
               :origin (g/rect-center bounds)
               :scale scale
               :effects :none
               :layer-depth 0})

      (g/draw sprite-batch
              {:texture texture
               :position (physics/position body-down)
               :source-rectangle bounds
               :color :white
               :rotation (physics/rotation body-down)
               :origin (g/rect-center bounds)
               :scale scale
               :effects :none
               :layer-depth 0}))))
