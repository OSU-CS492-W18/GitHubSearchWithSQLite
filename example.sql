-- CREATE TABLE savedRepos (
--   _id INTEGER PRIMARY KEY AUTOINCREMENT,
--   fullName TEXT NOT NULL,
--   description TEXT,
--   url TEXT NOT NULL,
--   stars INTEGER DEFAULT 0,
--   timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- INSERT INTO savedRepos (
--   fullName, description, url, stars
-- ) VALUES (
--   'google/android',
--   'The android OS',
--   'https:github.com/google/android',
--   3100000
-- );

-- SELECT _id, fullName FROM savedRepos WHERE fullName LIKE 'google/%';
SELECT * FROM savedRepos ORDER BY timestamp DESC;

-- UPDATE savedRepos
-- SET stars = 31001
-- WHERE fullName = 'google/material-design-icons';

-- DELETE FROM savedRepos WHERE fullName = 'google/android';
