(ns oneup.models.helper)

(def max-rank 5)
(def booty 10)

(defn reconcile-field
  "Maps an event field to a domain entity field"
  [event entity pattern]
  (condp = (pattern 0)
    :copy (let [from (pattern 1)
                to (get pattern 2 from)]
            (assoc entity to (event from)))
    :update (let [to (pattern 1)
                  f (get pattern 2)
                  from (get pattern 3)]
              (update-in entity [to]
                         #(if from
                            (f % (event from))
                            (f %))))))

(defn reconcile
  "Helper function for using an event to update an entity"
  [entity event & patterns]
  (reduce (partial reconcile-field event)
          entity
          patterns))
