(ns osm-eql.resolver.postgis
  (:require [com.wsscode.pathom.connect :as pc]
            [hugsql.core :as hugsql]))

(def db {:subprotocol "postgresql"
         :subname "//127.0.0.1:5432/osm"
         :user "osm"
         :password "osm"})

(hugsql/def-db-fns "postgis/schema.sql")


(defn schema-tables-tablenames [&[{:keys [filter]
                                   :or {filter ".*"}}]]
  (map :tablename (schema-tables db {:owner (:user db)
                                     :filter filter})))

(pc/defresolver res:schema-tables [env _]
  {::pc/output [{:postgis/tables [:postgis/table-name]}]}
  {:postgis/tables (map #(hash-map :postgis/table-name %)
                        (schema-tables-tablenames (-> env :ast :params)))})


;(schema-tables-details db {})

(pc/defresolver res:schema-columns [_ {:keys [postgis/table-name]}]
  {::pc/input #{:postgis/table-name}
   ::pc/output [:postgis/columns]}
  {:postgis/columns (map :column_name (schema-columns db {:table table-name}))})


(defn resolvers [] [res:schema-tables res:schema-columns])
