(ns oneup.models.helper)

;TODO: how can I make this a checked DSL?
(defn reconcile-field
  "Maps an event field to a domain entity field"
  [event entity pattern]
  (condp = (pattern 0)
    :set (let [to (pattern 1)
               value (pattern 2)]
           (assoc entity to value))
    :setf (let [to (pattern 1)
                f (pattern 2)]
            (assoc entity to
                   (apply f (map event (drop 3 pattern)))))
    :copy (let [from (pattern 1)
                to (get pattern 2 from)]
            (println "ENTITY " entity)
            (assoc entity to (event from)))
    :update (let [to (pattern 1)
                  f (pattern 2)]
              (apply update-in entity [to]
                     f (map event (drop 3 pattern))))))

(defn reconcile
  "Helper function for using an event to update an entity"
  [entity event & patterns]
  (reduce (partial reconcile-field event)
          entity
          patterns))

