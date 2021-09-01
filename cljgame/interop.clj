(ns cljgame.interop
  (:import [System Convert]
           [System.IO Path File]
           [System.Reflection Assembly]))

(defn int32 [n] (Convert/ToInt32 n))

(def get-prop 
  (memoize (fn [obj prop-name] (-> obj .GetType .BaseType (.GetProperty prop-name) (.GetValue obj)))))

(def current-exe-dir (-> (Assembly/GetEntryAssembly) .Location Path/GetDirectoryName))

(def current "MonoGame.Framework.dll")

(def executable (Path/Combine current-exe-dir  current))

(defn load-monogame []
    (assembly-load-from
        (cond (File/Exists current) current
              (File/Exists executable) executable)))
