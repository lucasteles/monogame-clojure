(ns cljgame.interop
  (:import [System Convert]
           [System.IO Path File]
           [System.Reflection Assembly]))

(defn int32 [n] (Convert/ToInt32 n))

(def get-prop
  (memoize (fn [obj prop-name] (-> obj .GetType .BaseType (.GetProperty prop-name) (.GetValue obj)))))

(def current-exe-dir (-> (Assembly/GetEntryAssembly) .Location Path/GetDirectoryName))

(defn load-dll [assembly]
  (let [executable (Path/Combine current-exe-dir  assembly)]
    (assembly-load-from
      (cond (File/Exists assembly) assembly
            (File/Exists executable) executable))))

(defn load-monogame []
  (load-dll "MonoGame.Framework.dll")
  (load-dll "Aether.Physics2D.dll"))

