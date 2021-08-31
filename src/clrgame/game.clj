(ns clrgame.game
  (:require [clrgame.interop :refer [current current-exe-dir int32 load-monogame]]
            [clrgame.monogame :as g])
  (:import [System Console]
           [System.IO Path])
  (:gen-class))

(load-monogame)

(import [Microsoft.Xna.Framework Color Vector2]
        [Microsoft.Xna.Framework.Input Keyboard Keys]
        [Microsoft.Xna.Framework.Graphics SpriteEffects])

(defn initialize [game { graphics :graphics-manager
                        window :window }]
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics)

  {:rotation 0
   :position  (g/vect (-> window .ClientBounds .Width (/ 2))
                      (-> window .ClientBounds .Height (/ 2))) })

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

(defn load-content [game {state :state}]
  (assoc state
         :texture/logo
         (g/load-texture-2d game "logo")))

(defn tick [{:keys [game game-time state]}]
  (let [{rot :rotation position :position} state
        velocity (read-keys)]
    (assoc state
           :rotation (+ rot 0.01) 
           :position (g/vect+ position velocity))))

(defn draw [{:keys [sprite-batch delta-time graphics-device]
             { logo :texture/logo
              rotation :rotation
              position :position } :state}]
  (let [logo-center (g/vect (-> logo .Bounds .Width (/ 2))
                            (-> logo .Bounds .Height (/ 2)))]
    (g/clear graphics-device Color/LightGray)

    (g/begin sprite-batch)
    (g/draw sprite-batch {:texture logo
                          :position position
                          :source-rectangle (.Bounds logo)
                          :color Color/White
                          :rotation rotation
                          :origin logo-center
                          :scale 0.5
                          :effects SpriteEffects/None
                          :layer-depth 0})
    (g/end sprite-batch)))

(defn -main [& args]
  (Console/WriteLine "Ola Delboni")
  (g/run load-content initialize tick draw))
