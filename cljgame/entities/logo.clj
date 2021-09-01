(ns cljgame.entities.logo
  (:require [cljgame.monogame :as g]))


(import [Microsoft.Xna.Framework Color ]
        [Microsoft.Xna.Framework.Graphics SpriteEffects])

(defn init [window]
  {:rotation 0
   :position  (g/vect (-> window .ClientBounds .Width (/ 2))
                      (-> window .ClientBounds .Height (/ 2)))})



(defn load- [game state] 
  (assoc state 
         :texture (g/load-texture-2d game "logo")))


(defn update- [delta-time {:keys [rotation] :as state}]
  (assoc state
         :rotation (+ rotation (* delta-time 0.2))))


(defn draw [sprite-batch state ]
 (let [{logo :texture 
        rotation :rotation
        position :position} state
       logo-center (g/vect (-> logo .Bounds .Width (/ 2))
                           (-> logo .Bounds .Height (/ 2)))]

    (g/begin sprite-batch)
    (g/draw sprite-batch {:texture logo
                          :position position
                          :source-rectangle (.Bounds logo)
                          :color (Color/op_Multiply Color/White 0.5)
                          :rotation rotation
                          :origin logo-center
                          :scale 0.5
                          :effects SpriteEffects/None
                          :layer-depth 0})
    (g/end sprite-batch))) 
