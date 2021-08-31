(ns jogo 
  (:require [interop :refer :all])
  (:import [System.IO Directory Path])
  (:gen-class))

(assembly-load-from "MonoGame.dll")
(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color])
(import [Microsoft.Xna.Framework.Graphics SpriteBatch Texture2D])

; (assembly-load-from "test.dll")
; (import [test Class1])

(defn graphics-device [game]
  (get-prop game "GraphicsDevice"))

(defn load-texture-2d [game texture-name]
  (let [content (get-prop game "Content")]
    #_ (.Load content (type-args Texture2D) texture-name)
    (generic-method content "Load" |Texture2D| texture-name)))

; (def x (new Class1))
; (generic-method x "Foo" |String|)

(defn initialize [game graphics]
  (set! (.IsMouseVisible game) true)
  (set! (.PreferredBackBufferWidth graphics) (int32 1024))
  (set! (.PreferredBackBufferHeight graphics) (int32 768))
  (.ApplyChanges graphics))

(defn load-content [game graphics state]
  { :texture/logo (load-texture-2d game "logo") })

(defn tick [{:keys [game game-time state]}]
  (println "Update..." (->> game-time .TotalGameTime .Milliseconds))
  state)

(defn draw [{:keys [game sprite-batch delta-time state]}]
  (.Clear (graphics-device game) Color/LightGray))


(defn run-game [load-fn initialize-fn update-fn draw-fn]
  (let [props (atom {:state {}})
        game-instance (proxy
                        ;; first vector contains superclass and interfaces that the created class should extend/implement
                        ;; second vector contains arguments to superclass constructor
                        [Game] []

                        ;; below are all overriden/implemented methods
                        (Initialize []
                          (let [state (initialize-fn this (:graphics @props)) ]
                            (when state (swap! props assoc :state state))
                            (proxy-super Initialize)))

                        (LoadContent []
                          (let [p @props
                                state (load-fn this (:graphics p) (:state p))]
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
                                               :graphics (:graphics props')}))
                            (proxy-super Update game-time)))

                        (Draw [game-time]
                          (let [props' @props]
                            (draw-fn {:game this
                                      :delta-time (->> game-time .ElapsedGameTime)
                                      :game-time game-time
                                      :state (:state props')})
                            (proxy-super Draw game-time))))]

    (swap! props assoc :graphics (new GraphicsDeviceManager game-instance))
    (set! (->> game-instance .Content .RootDirectory )
          (Path/Combine (Directory/GetCurrentDirectory) "Content/bin/DesktopGL"))
    (.Run game-instance)))

(Console/WriteLine "Ola Delboni")
(run-game load-content initialize tick draw)


