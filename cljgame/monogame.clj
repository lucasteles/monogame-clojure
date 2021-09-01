(ns cljgame.monogame
  (:require [cljgame.interop :refer [current-exe-dir get-prop load-monogame]])
  (:import [System.IO Directory Path Directory]))

(load-monogame)

(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color Vector2]
        [Microsoft.Xna.Framework.Graphics SpriteBatch Texture2D SpriteSortMode]
        [Microsoft.Xna.Framework.Content ContentManager])

(def graphics-device (fn [game] (get-prop game "GraphicsDevice")))

(defn load-texture-2d [game texture-name]
  (let [content (get-prop game "Content")]
    (.Load ^ContentManager content (type-args Texture2D) texture-name)))

(defn vect
  ([n] (new Vector2 n))
  ([x y] (new Vector2 x y)))

(defn vect+ [v1 v2] (Vector2/op_Addition v1 v2))
(defn vect- [v1 v2] (Vector2/op_Addition v1 v2))
(defn vect* [a b] (Vector2/op_Multiply a b))
(defn vect-div [a b] (Vector2/op_Division a b))


(def debug-content-path (Path/Combine (Directory/GetCurrentDirectory) "Content/bin/DesktopGL"))
(def exe-content-path (Path/Combine current-exe-dir "Content"))

(defn run [load-fn initialize-fn update-fn draw-fn]
  (let [props (atom {:state {}})
        game-instance (proxy
                        ;; first vector contains superclass and interfaces that the created class should extend/implement
                        ;; second vector contains arguments to superclass constructor
                        [Game] []

                        ;; below are all overriden/implemented methods
                        (Initialize []
                          (let [state (initialize-fn this @props)]
                            (when state (swap! props assoc :state state))
                            (proxy-super Initialize)))

                        (LoadContent []
                          (let [p @props
                                state (load-fn this @props)]
                            (swap! props assoc :sprite-batch (new SpriteBatch (graphics-device this)))
                            (when state (swap! props assoc :state state))
                            (proxy-super LoadContent)))

                        (Update [game-time]
                          (let [props' @props
                                state (:state props')
                                new-state (update-fn {:game this
                                                      :game-time game-time
                                                      :delta-time (-> game-time .ElapsedGameTime .TotalSeconds)
                                                      :state state
                                                      :window (:window props')
                                                      :graphics-manager (:graphics-manager props')})]
                            (when (not (identical? state new-state))
                              (swap! props assoc :state new-state))
                            (proxy-super Update game-time)))

                        (Draw [game-time]
                          (let [props' @props]
                            (draw-fn {:game this
                                      :delta-time (-> game-time .ElapsedGameTime .TotalSeconds)
                                      :game-time game-time
                                      :state (:state props')
                                      :sprite-batch (:sprite-batch props')
                                      :graphics-device (graphics-device this)
                                      :window (:window props')})
                            (proxy-super Draw game-time))))]

    (swap! props assoc :graphics-manager (new GraphicsDeviceManager game-instance))
    (swap! props assoc :window (get-prop game-instance "Window" ))
    (set! (->> game-instance .Content .RootDirectory )
          (if (Directory/Exists exe-content-path) exe-content-path debug-content-path))
    (.Run game-instance)))

(defn clear [graphics-device color]
  (.Clear graphics-device Color/LightGray))

(defn begin [sprite-batch &{:keys [sort-mode blend-state sampler-state depthStencil-state rasterizer-state effect transform-matrix]}]
  (.Begin sprite-batch
          (or sort-mode SpriteSortMode/Deferred)
          blend-state
          sampler-state
          depthStencil-state
          rasterizer-state
          effect
          transform-matrix))

(defn end [sprite-batch] (.End sprite-batch))


(defn draw [sprite-batch {:keys [texture position source-rectangle color rotation origin scale effects layer-depth
                                 destination-rectangle]}]
  (cond
    (and texture position source-rectangle color rotation origin scale effects layer-depth)
    (.Draw sprite-batch texture position source-rectangle color rotation origin scale effects layer-depth)

    (and  texture destination-rectangle source-rectangle color rotation origin effects layer-depth)
    (.Draw sprite-batch texture destination-rectangle source-rectangle color rotation origin effects layer-depth)

    (and texture position source-rectangle color)
    (.Draw sprite-batch texture position source-rectangle color)

    (and texture destination-rectangle source-rectangle color)
    (.Draw sprite-batch texture destination-rectangle source-rectangle color)

    (and texture position color)
    (.Draw sprite-batch texture position color)))

