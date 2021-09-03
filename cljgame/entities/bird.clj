(ns cljgame.entities.bird
  (:require [cljgame.monogame :as g]
            [cljgame.physics :as physics])
  (:import [System Math]
           [Microsoft.Xna.Framework Color Vector2]
           [Microsoft.Xna.Framework.Graphics SpriteEffects]))

(def scale 0.8)

(defn init [game window world]
  (let [texture (g/load-texture-2d game "birdspritesheet")
        position (g/vect 100 100)
        sprite-width (-> texture .Width (/ 2))
        sprite-height (.Height texture)]
    {:texture texture
     :sprite-index 0
     :animation-frame-time 0
     :sprite-source [(g/rect 0 0 sprite-width sprite-height)
                     (g/rect sprite-width 0 (.Width texture) sprite-height)]
     :body (physics/create-body world :dynamic 
                                (g/rect position 
                                        (* sprite-width scale) 
                                        (* sprite-height scale))
                                :bird)}))


(defn update- [{:keys [offset width position] :as state} delta-time]
  state)

(defn draw [sprite-batch state ]
  (let [{texture :texture
         sprite-index :sprite-index
         sprite-source :sprite-source
         body :body} state
        position (physics/position body)
        rotation (physics/rotation body)
        source-rect (get sprite-source sprite-index )]
    (g/draw sprite-batch {:texture texture
                          :position position
                          :source-rectangle source-rect
                          :color :white
                          :rotation rotation
                          :origin (g/rect-center source-rect)
                          :scale scale
                          :effects SpriteEffects/None
                          :layer-depth 0})))
