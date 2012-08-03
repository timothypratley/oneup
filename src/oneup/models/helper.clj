(ns oneup.models.helper)

(defn reconcile-field
  "Maps an event field to a domain entity field"
  [event entity pattern]
  (condp (partial = (pattern 0))
    :copy (let [from (pattern 1)
                to (get pattern 2 from)]
            (assoc entity to (event from)))
    :update (let [to (pattern 1)
                  f (pattern 2)
                  from (get pattern 3)]
              (update-in entity [to]
                         #(if from
                            (f (entity to) (event from))
                            (f (entity to)))))))

(defn reconcile
  "Helper function for using an event to update an entity"
  [entity event & patterns]
  (reduce (partial reconcile-field event)
          entity
          patterns))
