(ns metabase.models.dimensions
  (:require [toucan.models :as models]))

(def dimention-types
  "Possible values for `Dimensions.type`"
  #{:internal
    :external})

(models/defmodel Dimensions :metabase_dimensions)
