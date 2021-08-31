(ns jogo
  (:require [interop :refer :all]
            [monogame :as g])
  (:gen-class))

(assembly-load-from "MonoGame.dll")
(import [Microsoft.Xna.Framework Color])
(import [Microsoft.Xna.Framework.Graphics SpriteEffects])

(defn initialize [game graphics]
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn load-content [game graphics state]
  { :texture/logo (g/load-texture-2d game "logo") })

(defn tick [{:keys [game game-time state]}]
  state)

(defn draw [{:keys [window sprite-batch delta-time graphics-device]
             { logo :texture/logo } :state}]
  (let [logo-center (g/vect (-> logo .Bounds .Width (/ 2))
                            (-> logo .Bounds .Height (/ 2)))
        position (g/vect (-> window .ClientBounds .Width (/ 2))
                         (-> window .ClientBounds .Height (/ 2)))]
    (g/clear graphics-device Color/LightGray)

    (g/begin sprite-batch)
    (g/draw sprite-batch {:texture logo
                          :position position
                          :source-rectangle (.Bounds logo)
                          :color Color/White
                          :rotation 0
                          :origin logo-center
                          :scale 0.5
                          :effects SpriteEffects/None
                          :layer-depth 0})
    (g/end sprite-batch)))

(Console/WriteLine "Ola Delboni")
(g/run load-content initialize tick draw)


