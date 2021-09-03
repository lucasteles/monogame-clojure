(ns cljgame.entities.bird
  (:require [cljgame.physics :as physics]
            [cljgame.monogame :as g])
  (:import [System Math]))

(def scale 0.8)
(def jump-force (g/vect 0 -8))
(def animation-duration 0.25)

(defn init [game world]
  (let [texture (g/load-texture-2d game "birdspritesheet")
        position (g/vect 150 50)
        sprite-width (-> texture .Width (/ 2))
        sprite-height (.Height texture)]
    {:texture texture
     :sprite-index :normal
     :animation-frame-time 0
     :rotation 0
     :sprite-source {:normal (g/rect 0 0 sprite-width sprite-height)
                     :flip (g/rect sprite-width 0 sprite-width sprite-height)}
     :body (physics/create-body world :dynamic
                                (g/rect position
                                        (* sprite-width scale)
                                        (* sprite-height scale))
                                :bird)}))


(defn handle-jump [{:keys [offset width position body holding] :as state} delta-time]
  (let [keyboard (g/keyboard-state)
        pressed (g/is-key-dowm keyboard :space)
        released (g/is-key-up keyboard :space)]
    (cond
      (and (not holding) pressed)
      (do (physics/set-linear-velocity! body g/vect-0)
          (physics/apply-impulse! body jump-force)
          (assoc state 
                 :holding true
                 :sprite-index :flip
                 :rotation 0
                 :animation-frame-time 0))
      (and holding released )
      (assoc state :holding false)
      :else
      state)))

(defn handle-animation [{:keys [sprite-index animation-frame-time] :as state} delta-time]
  (if (and (= :flip sprite-index) (>= animation-frame-time animation-duration))
    (assoc state :animation-frame-time 0 :sprite-index :normal)
    (assoc state :animation-frame-time (+ delta-time animation-frame-time))))

(defn handle-rotation [{ body :body 
                         rotation :rotation :as state}]
  (let [velocity-y (-> body physics/velocity .Y)]
    (cond 
      (< velocity-y 0)
      (let [new-rotation (- rotation 0.03)
            max-rot -0.52]
        (assoc state :rotation 
               (if (< new-rotation max-rot) 
                     max-rot new-rotation)))
       (> velocity-y 0) 
       (let [new-rotation (+ rotation 0.03)
             max-rot 5.76]
         (assoc state :rotation (if (> new-rotation max-rot) 
                                     max-rot new-rotation)))
      :else state)))

(defn update- [state delta-time]
  (-> state 
      (handle-jump delta-time)
      (handle-animation delta-time)
      (handle-rotation)))

(defn draw [sprite-batch state ]
  (let [{texture :texture
         sprite-index :sprite-index
         sprite-source :sprite-source
         rotation :rotation
         body :body} state
        position (physics/position body)
        source-rect (sprite-index sprite-source)]
    (g/draw sprite-batch {:texture texture
                          :position position
                          :source-rectangle source-rect
                          :color :white
                          :rotation rotation
                          :origin (g/rect-center source-rect)
                          :scale scale
                          :effects :none
                          :layer-depth 0})))
