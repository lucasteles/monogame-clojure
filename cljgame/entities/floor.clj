(ns cljgame.entities.floor
  (:require [cljgame.monogame :as g]
            [cljgame.physics :as physics]
            [cljgame.entities.pipes :as pipes])
  (:import [System Math]))

(def scale 2.5)
(def speed (- pipes/speed))

(defn init [game window world]
  (let [texture (g/load-texture-2d game "floor")
        height (-> texture .Height (* scale))
        width (-> texture .Width (* scale))
        position (g/vect 0 (- (g/height window) (/ height 2)))]
    {:texture texture
     :position position
     :height height
     :width width
     :tile-number (inc (-> window g/width (/ width) double Math/Ceiling))
     :body (physics/create-body world :static 
                                (g/rect position (g/height window) height)
                                :floor)
     :offset 0}))


(defn update- [{:keys [offset width position] :as state} delta-time]
  (assoc state
         :offset (if (>= (Math/Abs offset) width) 0
                     (+ offset (* speed delta-time)))))

(defn draw [sprite-batch state ]
  (let [{texture :texture
         position :position
         tile-number :tile-number
         offset :offset
         height :height 
         width :width } state
        step (g/vect width 0) 
        source-rect (.Bounds texture)]
    
    (dotimes [i tile-number]
      (g/draw sprite-batch {:texture texture
                            :position (-> position 
                                          (g/vect+ (g/vect* step i)) 
                                          (g/vect+ (g/vect offset 0)))
                            :source-rectangle source-rect
                            :color :white
                            :rotation 0
                            :origin (g/rect-center source-rect)
                            :scale scale
                            :effects :none
                            :layer-depth 0}))))
