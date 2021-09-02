(ns cljgame.entities.score
  (:require [cljgame.monogame :as g]
            [cljgame.entities.ball :as ball]))

(import [Microsoft.Xna.Framework Color Vector2])

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

(defn init []
  {:score/player1 0
   :score/player2 0})

(defn update- [state window]
  (if-let [winner (check-point (:ball state) window)] 
    (-> state 
        (update-in [:score winner] inc)
        (update :ball reset-ball window))
    state))

(defn draw [sprite-batch window score]
  (let [width (g/width window)]))
