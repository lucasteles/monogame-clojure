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

(defn create-pipe [width texture-height window world]
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
  [{:keys [width height last-spawn] :as state} window world delta-time]
  (if (<= last-spawn frequency)
    (assoc state :last-spawn (+ last-spawn delta-time))
    (-> state
        (update :pipes conj (create-pipe width height window world))
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

(defn not-counted-pipe? [bird pipe]
  (and (false? (:counted pipe))
       (-> pipe :body-up physics/position .X (< (-> bird :body physics/position .X)))))

(defn check-point [{:keys [bird score]
                    {:keys [pipes score-sound]} :pipe-manager
                    :as state}]
  (let [pipe (->> pipes (filter (partial not-counted-pipe? bird)) first)]
    (if pipe
      (do (g/play score-sound)
          (-> state
              (assoc-in [:pipe-manager :pipes] (map #(if (= % pipe) 
                                                       (assoc % :counted true) 
                                                       %) pipes))
              (update-in [:score :score] inc)))
      state)))

(defn update-pipes [{:keys [offset width position] :as state} window world delta-time]
  (-> state
      (spawn-handler window world delta-time)
      (update :pipes clear-pipes world)))

(defn update- [state window world delta-time]
  (-> state
      (update :pipe-manager update-pipes window world delta-time)
      (check-point)))

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

