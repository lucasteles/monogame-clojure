(ns cljgame.entities.ball
  (:require [cljgame.monogame :as g]))

(import [Microsoft.Xna.Framework Color Vector2])

(def initial-speed 250)
(def initial-velocity (g/vect initial-speed))

(defn center-position [window size]
  (let [width (g/width window)
        height (g/height window)
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

(defn wall-collision [{:keys [size position velocity] :as state} window]
  (let [y (.Y position)
        ball-size (.Y size)
        height (g/height window)
        new-velocity (if (or (>= (+ y ball-size) height)
                             (<= y 0))
                       (g/vect-with-y velocity (- (.Y velocity)))
                       velocity)]
    (assoc state :velocity new-velocity)))

(defn check-player-colision? [{:keys [position size]} player]
  (let [rect-ball (g/rect position size)
        rect-player (g/rect (:position player) (:size player))]
    (g/rect-intersects rect-ball rect-player)))


(defn player-collision [{:keys [velocity position] :as state} 
                        {:keys [player1 player2]}] 
  (if (or (check-player-colision? state player1)
          (check-player-colision? state player2))
    (assoc state :velocity (g/vect (-> velocity .X -) (.Y velocity)))
    state))

(defn update- [state delta-time window players]
  (-> state
      (wall-collision window)
      (player-collision players)
      (move-ball delta-time)))

(defn draw [sprite-batch {:keys [texture size position]} ]
  (g/draw sprite-batch {:texture texture
                        :destination-rectangle (g/rect position size)
                        :color :white}))

