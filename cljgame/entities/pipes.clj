(ns cljgame.entities.pipes
  (:require [cljgame.monogame :as g])
  (:import [System Math]
           [Microsoft.Xna.Framework Color Vector2]
           [Microsoft.Xna.Framework.Graphics SpriteEffects]))

(def speed 90)
(def gap 200)
(def frequency 5)
(def scale 2)

(def velocity (g/vect (- speed) 0))

(defn init [game]
  (let [texture (g/load-texture-2d game "pipe")
        texture-height (-> texture .Bounds .Height (* scale))
        texture-width (-> texture .Bounds .Width (* scale))]
    {:texture texture
     :pipes []
     :last-spawn 0
     :heigt texture-height
     :width texture-width }))

(defn create-pipe [position height window ]
  (let [random-top (- (rand 300))]
    {:position (g/vect (-> window g/width (+ 20)) random-top) 
     :y-offset random-top}))

(defn spawn-handler
  [{:keys [position height last-spawn] :as state} window delta-time]
  (if (<= last-spawn frequency)
    (assoc state :last-spawn (+ last-spawn delta-time))
    (->  state
        (update :pipes conj (create-pipe position height window))
        (assoc :last-spawn 0))))

(defn move-pipe [delta-time pipe] (update pipe :position g/vect+ (g/vect* velocity delta-time)))
(defn move-pipes [pipes delta-time] (map (partial move-pipe delta-time) pipes))

(defn update- [{:keys [offset width position] :as state} delta-time window]
  (-> state
      (spawn-handler window delta-time)
      (update :pipes move-pipes delta-time)))

(defn draw [sprite-batch {:keys [texture pipes]}]
  (doseq [pipe pipes]
    (let [{:keys [position]} pipe]
      (g/draw sprite-batch {:texture texture
                            :position position
                            :source-rectangle (.Bounds texture)
                            :color Color/White
                            :rotation 0
                            :origin Vector2/Zero
                            :scale scale
                            :effects SpriteEffects/None
                            :layer-depth 0}))))
