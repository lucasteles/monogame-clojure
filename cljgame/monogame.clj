(ns cljgame.monogame
  (:require [cljgame.interop :refer [current-exe-dir get-prop load-monogame]])
  (:import [System.IO Directory Path Directory]
           [System.Linq Enumerable]))

(load-monogame)

(import [Microsoft.Xna.Framework Game GraphicsDeviceManager Color Vector2 Rectangle GameWindow]
        [Microsoft.Xna.Framework.Graphics SpriteBatch Texture2D SpriteSortMode SpriteEffects SpriteFont]
        [Microsoft.Xna.Framework.Content ContentManager]
        [Microsoft.Xna.Framework.Media Song]
        [Microsoft.Xna.Framework.Audio SoundEffect]
        [Microsoft.Xna.Framework.Input Keyboard Keys])

(def graphics-device (fn [game] (get-prop game "GraphicsDevice")))
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
                          (let [{:keys [window graphics-manager state]} @props
                                new-state (update-fn {:game this
                                                      :game-time game-time
                                                      :delta-time (-> game-time .ElapsedGameTime .TotalSeconds)
                                                      :state state
                                                      :window window
                                                      :graphics-manager graphics-manager})]
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

(defn exit [game] (.Exit game))

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

(defn load-texture-2d [game texture-name]
  (let [content (get-prop game "Content")]
    (.Load ^ContentManager content (type-args Texture2D) texture-name)))

(defn load-sprite-font [game font-name]
  (let [content (get-prop game "Content")]
    (.Load ^ContentManager content (type-args SpriteFont) font-name)))

(defn load-sound-effect [game sound]
  (let [content (get-prop game "Content")]
    (.Load ^ContentManager content (type-args SoundEffect) sound)))

(defn sound-effect-instance [^SoundEffect sound-effect]
  (.CreateInstance sound-effect))
(defn play [sound] (.Play sound))

(defn load-song [game song]
  (let [content (get-prop game "Content")]
    (.Load ^ContentManager content (type-args Song) song)))

(defn vect
  ([n] (new Vector2 n))
  ([x y] (new Vector2 x y)))

(def vect-0 (Vector2/Zero))
(def vect-1 (Vector2/One))
(def vect-up (vect 0 -1))
(def vect-down (vect 0 1))
(def vect-right (vect 1 0))
(def vect-left (vect -1 0))

(defn vect+ [v1 v2] (Vector2/op_Addition v1 v2))
(defn vect-
  ([v] (Vector2/op_UnaryNegation v))
  ([v1 v2] (Vector2/op_Subtraction v1 v2)))

(defn vect* [^Vector2 a b] (Vector2/op_Multiply a b))
(defn vect-div [^Vector2 a b] (Vector2/op_Division a b))
(defn vect-map [^Vector2 v] { :x (.X v) :y (.Y v)})
(defn vect-with-x [^Vector2 v x] (vect x (.Y v)))
(defn vect-with-y [^Vector2 v y] (vect (.X v) y))

(defn rect
  ([^Vector2 location ^Vector2 size]
   (new Rectangle (.ToPoint location) (.ToPoint size)))
  ([^Vector2 location width height]
   (new Rectangle (.X location) (.Y location) width height))
  ([x y width height]
   (new Rectangle x y width height)))

(defn rect-size [^Rectangle r] (-> r .Size .ToVector2))
(defn rect-position [^Rectangle r] (.Location r))
(defn rect-center [^Rectangle r] (vect-div (rect-size r) 2))
(defn rect-intersects [^Rectangle r1 ^Rectangle r2] (.Intersects r1 r2))
(defn width [^GameWindow window] (-> window .ClientBounds .Width))
(defn height [^GameWindow window] (-> window .ClientBounds .Height))
(defn window-size [^GameWindow window] (let [bounds (.ClientBounds window)] (vect (.Width bounds) (.Height bounds))))
(defn tap [v] (println v) v)
(defn random [a b] (+ a (rand (- b a))))
(defn random-int [a b] (int (+ a (rand (inc (- b a))))))

(def keys-map
  {:none Keys/None :back Keys/Back :tab Keys/Tab :enter Keys/Enter :pause Keys/Pause :caps-lock Keys/CapsLock
   :kana Keys/Kana :kanji Keys/Kanji :escape Keys/Escape :ime-convert Keys/ImeConvert :ime-no-convert Keys/ImeNoConvert
   :space Keys/Space :page-up Keys/PageUp :page-down Keys/PageDown :end Keys/End :home Keys/Home
   :left Keys/Left :up Keys/Up :right Keys/Right :down Keys/Down :select Keys/Select :print Keys/Print :execute Keys/Execute
   :print-screen Keys/PrintScreen :insert Keys/Insert :delete Keys/Delete :help Keys/Help
   :d0 Keys/D0 :d1 Keys/D1 :d2 Keys/D2 :d3 Keys/D3 :d4 Keys/D4 :d5 Keys/D5 :d6 Keys/D6 :d7 Keys/D7 :d8 Keys/D8 :d9 Keys/D9
   :a Keys/A :b Keys/B :c Keys/C :d Keys/D :e Keys/E :f Keys/F :g Keys/G :h Keys/H :i Keys/I :j Keys/J :k Keys/K :l Keys/L
   :m Keys/M :n Keys/N :o Keys/O :p Keys/P :q Keys/Q :r Keys/R :s Keys/S :t Keys/T :u Keys/U :v Keys/V :w Keys/W :x Keys/X
   :y Keys/Y :z Keys/Z :left-windows Keys/LeftWindows :right-windows Keys/RightWindows :apps Keys/Apps :sleep Keys/Sleep
   :num-pad-0 Keys/NumPad0 :num-pad-1 Keys/NumPad1 :num-pad-2 Keys/NumPad2 :num-pad-3 Keys/NumPad3 :num-pad-4 Keys/NumPad4
   :num-pad-5 Keys/NumPad5 :num-pad-6 Keys/NumPad6 :num-pad-7 Keys/NumPad7 :num-pad-8 Keys/NumPad8 :num-pad-9 Keys/NumPad9
   :multiply Keys/Multiply :add Keys/Add :separator Keys/Separator :subtract Keys/Subtract :decimal Keys/Decimal :divide Keys/Divide
   :f1 Keys/F1 :f2 Keys/F2 :f3 Keys/F3 :f4 Keys/F4 :f5 Keys/F5 :f6 Keys/F6 :f7 Keys/F7 :f8 Keys/F8 :f9 Keys/F9 :f10 Keys/F10
   :f11 Keys/F11 :f12 Keys/F12 :f13 Keys/F13 :f14 Keys/F14 :f15 Keys/F15 :f16 Keys/F16 :f17 Keys/F17 :f18 Keys/F18 :f19 Keys/F19
   :f20 Keys/F20 :f21 Keys/F21 :f22 Keys/F22 :f23 Keys/F23 :f24 Keys/F24 :num-lock Keys/NumLock :scroll Keys/Scroll :left-shift Keys/LeftShift
   :right-shift Keys/RightShift :left-control Keys/LeftControl :right-control Keys/RightControl :left-alt Keys/LeftAlt :right-alt Keys/RightAlt
   :browser-back Keys/BrowserBack :browser-forward Keys/BrowserForward :browser-refresh Keys/BrowserRefresh :browser-stop Keys/BrowserStop
   :browser-search Keys/BrowserSearch :browser-favorites Keys/BrowserFavorites :browser-home Keys/BrowserHome :volume-mute Keys/VolumeMute
   :volume-down Keys/VolumeDown :volume-up Keys/VolumeUp :media-next-track Keys/MediaNextTrack :media-previous-track Keys/MediaPreviousTrack
   :media-stop Keys/MediaStop :media-play-pause Keys/MediaPlayPause :launch-mail Keys/LaunchMail :select-media Keys/SelectMedia :launch-application1 Keys/LaunchApplication1
   :launch-application2 Keys/LaunchApplication2 :oem-semicolon Keys/OemSemicolon :oem-plus Keys/OemPlus :oem-comma Keys/OemComma
   :oem-minus Keys/OemMinus :oem-period Keys/OemPeriod :oem-question Keys/OemQuestion :oem-tilde Keys/OemTilde :chat-pad-green Keys/ChatPadGreen
   :chat-pad-orange Keys/ChatPadOrange :oem-open-brackets Keys/OemOpenBrackets :oem-pipe Keys/OemPipe :oem-close-brackets Keys/OemCloseBrackets
   :oem-quotes Keys/OemQuotes :oem8 Keys/Oem8 :oem-backslash Keys/OemBackslash :processKey Keys/ProcessKey :oem-copy Keys/OemCopy :oem-auto Keys/OemAuto
   :oem-enl-w Keys/OemEnlW :attn Keys/Attn :crsel Keys/Crsel :exsel Keys/Exsel :erase-eof Keys/EraseEof :play Keys/Play :zoom Keys/Zoom :pa1 Keys/Pa1
   :oem-clear Keys/OemClear})

(def colors-map
  {:alice-blue Color/AliceBlue :antique-white Color/AntiqueWhite :aqua Color/Aqua :aquamarine Color/Aquamarine :azure Color/Azure
   :beige Color/Beige :bisque Color/Bisque :black Color/Black :blanched-almond Color/BlanchedAlmond :blue Color/Blue :blue-violet Color/BlueViolet
   :brown Color/Brown :burly-wood Color/BurlyWood :cadet-blue Color/CadetBlue :chartreuse Color/Chartreuse :chocolate Color/Chocolate :coral Color/Coral
   :cornflower-blue Color/CornflowerBlue :cornsilk Color/Cornsilk :crimson Color/Crimson :cyan Color/Cyan :dark-blue Color/DarkBlue :dark-cyan Color/DarkCyan
   :dark-goldenrod Color/DarkGoldenrod :dark-gray Color/DarkGray :dark-green Color/DarkGreen :dark-khaki Color/DarkKhaki :dark-magenta Color/DarkMagenta
   :dark-olive-green Color/DarkOliveGreen :dark-orange Color/DarkOrange :dark-orchid Color/DarkOrchid :dark-red Color/DarkRed :dark-salmon Color/DarkSalmon
   :dark-sea-green Color/DarkSeaGreen :dark-slate-blue Color/DarkSlateBlue :dark-slate-gray Color/DarkSlateGray :dark-turquoise Color/DarkTurquoise
   :dark-violet Color/DarkViolet :deep-pink Color/DeepPink :deep-sky-blue Color/DeepSkyBlue :dim-gray Color/DimGray :dodger-blue Color/DodgerBlue :firebrick Color/Firebrick
   :floral-white Color/FloralWhite :forest-green Color/ForestGreen :fuchsia Color/Fuchsia :gainsboro Color/Gainsboro :ghost-white Color/GhostWhite :gold Color/Gold
   :goldenrod Color/Goldenrod :gray Color/Gray :green Color/Green :green-yellow Color/GreenYellow :honeydew Color/Honeydew :hot-pink Color/HotPink :indian-red Color/IndianRed
   :indigo Color/Indigo :ivory Color/Ivory :khaki Color/Khaki :lavender Color/Lavender :lavender-blush Color/LavenderBlush :lawn-green Color/LawnGreen :lemon-chiffon Color/LemonChiffon
   :light-blue Color/LightBlue :light-coral Color/LightCoral :light-cyan Color/LightCyan :light-goldenrod-yell Color/LightGoldenrodYellow :light-gray Color/LightGray :light-green Color/LightGreen
   :light-pink Color/LightPink :light-salmon Color/LightSalmon :light-sea-green Color/LightSeaGreen :light-sky-blue Color/LightSkyBlue :light-slate-gray Color/LightSlateGray
   :light-steel-blue Color/LightSteelBlue :light-yellow Color/LightYellow :lime Color/Lime :lime-green Color/LimeGreen :linen Color/Linen :magenta Color/Magenta
   :maroon Color/Maroon :medium-aquamarine Color/MediumAquamarine :medium-blue Color/MediumBlue :medium-orchid Color/MediumOrchid :medium-purple Color/MediumPurple
   :medium-sea-green Color/MediumSeaGreen :medium-slate-blue Color/MediumSlateBlue :medium-spring-green Color/MediumSpringGreen :medium-turquoise Color/MediumTurquoise
   :medium-violet-red Color/MediumVioletRed :midnight-blue Color/MidnightBlue :mint-cream Color/MintCream :misty-rose Color/MistyRose :moccasin Color/Moccasin :mono-game-orange Color/MonoGameOrange
   :navajo-white Color/NavajoWhite :navy Color/Navy :old-lace Color/OldLace :olive Color/Olive :olive-drab Color/OliveDrab :orange Color/Orange :orange-red Color/OrangeRed
   :orchid Color/Orchid :pale-goldenrod Color/PaleGoldenrod :pale-green Color/PaleGreen :pale-turquoise Color/PaleTurquoise :pale-violet-red Color/PaleVioletRed :papaya-whip Color/PapayaWhip
   :peach-puff Color/PeachPuff :peru Color/Peru :pink Color/Pink :plum Color/Plum :powder-blue Color/PowderBlue :purple Color/Purple
   :red Color/Red :rosy-brown Color/RosyBrown :royal-blue Color/RoyalBlue :saddle-brown Color/SaddleBrown :salmon Color/Salmon :sandy-brown Color/SandyBrown :sea-green Color/SeaGreen
   :sea-shell Color/SeaShell :sienna Color/Sienna :silver Color/Silver :sky-blue Color/SkyBlue :slate-blue Color/SlateBlue :slate-gray Color/SlateGray :snow Color/Snow :spring-green Color/SpringGreen
   :steel-blue Color/SteelBlue :tan Color/Tan :teal Color/Teal :thistle Color/Thistle :tomato Color/Tomato :transparent Color/Transparent :transparent-black Color/TransparentBlack :turquoise Color/Turquoise
   :violet Color/Violet :wheat Color/Wheat :white Color/White :white-smoke Color/WhiteSmoke :yellow Color/Yellow :yellow-green Color/YellowGreen })

(def sprite-effects-map {
   :none SpriteEffects/None
   :flip-horizontally SpriteEffects/FlipHorizontally 
   :flip-vertically SpriteEffects/FlipVertically })

(defn- find-color [color]  
  (when color
    (if (keyword? color)
      (or (color colors-map) (throw (Exception. "INVALID COLOR")))
      color)))

(defn keyboard-state [] (Keyboard/GetState))
(defn is-key-dowm [keyboard-state key-] (.IsKeyDown keyboard-state (key- keys-map)))
(defn is-key-up [keyboard-state key-] (.IsKeyUp keyboard-state (key- keys-map)))

(defn pixel-texture [game color]
  (let [graphics (graphics-device game)
        texture (new Texture2D graphics 1 1)
        color-internal (find-color color)
        color-array (Enumerable/ToArray (type-args Color) (Enumerable/Cast (type-args Color) [color-internal]))]
    (.SetData ^Texture2D texture (type-args Color) color-array)
    texture))

(defn clear [graphics-device color]
  (.Clear graphics-device (find-color color)))

(defn draw [sprite-batch {:keys [texture position source-rectangle color rotation origin scale effects layer-depth
                                 destination-rectangle]}]
  (let [color-internal (find-color color)
        effects-internal (if (keyword? effects) (effects sprite-effects-map) effects)]
  (cond
    (and texture position source-rectangle color-internal rotation origin scale effects-internal layer-depth)
    (.Draw sprite-batch texture position source-rectangle color-internal rotation origin scale effects-internal layer-depth)

    (and texture destination-rectangle source-rectangle color-internal rotation origin effects-internal layer-depth)
    (.Draw sprite-batch texture destination-rectangle source-rectangle color-internal rotation origin effects-internal layer-depth)

    (and texture position source-rectangle color-internal)
    (.Draw sprite-batch texture position source-rectangle color-internal)

    (and texture destination-rectangle source-rectangle color-internal)
    (.Draw sprite-batch texture destination-rectangle source-rectangle color-internal)

    (and texture destination-rectangle color-internal)
    (.Draw sprite-batch texture destination-rectangle color-internal)

    (and texture position color-internal)
    (.Draw sprite-batch texture position color-internal)

    :else
    (throw (new Exception "INVALID DRAW PARAMETERS")))))

(defn draw-text [sprite-batch {:keys [sprite-font text position color
                                      rotation origin scale effects layer-depth]}]
  (let [color-internal (find-color color)
        effects-internal (if (keyword? effects) (effects sprite-effects-map) effects)]
  (cond
    (and sprite-font text position color-internal)
    (.DrawString sprite-batch sprite-font (str text) position color-internal)

    (and sprite-font text position color-internal rotation origin scale effects-internal layer-depth)
    (.DrawString sprite-batch sprite-font (str text) position color-internal rotation origin scale effects-internal layer-depth)

    :else
    (throw (new Exception "INVALID DRAW TEXT PARAMETERS")))))

