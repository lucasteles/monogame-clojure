(ns cljgame.entities.bird
  (:require [cljgame.monogame :as g]
            [cljgame.physics :as physics])
  (:import [System Math]
           [Microsoft.Xna.Framework Color Vector2]
           [Microsoft.Xna.Framework.Graphics SpriteEffects]))

(def scale 0.8)

(defn init [game window world]
  (let [texture (g/load-texture-2d game "birdspritesheet")
        height (-> texture .Height (* scale))
        width (-> texture .Width (* scale))
        position (g/vect 100 100)]
    {:texture texture
     :body (physics/create-body world :dynamic 
                                (g/rect position width height)
                                :bird)}))


(defn update- [{:keys [offset width position] :as state} delta-time]
  state)

(defn draw [sprite-batch state ]
  (let [{texture :texture
         body :body} state
        position (physics/position body)
        rotation (physics/rotation body)
        source-rect (.Bounds texture)]

    (g/draw sprite-batch {:texture texture
                          :position position
                          :source-rectangle source-rect
                          :color Color/White
                          :rotation rotation
                          :origin (g/rect-center source-rect)
                          :scale scale
                          :effects SpriteEffects/None
                          :layer-depth 0})))
