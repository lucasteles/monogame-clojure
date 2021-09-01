(ns cljgame.entities.player
  (:require [cljgame.monogame :as g]))

(import [Microsoft.Xna.Framework Color Vector2]
        [Microsoft.Xna.Framework.Input Keyboard Keys])

(defn define-player-Position [window index size]
  (let [bounds (.ClientBounds window)
        width (.Width bounds)
        height (.Height bounds)
        {:keys [x y]} (g/vect-map size)]
    (case index
      :player1
      (g/vect x (-> height (/ 2) (- (/ y 2))))

      :player2
      (g/vect (- width  (* x 2))
              (-> height (/ 2) (- (/ y 2)))))))

(defn init [window index state game]
  (let [size (g/vect 40 200)]
    (assoc state
           :size size
           :texture (g/pixel-texture game Color/Black)
           :index index
           :position (define-player-Position window index size))))

(defn update- [delta-time {:keys [] :as state}]
  state)

(defn draw [sprite-batch {:keys [texture size position]} ]
  (g/draw sprite-batch {:texture texture
                        :destination-rectangle (g/rect position size)
                        :color Color/White}))

(defn read-keys []
  (let [keyboard (Keyboard/GetState)
        pressed (fn [k] (.IsKeyDown keyboard k))]
    (cond 
      (and (pressed Keys/W) (pressed Keys/A)) (g/vect -2 -2)
      (and (pressed Keys/W) (pressed Keys/D)) (g/vect 2 -2)
      (and (pressed Keys/S) (pressed Keys/A)) (g/vect -2 2)
      (and (pressed Keys/S) (pressed Keys/D)) (g/vect 2 2)
      (pressed Keys/W) (g/vect 0 -2)
      (pressed Keys/S) (g/vect 0 2)
      (pressed Keys/A) (g/vect -2 0)
      (pressed Keys/D) (g/vect 2 0)
      :else Vector2/Zero)))
