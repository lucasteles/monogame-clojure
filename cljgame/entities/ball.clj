(ns cljgame.entities.ball
  (:require [cljgame.monogame :as g]
            [cljgame.monogame :as g]))

(import [Microsoft.Xna.Framework Color Vector2])

(def ^:private initial-velocity (g/vect 200))

(defn center-position [window size]
  (let [bounds (.ClientBounds window)
        width (.Width bounds)
        height (.Height bounds)
        {:keys [x y]} (g/vect-map size)]
    (g/vect (-> width (/ 2) (- (/ x 2)))
            (-> height (/ 2) (- (/ y 2))))))

(defn init [window game]
  (let [size (g/vect 40)]
    {:size size
     :texture (g/pixel-texture game Color/Brown)
     :position (center-position window size)
     :velocity initial-velocity}))

(defn move-ball [{:keys [position velocity] :as state} delta-time]
  (assoc state :position (g/vect+ position (g/vect* velocity delta-time))))

(defn check-velocity [{:keys [size position velocity] :as state} window]
  (let [y (.Y position)
        ball-size (.Y size)
        height (-> window .ClientBounds .Height)
        new-velocity (if (or (>= (+ y ball-size) height)
                             (<= y 0))
                       (g/vect-with-y velocity (- (.Y velocity)))
                       velocity)]
    (assoc state :velocity new-velocity)))

(defn update- [state delta-time window]
  (-> state
      (check-velocity window)
      (move-ball delta-time)))

(defn draw [sprite-batch {:keys [texture size position]} ]
  (g/draw sprite-batch {:texture texture
                        :destination-rectangle (g/rect position size)
                        :color Color/White}))

