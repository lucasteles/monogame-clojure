(ns cljgame.entities.score
  (:require [cljgame.monogame :as g])
  (:import [System Math]))

(def ^:private font-size 75)
(def ^:private top-offset 64)

(defn init [game window]
  {:score 0
   :font (g/load-sprite-font game "scorefont")
   :position (g/vect (-> window g/width (/ 2) (- (/ font-size 2)))
                     top-offset) })

(defn draw [sprite-batch {font :font position :position score :score}]
  (g/draw-text
    sprite-batch
    {:sprite-font font
     :text score
     :position (g/vect+ position (g/vect 6 -6))
     :color :black})

  (g/draw-text
    sprite-batch
    {:sprite-font font
     :text score
     :position position
     :color :white})
  
  )
