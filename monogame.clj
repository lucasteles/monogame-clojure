(ns monogame
  (:require [interop :refer :all])
  (:import [System.IO Directory Path]))

(assembly-load-from "MonoGame.dll")
(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color])
(import [Microsoft.Xna.Framework.Graphics SpriteBatch Texture2D])

(def graphics-device (memoize (fn [game] (get-prop game "GraphicsDevice"))))

(defn load-texture-2d [game texture-name]
  (let [content (get-prop game "Content")]
    #_ (.Load content (type-args Texture2D) texture-name)
    (generic-method content "Load" |Texture2D| texture-name)))


(defn run [load-fn initialize-fn update-fn draw-fn]
  (let [props (atom {:state {}})
        game-instance (proxy
                        ;; first vector contains superclass and interfaces that the created class should extend/implement
                        ;; second vector contains arguments to superclass constructor
                        [Game] []

                        ;; below are all overriden/implemented methods
                        (Initialize []
                          (let [state (initialize-fn this (:graphics-manager @props)) ]
                            (when state (swap! props assoc :state state))
                            (proxy-super Initialize)))

                        (LoadContent []
                          (let [p @props
                                state (load-fn this (:graphics-manager p) (:state p))]
                            (swap! props assoc :sprite-batch (new SpriteBatch (graphics-device this)))
                            (when state (swap! props assoc :state state))
                            (proxy-super LoadContent)))

                        (Update [game-time]
                          (let [props' @props]
                            (swap! props assoc :state
                                   (update-fn {:game this
                                               :delta-time (->> game-time .ElapsedGameTime)
                                               :game-time game-time
                                               :state (:state props')
                                               :graphics-manager (:graphics-manager props')}))
                            (proxy-super Update game-time)))

                        (Draw [game-time]
                          (let [props' @props]
                            (draw-fn {:game this
                                      :delta-time (->> game-time .ElapsedGameTime)
                                      :game-time game-time
                                      :state (:state props')
                                      :graphics-device (graphics-device this)})
                            (proxy-super Draw game-time))))]

    (swap! props assoc :graphics-manager (new GraphicsDeviceManager game-instance))
    (set! (->> game-instance .Content .RootDirectory )
          (Path/Combine (Directory/GetCurrentDirectory) "Content/bin/DesktopGL"))
    (.Run game-instance)))
