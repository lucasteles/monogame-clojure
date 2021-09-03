(ns cljgame.entities.pipes
  (:require [cljgame.monogame :as g]
            [cljgame.physics :as physics])
  (:import [System Math]))

(def speed 90)
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
     :height texture-height
     :width texture-width }))

(defn create-pipe [position width texture-height floor-height window world]
  (let [center (/ texture-height 2)
        random-top (-> (g/random-int 100 350))
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
     :body-down body-down}))

(defn spawn-handler
  [{:keys [position width height last-spawn] :as state} window world delta-time floor-height]
  (if (<= last-spawn frequency) 
    (assoc state :last-spawn (+ last-spawn delta-time))
    (-> state
        (update :pipes conj (create-pipe position width height floor-height window world))
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

(defn update-pipes [{:keys [offset width position] :as state} window world delta-time floor-height]
  (-> state
      (spawn-handler window world delta-time floor-height)
      (update :pipes clear-pipes world)))

(defn update- [{{floor-height :height} :floor :as state} 
               state-key window world delta-time]
  (println floor-height)
  (update state state-key update-pipes window world delta-time floor-height))

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
