-- :name schema-tables
SELECT tablename
FROM pg_catalog.pg_tables
WHERE tableowner = :owner
AND tablename ~ :filter

-- :name schema-tables-details
SELECT schemaname,relname,n_live_tup
FROM pg_stat_user_tables
ORDER BY n_live_tup DESC

-- :name schema-columns
SELECT *
FROM information_schema.COLUMNS
WHERE TABLE_NAME = :table
