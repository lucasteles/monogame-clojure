(ns cljgame.entities.logo
  (:require [cljgame.monogame :as g]))


(import [Microsoft.Xna.Framework Color])

(defn init [window]
  {:rotation 0
   :position  (g/vect (-> window g/width (/ 2))
                      (-> window g/height (/ 2)))})


(defn load- [state game] 
  (assoc state 
         :texture (g/load-texture-2d game "logo")))


(defn update- [{:keys [rotation] :as state} delta-time]
  (assoc state
         :rotation (+ rotation (* delta-time 0.2))))


(defn draw [sprite-batch state ]
 (let [{logo :texture 
        rotation :rotation
        position :position} state
       logo-center (g/vect (-> logo .Bounds .Width (/ 2))
                           (-> logo .Bounds .Height (/ 2)))]

    (g/draw sprite-batch {:texture logo
                          :position position
                          :source-rectangle (.Bounds logo)
                          :color (Color/op_Multiply Color/White 0.5)
                          :rotation rotation
                          :origin logo-center
                          :scale 0.5
                          :effects :none
                          :layer-depth 0}))) 
