(ns cljgame.entities.player
  (:require [cljgame.monogame :as g]))

(defn define-player-Position [window index size]
  (let [width (g/width window)
        height (g/height window)
        {:keys [x y]} (g/vect-map size)]
    (case index
      :player1
      (g/vect x (-> height (/ 2) (- (/ y 2))))

      :player2
      (g/vect (- width  (* x 2))
              (-> height (/ 2) (- (/ y 2)))))))

(defn init [window index game]
  (let [size (g/vect 40 200)]
    {:size size
     :texture (g/pixel-texture game :saddle-brown)
     :index index
     :position (define-player-Position window index size) }))

(defn read-velocity [player-index]
  (let [keyboard (g/keyboard-state)
        pressed (fn [k] (g/is-key-dowm keyboard k))
        velocity (g/vect 0 600)]
    (cond
      (or (and (= player-index :player1) (pressed :w))
          (and (= player-index :player2) (pressed :up)))
      (g/vect- velocity)


      (or (and (= player-index :player1) (pressed :s))
          (and (= player-index :player2) (pressed :down)))
      velocity

      :else g/vect-0)))

(defn clamp-position [position window size]
  (let [{:keys [x y]} (g/vect-map position)
        min-y 0
        max-y (g/height window) ]
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
                        :color :white}))

