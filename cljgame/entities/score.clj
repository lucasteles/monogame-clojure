(ns cljgame.entities.score
  (:require [cljgame.monogame :as g]
            [cljgame.entities.ball :as ball]))

(import [Microsoft.Xna.Framework Color])

(def ^:private font-size 64)

(defn check-point [{:keys [position size]} window]
  (let [p1 (>= (+ (.X position) (.X size))
               (g/width window))
        p2 (<= (.X position) 0)]
    (cond p1 :score/player1
          p2 :score/player2)))

(defn reset-ball [ball window]
  (assoc ball
         :position (ball/center-position window (:size ball))
         :velocity (-> ball :velocity g/vect-)))

(defn init [game]
  {:score/player1 0
   :score/player2 0
   :font (g/load-sprite-font game "sourcecodepro64")})

(defn update- [state window]
  (if-let [winner (check-point (:ball state) window)]
    (-> state
        (update-in [:score winner] inc)
        (update :ball reset-ball window))
    state))

(defn draw [sprite-batch window {:score/keys [player1 player2]
                                 font :font}]
  (let [width (g/width window)
        half-font (/ font-size 2)
        y 50
        p1-pos (g/vect (-> width (/ 4) (- half-font)) y)
        p2-pos (g/vect (-> width (* 3) (/ 4) (- half-font)) y)]
    (g/draw-text sprite-batch{:sprite-font font
                              :text player1
                              :position p1-pos
                              :color Color/Black})

    (g/draw-text sprite-batch{:sprite-font font
                              :text player2
                              :position p2-pos
                              :color Color/Black})))
