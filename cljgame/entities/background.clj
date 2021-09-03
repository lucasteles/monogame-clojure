(ns cljgame.entities.background
  (:require [cljgame.monogame :as g])
  (:import [System Math]
           [Microsoft.Xna.Framework Color Vector2]
           [Microsoft.Xna.Framework.Graphics SpriteEffects]))

(def scale 4.5)
(def speed -15)

(defn init [game window]
  (let [texture (g/load-texture-2d game "background")
        texture-height (-> texture .Bounds .Height (* scale))
        texture-width (-> texture .Bounds .Width (* scale))]
    {:texture texture
     :position (g/vect 0 0)
     :heigt texture-height
     :width texture-width
     :tile-number (inc (-> window g/width (/ texture-width) double Math/Ceiling))
     :offset 0}))

(defn update- [{:keys [offset width position] :as state} delta-time]
  (assoc state
         :offset (if (>= (Math/Abs offset) width) 0
                     (+ offset (* speed delta-time)))))

(defn draw [sprite-batch state ]
  (let [{logo :texture
         position :position
         tile-number :tile-number
         offset :offset
         height :heigt 
         width :width } state
        step (g/vect width 0) ]
    
    (dotimes [i tile-number]
      (g/draw sprite-batch {:texture logo
                            :position (-> position 
                                          (g/vect+ (g/vect* step i)) 
                                          (g/vect+ (g/vect offset 0)))
                            :source-rectangle (.Bounds logo)
                            :color :white
                            :rotation 0
                            :origin Vector2/Zero
                            :scale scale
                            :effects SpriteEffects/None
                            :layer-depth 1}))))
