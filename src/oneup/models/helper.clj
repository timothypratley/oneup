(ns oneup.models.helper)

(defn reconcile-field
  "Maps an event field to a domain entity field"
  [event entity pattern]
  (condp (partial = (pattern 0))
    :copy (let [from (pattern 1)
                to (pattern 2 from)]
            (assoc entity to (event from)))
    :update (let [f (pattern 1)
                  from (pattern 2)
                  to (pattern 3 from)]
              (update-in entity [to] (f (event from))))))

(defn reconcile
  "Helper function for using an event to update an entity"
  [entity event & patterns]
  (reduce (partial reconcile-field event)
          entity
          patterns))