(ns cljgame.physics
  (:require [cljgame.interop :refer [load-monogame]])
  (:import [System Math]))

(load-monogame)

(import [tainicom.Aether.Physics2D.Dynamics World BodyType]
        [Microsoft.Xna.Framework Color Vector2 Rectangle])

(def pixels-per-meter 100)

(defn create-world []
  (new World (new Vector2 0 10)))

(defn step [world delta-time]
  (.Step world delta-time))

(def ^:private body-type-map
  {:static BodyType/Static
   :kinematic BodyType/Kinematic
   :dynamic BodyType/Dynamic })

(defn pixels->meters [n] (/ n pixels-per-meter))
(defn meters->pixels [n] (* n pixels-per-meter))

(defn vect-pixels->meters [n] (Vector2/op_Division n pixels-per-meter))
(defn vect-meters->pixels [n] (Vector2/op_Multiply n pixels-per-meter))

(defn create-body
  [^World world
   ^BodyType body-type
   ^Rectangle rectangle
   &[tag rotation]]
  (let [internal-body-type (or (body-type body-type-map) (throw (Exception. "INVALID BODY TYPE")) )
        width (-> rectangle .Width pixels->meters)
        height (-> rectangle .Height pixels->meters)
        x (pixels->meters (.X rectangle))
        y (pixels->meters (.Y rectangle))
        body (.CreateRectangle world width height 
                               1  ; density
                               (Vector2. x y)
                               (or rotation 0) ; rotation
                               internal-body-type)]
    (-> body .Mass (set! 1))
    (-> body .Tag (set! tag))
    (-> body (.SetRestitution 0))
    (-> body (.SetFriction 0)) 
    body))

(defn velocity [body] (-> body .LinearVelocity vect-meters->pixels))
(defn position [body] (-> body .Position vect-meters->pixels))
(defn rotation [body] (-> body .Rotation))


