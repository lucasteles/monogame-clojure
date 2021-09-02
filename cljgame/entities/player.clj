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
           :texture (g/pixel-texture game Color/SaddleBrown)
           :index index
           :position (define-player-Position window index size))))

(defn read-velocity [player-index]
  (let [keyboard (Keyboard/GetState)
        pressed (fn [k] (.IsKeyDown keyboard k))
        velocity (g/vect 0 600)]
    (cond
      (or (and (= player-index :player1) (pressed Keys/W))
          (and (= player-index :player2) (pressed Keys/Up)))
      (g/vect- velocity)


      (or (and (= player-index :player1) (pressed Keys/S))
          (and (= player-index :player2) (pressed Keys/Down)))
      velocity

      :else Vector2/Zero)))

(defn clamp-position [position window size]
  (let [{:keys [x y]} (g/vect-map position)
        min-y 0
        max-y (-> window .ClientBounds .Height) ]
    (cond
      (< y min-y)
      (g/vect-with-y position 0)

      (> (+ y (.Y size)) max-y)
      (g/vect-with-y position (- max-y (.Y size)))

      :else position)))

(defn move-player [position player-index delta-time window size]
  (let [velocity (-> player-index read-velocity (g/vect* delta-time))]
    (-> position (g/vect+ velocity) (clamp-position window size))))

(defn update- [{ index :index size :size :as state} delta-time window]
  (update state :position move-player index delta-time window size))

(defn draw [sprite-batch {:keys [texture size position]} ]
  (g/draw sprite-batch {:texture texture
                        :destination-rectangle (g/rect position size)
                        :color Color/White}))

